from threading import Thread
import cv2

class NavigationStream
    def __init__(self, address=(HOST, PORT)):

        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.bind(address)

        inputs = [sock]
        outputs = []

        self.data = None
        
        # initialize the variable used to indicate if the thread should
        # be stopped
        self.stopped = False
        
    def start(self):
        # start the thread to read frames from the video stream
        Thread(target=self.update, args=()).start()
        return self
    
    def update(self):
        # keep looping infinitely until the thread is stopped
        while True:
            # if the thread indicator variable is set, stop the thread
            if self.stopped:
                return
            
            # otherwise, read the next frame from the stream
            readable, _, _ = select.select(inputs, outputs, inputs, 0.25)

            try:
                if readable:
                        data, rioAddress = sock.recvfrom(65536)
                else:
                        sock.sendto("registerNav")
                        data = None
                        rioAddress = None

            except Exception:
                msg = None
                address = None

            
    def read(self):
        # return the frame most recently read
        return self.data
    
    def stop(self):
        # indicate that the thread should be stopped
        self.stopped = True
