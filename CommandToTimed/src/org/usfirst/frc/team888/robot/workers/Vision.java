package org.usfirst.frc.team888.robot.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.usfirst.frc.team888.robot.RobotMap;

/**
 *
 */
public class Vision {

	protected InetAddress jetsonAddress;

	protected DatagramSocket sock;
	protected DatagramPacket message;

	protected int cycle = 0;
	protected byte[] byteCameraMessage = ByteBuffer.allocate(4).putInt(cycle).array();

	public Vision() {
		try {
			jetsonAddress = InetAddress.getByAddress(RobotMap.IP_ADDRESS);
			sock = new DatagramSocket(RobotMap.RIO_UDP_PORT);
			message = new DatagramPacket(byteCameraMessage, 
					byteCameraMessage.length, jetsonAddress, RobotMap.JETSON_UDP_PORT);
		} catch (Exception e) {}
	}
	
	/**
	 * Sends a request to the Jetson for a cube location
	 * @param cycle The cycle the message was sent
	 */
	public void sendMessage(int cycle) {
		byteCameraMessage = ByteBuffer.allocate(4).putInt(cycle).array();
		
		try {
			message.setData(byteCameraMessage);
			sock.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}