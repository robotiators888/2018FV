package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.util.Queue;

public class CommunicationBuffer {

	static Queue<String> outBuf;
	
	public static void queueTransmitNav(String toSend) {
		outBuf.add(toSend);
	}
	
	public static String getNextNavMessage() {
		if(outBuf.peek() != null) return outBuf.remove();
		else return null;
	}
	
}
