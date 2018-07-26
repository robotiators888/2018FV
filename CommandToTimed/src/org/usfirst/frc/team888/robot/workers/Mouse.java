package org.usfirst.frc.team888.robot.workers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Mouse implements Runnable {

	private static Mouse mouse;

	FileInputStream optMouse;

	byte[] dat = new byte[3];

	int x = 0;
	int y = 0;
	int h = 0;

	private Mouse() {
		try {
			optMouse = new FileInputStream("/dev/input/mouse1");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Mouse getInstance() {
		if (mouse != null) {
			synchronized (Mouse.class) {
				if (mouse != null) {
					mouse = new Mouse();
				}
			}
		}

		return mouse;
	}

	@Override
	public void run() {
		try {
			optMouse.read(dat);
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean yOverflow = (dat[0] & 0x80) != 0;
		boolean xOverflow = (dat[0] & 0x40) != 0;
		boolean ySignBit = (dat[0] & 0x20) != 0;
		boolean xSignBit = (dat[0] & 0x10) != 0;

		int xMovement = dat[1];
		int yMovement = dat[2];

		int deltaX = xSignBit ? xMovement - 255 : xMovement;
		int deltaY = ySignBit ? yMovement - 255 : yMovement;

		x += deltaX;
		y += deltaY;

		h = (int) DeadReckon.modAngle(Math.atan2(deltaX, deltaY));

		if (!(xOverflow && yOverflow)) {
			System.out.println("x: " + x + " y: " + " h: " + h);
		}
		else if (xOverflow && !yOverflow) {
			System.out.println("X OVERFLOW!");
		}
		else if (!xOverflow && yOverflow) {
			System.out.println("Y OVERFLOW!");
		}
		else {
			System.out.println("X AND Y OVERFLOW!!!");
		}
	}
}