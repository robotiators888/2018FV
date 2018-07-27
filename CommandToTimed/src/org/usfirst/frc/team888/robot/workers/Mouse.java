package org.usfirst.frc.team888.robot.workers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Mouse {

	private static Mouse mouse;

	FileInputStream optMouse;

	BufferedWriter bw;
	File mouseData;
	FileOutputStream fos;
	
	String fileName = "/c/mouseData" + System.currentTimeMillis();


	byte[] dat = new byte[3];

	int x = 0;
	int y = 0;
	int h = 0;

	private Mouse() {
		try {
			optMouse = new FileInputStream("/dev/input/mouse0");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Mouse getInstance() {
		if (mouse == null) {
			synchronized(Mouse.class) {
				if (mouse == null) {
					mouse = new Mouse();
				}
			}
		}

		return mouse;
	}

	public void mouse() {
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


		String log;

		if (!(xOverflow && yOverflow)) {
			log = ("x: " + x + " y: " + " h: " + h);
		}
		else if (xOverflow && !yOverflow) {
			log = ("X OVERFLOW!");
		}
		else if (!xOverflow && yOverflow) {
			log = ("Y OVERFLOW!");
		}
		else {
			log = ("X AND Y OVERFLOW!!!");
		}

		mouseData = new File(fileName);

		try {
			fos = new FileOutputStream(mouseData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bw = new BufferedWriter(new OutputStreamWriter(fos));

		try {
			bw.append(log);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}