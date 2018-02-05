package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/**
 * Opens a UDP socket, and copies any data received into internal memory.
 */
public class UDPHandler extends Thread {
	
	DatagramSocket socket = null;
	boolean open = false, r;
	byte[] buf = new byte[65536];
	List<InetAddress> target = new ArrayList<InetAddress>(0);
	List<Integer> target_port = new ArrayList<Integer>(0);
	
	/**
	 * Opens the socket.
	 */
	public UDPHandler() {
		try {
			socket = new DatagramSocket(4445);
			socket.setSoTimeout(50);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		r = (socket != null);
	}
	
	/**
	 * Begins running collections, waiting for a packet then checking it for a registration value.
	 * Once that happens, it begins sending any nav message placed into CommunicationBuffer.
	 */
	public void run() {
		while(r) {
			DatagramPacket Ipacket = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(Ipacket);
				String t = (new String(Ipacket.getData(), 0, Ipacket.getLength()));
				if(t.equals("registerNav")) {
					target.add(Ipacket.getAddress());
					target_port.add(Integer.valueOf(Ipacket.getPort()));
					open = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(open && target.get(0) != null) {
				buf = DatatypeConverter.parseHexBinary(toHex(CommunicationBuffer.getNextNavMessage()));
				if(buf != null) {
					for(int i = 0; i < target.size(); i++) {
						DatagramPacket packet = new DatagramPacket(buf, buf.length, target.get(i), target_port.get(i).intValue());
						try {
							socket.send(packet);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
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
	
	private String toHex(String arg) {
		return String.format("%040x", new BigInteger(1, arg.getBytes()));
	}
	
}
