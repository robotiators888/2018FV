package org.usfirst.frc.team888.robot.subsystems;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CommunicationsBuffer {

	static ArrayList<BufferData> buf = new ArrayList<>(5);

	public static synchronized void addData(byte[] data) {
		buf.add(new BufferData(data));
	}

	public static synchronized Number[] getHighestCycle() {
		if(buf.isEmpty()) return null;
		BufferData b = buf.stream()
				.max((b1, b2) -> Integer.compare(b1.cycle, b2.cycle)).orElse(null);
		buf.clear();
		return new Number[] {b.cycle, b.x, b.y};
	}

}

class BufferData {

	public int cycle;
	public double x;
	public double y;

	public BufferData(byte[] data) {
		ByteBuffer bbuf = ByteBuffer.wrap(data);
		cycle = bbuf.getInt(0);
		x = bbuf.getDouble(4);
		y = bbuf.getDouble(12);
	}

}
