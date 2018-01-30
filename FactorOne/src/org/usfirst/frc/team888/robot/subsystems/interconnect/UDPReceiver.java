package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Opens a UDP socket, and copies any data received into internal memory.
 */
public class UDPReceiver extends Thread {
	
	DatagramSocket socket;
	boolean running;
	byte[] buf = new byte[1024];
	String received;
	
	/**
	 * Opens the socket.
	 */
	public UDPReceiver() {
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Begins running collections, waiting for a packet then copying it into internal memory.
	 */
	public void run() {
		running = true;
		
		while(running) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				received = new String(packet.getData(), 0, packet.getLength());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return The latest data packet's data as a String.
	 */
	public String getReceived() {
		return received;
	}
	
	/**
	 * @return The latest data packet's data as a JSONObject.
	 */
	public JSONObject getReceivedAsJSON() {
		try {
			return new JSONObject(received);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Closes the socket manually.
	 */
	public void close() {
		running = false;
		socket.close();
	}
	
}
