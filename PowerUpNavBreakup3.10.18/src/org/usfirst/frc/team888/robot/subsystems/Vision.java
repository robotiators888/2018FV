package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Vision extends Subsystem {

	//Instantiates camera objects
	protected boolean previousCameraButtonState = false;
	protected InetAddress cameraAddress;

	protected DatagramSocket sock;
	protected DatagramPacket message;

	protected String cameraMessage = "frontCamera";
	protected byte[] byteCameraMessage = cameraMessage.getBytes();

	public Vision() {
		//Declares objects for RIO-PI communication
		try {
			cameraAddress = InetAddress.getByAddress(RobotMap.IP_ADDRESS);
			sock = new DatagramSocket(RobotMap.RIO_UDP_PORT);
			message = new DatagramPacket(byteCameraMessage, 
					byteCameraMessage.length, cameraAddress, RobotMap.PI_UDP_PORT);
		} catch (Exception e) {}
	}

	/**
	 * Switches which camera is being sent to the display
	 */
	public void switchCamera() {
		if(cameraMessage.equals("frontCamera")) {
			cameraMessage = "backCamera";
			byteCameraMessage = cameraMessage.getBytes();
		} else {
			cameraMessage = "frontCamera";
			byteCameraMessage = cameraMessage.getBytes();
		}

		try {
			message.setData(byteCameraMessage);
			sock.send(message);
			SmartDashboard.putString("sent camera message", cameraMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}