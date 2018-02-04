package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.util.LinkedList;
import java.util.Queue;

public class CommunicationBuffer {

	static Queue<String> outBuf = new LinkedList<String>();
	
	public static void queueTransmitNav(String toSend) {
		outBuf.add(toSend);
	}
	
	public static String getNextNavMessage() {
		if(outBuf.peek() != null) return outBuf.remove();
		else return null;
	}
	
	public static void flushNav() {
		while(outBuf.peek() != null) outBuf.remove();
	}
	
}
