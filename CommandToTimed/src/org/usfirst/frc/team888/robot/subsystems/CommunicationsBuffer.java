package org.usfirst.frc.team888.robot.subsystems;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CommunicationsBuffer {

	static ArrayList<BufferData> buf = new ArrayList<>(5);

	public static synchronized void addData(byte[] data) {
		buf.add(new BufferData(data));
	}

	public static synchronized double[] getHighestCycle() {
		if(buf.isEmpty()) return null;
		BufferData b = buf.stream()
				.max((b1, b2) -> Integer.compare(b1.cycle, b2.cycle)).orElse(null);
		buf.clear();
		return new double[] {b.cycle, b.x, b.y};
	}

}

class BufferData {

	public int cycle;
	public double x;
	public double y;

	public BufferData(byte[] data) {
		ByteBuffer bbuf = ByteBuffer.wrap(data);
		cycle = bbuf.getInt();
		x = Double.valueOf(bbuf.getFloat());
		y = Double.valueOf(bbuf.getFloat());
		System.out.println(x);
		System.out.println(y);
	}

}