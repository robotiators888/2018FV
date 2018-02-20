#!/usr/bin/env python

# import the necessary packages
import argparse

try:
    import cPickle as pickle
except:
    import pickle

import cv2
import select
import socket
import struct
import sys
import time

import numpy as np
import Tkinter as tk
from PIL import Image, ImageTk
import threading


# define frame fragment maximum send size
maxFragmentSize = 61440

# defines server address to work on all TCP stacks
HOST    = "0.0.0.0"
PORT    = 9999
ADDRESS = (HOST, PORT)

# defines camera frame provider address
cameraHost    = "10.8.88.12"
cameraPort    = 8888
cameraAddress = (cameraHost, cameraPort)


# create a UDP socket endpoint
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# attaches socket to server address
sock.bind(ADDRESS)

inputs = [sock]
outputs = []

fragmentList = []

state = 'idle'

cameraMessage = "frontCamera"

sock.sendto(cameraMessage, cameraAddress)

# set up GUI
root = tk.Tk() #makes main window
root.wm_title("camera feed")
root.config(background="#FFFFFF")

# graphics window
imageFrame = tk.Frame(root, width=600, height=500)
imageFrame.grid(row=0, column=0, padx=10, pady=2)

lmain = tk.Label(imageFrame)
lmain.grid(row=0, column=0)

def switchToBack():
    print "back"
    cameraMessage = "backCamera"
    sock.sendto(cameraMessage, cameraAddress)

def switchToFront():
    print "front"
    cameraMessage = "frontCamera"
    sock.sendto(cameraMessage, cameraAddress)

b = tk.Button(root, text="FRONT", command=switchToFront)
b.grid(row=1, column=0)

b = tk.Button(root, text="BACK", command=switchToBack)
b.grid(row=1, column=1)

def show():
  state = 'idle'
  
  # keep looping
  while True:

    readable, _, _ = select.select(inputs, outputs, inputs, 0.25)
	
    # try to catch all exceptions from socket reads
    try:
	    if readable:
		    # attempt to read message from socket
		    #msg, address = sock.recvfrom(65507)
		    msg, address = sock.recvfrom(65536)
	    else: 
		    sock.sendto(cameraMessage, cameraAddress)
		    msg = None
		    address = None

    except Exception:

    #    # set received fields to none when exception received
    #    # (an exception is generated each time one attempts to read from a socket and nothing is there)
        msg = None
        address = None

    # if a message was received...
    if msg and (len(msg) >= 8):

        # process current message.

        msgId, msgLength = struct.unpack('!2I', msg[0:8])

        
        # If this is message 10....
        if msgId == 10:

            # process message 10
            
            fragmentSize = msgLength - 8

            fragment, = struct.unpack("!8x{0}s".format(fragmentSize), msg)

            #flattenedFrame = fragment
            fragmentList = [fragment]
            
            state = 'loading'
        

        # If this is message 11....
        if msgId == 11:

            if state == 'loading':

                # process message 11

                fragmentSize = msgLength - 16

                fragmentNumber, \
                last, \
                fragment = \
                    struct.unpack("!8x2I{0}s".format(fragmentSize), msg)

                #flattenedFrame += fragment

                fragmentList.append(fragment)

                if last == 1:
                    state = 'show'

    if state == 'show':
        
        try:
            flattenedFrame = ''.join(fragmentList)
            
            imgencode = pickle.loads(flattenedFrame)
            frame = cv2.imdecode(imgencode, 1)

            cv2image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGBA)
            img = Image.fromarray(cv2image)
            imgtk = ImageTk.PhotoImage(image=img)

            lmain.imgtk = imgtk
            lmain.configure(image=imgtk)
            
            #cv2.imshow("Frame", frame)
            #cv2.waitKey(100)
        except Exception:
            None
            
        state = 'idle'


threading.Thread(target=show).start()

root.mainloop()


