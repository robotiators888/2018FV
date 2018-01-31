package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Opens a UDP socket, and copies any data received into internal memory.
 */
public class UDPHandler extends Thread {
	
	DatagramSocket socket;
	boolean open;
	byte[] buf = new byte[1024];
	InetAddress RPi0;
	int RPi0_port;
	
	/**
	 * Opens the socket.
	 */
	public UDPHandler() {
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Begins running collections, waiting for a packet then checking it for a registration value.
	 * Once that happens, it begins sending any nav message placed into CommunicationBuffer.
	 */
	public void run() {
		boolean initWait = true;
		
		while(initWait) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				String t = (new String(packet.getData(), 0, packet.getLength()));
				if(t.equals("registerNav")) {
					initWait = false;
					RPi0 = packet.getAddress();
					RPi0_port = packet.getPort();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		open = true;
		
		while(open) {
			buf = CommunicationBuffer.getNextNavMessage().getBytes();
			if(buf != null) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length, RPi0, RPi0_port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Closes the socket manually.
	 */
	public void close() {
		open = false;
		socket.close();
	}
	
}
