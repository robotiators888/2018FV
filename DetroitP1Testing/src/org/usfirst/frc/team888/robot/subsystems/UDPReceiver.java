package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPReceiver extends Thread {
	
	DatagramSocket socket;
	DatagramPacket dat;
	byte[] receiveData = new byte[20];
	
	private void init() {
		try {
			socket = new DatagramSocket(888);
			dat = new DatagramPacket(receiveData, receiveData.length);
		} catch (SocketException e) {

		}
	}
	
	@Override
	public void run() {
		init();
		
		do {
			try {
				socket.receive(dat);
				CommunicationsBuffer.addData(dat.getData());
			} catch (IOException e) {
				
			}
		} while(true);
	}
}
