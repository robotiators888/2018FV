package org.usfirst.frc.team888.robot.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Looks for raw UDP messages
 */
public class UDPReceiver extends Thread {

    // Instantiates the objects for receiving data
    DatagramSocket socket;
    DatagramPacket dat;
    byte[] receiveData = new byte[20];

    /**
     * Declares the socket and empty message
     */
    private void init() {
        try {
            socket = new DatagramSocket(888);
            dat = new DatagramPacket(receiveData, receiveData.length);
        } catch (SocketException e) {

        }
    }

    /**
     * Looks for messages sent from the Jetson
     */
    @Override
    public void run() {
        init();

        do {
            try {
                socket.receive(dat);
                CommunicationsBuffer.addData(dat.getData());
            } catch (IOException e) {

            }
        } while (true);
    }
}