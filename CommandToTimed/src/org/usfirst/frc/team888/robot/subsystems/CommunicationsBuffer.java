package org.usfirst.frc.team888.robot.subsystems;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Used for processing UDP messages from the Jetson
 */
public class CommunicationsBuffer {

	static ArrayList<BufferData> buf = new ArrayList<>(5);

	/**
	 * Adds data to the data buffer
	 * @param data The byte array message
	 */
	public static synchronized void addData(byte[] data) {
		buf.add(new BufferData(data));
	}

	/**
	 * Compares the messages to see which one contains the most recent data
	 * @return The most recent message
	 */
	public static synchronized double[] getHighestCycle() {
		if(buf.isEmpty()) return null;
		BufferData b = buf.stream()
				.max((b1, b2) -> Integer.compare(b1.cycle, b2.cycle)).orElse(null);
		buf.clear();
		return new double[] {b.cycle, b.x, b.y};
	}

}

/**
 * Reads the byte array message and converts readable data
 */
class BufferData {

	public int cycle;
	public double x;
	public double y;

	/**
	 * Processes the data
	 * @param data The byte array message
	 */
	public BufferData(byte[] data) {
		ByteBuffer bbuf = ByteBuffer.wrap(data);
		cycle = bbuf.getInt();
		x = Double.valueOf(bbuf.getFloat());
		y = Double.valueOf(bbuf.getFloat());
		System.out.println(x);
		System.out.println(y);
	}

}