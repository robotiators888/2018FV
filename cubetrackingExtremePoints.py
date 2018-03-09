#!/usr/bin/env python

# import the necessary packages
from collections import deque
import numpy as np
import argparse
import cv2
import imutils
from imutils.video import WebcamVideoStream
from NavigationStream import NavigationStream


# defines server address for raspberry pi
HOST = "0.0.0.0"
PORT = 5555

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(address)

inputs = [sock]
outputs = []

# camera calibration
pixelHeight = 480
pixelWidth = 640


# define the lower and upper boundaries of the yellow
# cube in the HSV color space, then initialize the
# list of tracked points
yellowLower = (0, 80, 120)
yellowUpper = (50, 201, 180)


# start nav data feed
nav = NavigationStream(address=(HOST, PORT).start()

# start camera feed
camera = WebcamVideoStream(src=0).start()


# keep looping
while True:

    # grab the current nav data
    data = nav.read()
                
    # grab the current frame
    frame = camera.read()
	

    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    # construct a mask for the color "green", then perform
    # a series of dilations and erosions to remove any small
    # blobs left in the mask
    mask = cv2.inRange(hsv, yellowLower, yellowUpper)
    mask = cv2.erode(mask, None, iterations=2)
    mask = cv2.dilate(mask, None, iterations=2)

    # find contours in the mask and initialize the current
    # (x, y) center of the ball
    #cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]
    cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if imutils.is_cv2() else cnts[1]
    center = None

    # only proceed if at least one contour was found
    if len(cnts) > 0:
        # find the largest contour in the mask, then use
        # it to compute the minimum enclosing circle and
        # centroid
        c = max(cnts, key=cv2.contourArea)
		x, y, w, h = cv2.boundingRect(c)
		((a, b), radius) = cv2.minEnclosingCircle(c)
		M = cv2.moments(c)
		center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))

        # finding the extreme values
        extLeft = tuple(c[c[:, :, 0].argmin()][0])
        extRight = tuple(c[c[:, :, 0].argmax()][0])
        extTop = tuple(c[c[:, :, 1].argmin()][0])
        extBot = tuple(c[c[:, :, 1].argmax()][0])

	# only proceed if the radius meets a minimum size
	if radius > 10:
		    # img, top left, bottom right, color, line thickness
		    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 255), 2)
		    cv2.circle(frame, (int(a), int(b)), int(radius), (0, 255, 255), 2)
			
		    cv2.circle(frame, center, 5, (0, 255, 255), -1)  #yellow center point
		    cv2.circle(frame, extLeft, 5, (0, 0, 255), -1)  #red left point
		    cv2.circle(frame, extRight, 5, (0, 255, 0), -1)  #green right point
		    cv2.circle(frame, extTop, 5, (255, 0, 0), -1)  #blue top point
		    cv2.circle(frame, extBot, 5, (255, 255, 0), -1)  #teal bottom point
			
		    #cv2.drawContours(frame, cnts, -1, (0,255,0), 3)

		    cubeX = 


    # show the frame to our screen
    #cv2.imshow("Frame", frame)
    cv2.imshow("Frame", frame)
    key = cv2.waitKey(1) & 0xFF

    # if the 'q' key is pressed, stop the loop
    if key == ord("q"):
	    break

# cleanup the camera and close any open windows
camera.release()
cv2.destroyAllWindows()
