package org.usfirst.frc.team888.robot.subsystems.interconnect;

import java.nio.ByteBuffer;

public class CommunicationBuffer {

	static byte[] outBuf;
	
	public static void setTransmitNav(double x, double y, double z, double heading, double pitch, double roll) {
		double[] vals = {x, y, z, heading, pitch, roll};
		
		byte[][] bWork = new byte[6][8];
		for(int i = 0; i < bWork.length; i++) ByteBuffer.wrap(bWork[i]).putDouble(vals[i]);
		
		byte[] bFinal = new byte[84];
		byte[] bMID = new byte[4], bML = new byte[4];
		ByteBuffer.wrap(bMID).putInt(20);
		for(int i = 0; i < 4; i++) bFinal[i] = bMID[i];
		ByteBuffer.wrap(bML).putInt(76);
		for(int i = 0; i < 4; i++) bFinal[i + 4] = bML[i];
		for(int i = 0; i < bWork.length; i++) {
			for(int j = 0; j < 8; j++) {
				bFinal[j + ((i * 8) + 8)] = bWork[i][j];
			}
		}
		
		outBuf = bFinal;
	}
	
	public static byte[] getNextNavMessage() {
		return outBuf;
	}

}
