package org.usfirst.frc.team888.robot.subsystems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DeadReckon {

	// Instantiates drive train object
	protected DriveTrain drive;

	// Instantiates values for calculating location and speed
	protected double angle;
	protected double changeInDistance;
	protected double changeInEncoderLeft;
	protected double changeInEncoderRight;
	protected double changeInHeading;
	protected double changeInX;
	protected double changeInY;
	protected double clickPosX;
	protected double clickPosY;
	protected int cycle;
	protected String direction;
	protected double encoderLeftValue;
	protected double encoderRightValue;
	protected double heading;
	protected double lastChangeInEncoderLeft;
	protected double lastChangeInEncoderRight;
	protected double lastEncoderLeft;
	protected double lastEncoderRight;
	protected double lastTime;
	protected double PORtoLeft;
	protected double PORtoRight;
	protected double posX;
	protected double posY;
	protected double speed;
	protected double time;
	protected double timePassed;
	protected boolean calibrated;

	//Instantiates objects for logging
	BufferedWriter bw;
	File navData;
	FileOutputStream fos;

	//Instantiates boolean to tell whether or not the log file has been opened
	protected boolean fileOpened = false;

	//Instantiates logging values
	protected ArrayList<double[]> navLog = new ArrayList<>(9000);

	public DeadReckon(DriveTrain p_drive) {
		// Declares the drive object to be equal to the object passed in by Robot
		drive = p_drive;

		// Resets the encoder values
		reset();
	}

	/**
	 * Calculates the location of the robot
	 */
	public void updateTracker() {
		// Calls the method to get the most recent encoder data
		updateEncoderVals();

		/* Calculates the change in the encoders since the last time the method was called.
		 * If the change in the encoders is impossibly large, the change is ignored and
		 * the previous change is used instead.
		 */
		changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
		if (changeInEncoderLeft > 3000 || changeInEncoderLeft < -3000) {
			changeInEncoderLeft = lastChangeInEncoderLeft;
		}
		else {
			lastChangeInEncoderLeft = changeInEncoderLeft;
		}

		changeInEncoderRight = encoderRightValue - lastEncoderRight;
		if (changeInEncoderRight > 3000  || changeInEncoderRight < -3000) {
			changeInEncoderRight = lastChangeInEncoderRight;
		}
		else {
			lastChangeInEncoderRight = changeInEncoderRight;
		}

		/* Calculates the change in time since the last time the method +was called.
		 * It should be approximately 20 milliseconds.
		 */
		timePassed = time - lastTime;

		// Algorithm for when the robot is going forward
		if (changeInEncoderLeft >= 0  && changeInEncoderRight >= 0) {

			changeInDistance = (changeInEncoderLeft + changeInEncoderRight) / 2;
			changeInHeading = (changeInEncoderLeft - changeInEncoderRight) / RobotMap.WHEEL_BASE;

			direction = "forward";
		} 

		// Algorithm for when the robot is going backward
		else if (changeInEncoderLeft <= 0  && changeInEncoderRight <= 0) {

			changeInDistance = (changeInEncoderLeft + changeInEncoderRight) / 2;
			changeInHeading = (changeInEncoderLeft - changeInEncoderRight) / RobotMap.WHEEL_BASE;

			direction = "backward";
		}


		// Algorithm for when the robot is spinning clockwise
		else if (changeInEncoderLeft >= 0  && changeInEncoderRight <= 0) {
			// Use a system of equation to find where inside the wheel base the point of rotation is.
			PORtoLeft = (Math.abs(changeInEncoderLeft) * RobotMap.WHEEL_BASE) / 
					(Math.abs(changeInEncoderLeft) + Math.abs(changeInEncoderRight));
			PORtoRight = RobotMap.WHEEL_BASE - PORtoLeft;

			// If the point of rotation is farther from the left side...
			if (PORtoLeft >= PORtoRight) {
				/* ...use the left changes in the left encoder to calculate your
				 * change in distance and heading.
				 */
				// Find the change in heading using the law of cosines and negate it.
				changeInHeading = Math.acos((Math.pow(changeInEncoderLeft, 2) 
						- (2 * Math.pow(PORtoLeft, 2))) / (-2 * Math.pow(PORtoLeft, 2)));
				// Find the change in distance based on the law of sines  and negate it.
				changeInDistance = Math.sin(changeInHeading) * (PORtoLeft - (RobotMap.WHEEL_BASE / 2)) /
						Math.sin((Math.PI - changeInHeading) / 2);
			}
			// If the point of rotation is farther from the right side...
			else {
				/* ...use the left changes in the left encoder to calculate your
				 * change in distance and heading.
				 */
				// Find the change in heading using the law of cosines and negate it.
				changeInHeading = Math.acos((Math.pow(changeInEncoderRight, 2) 
						- (2 * Math.pow(PORtoRight, 2))) / (-2 * Math.pow(PORtoRight, 2)));
				// Find the change in distance based on the law of sines  and negate it.
				changeInDistance = Math.sin(changeInHeading) * (PORtoRight - (RobotMap.WHEEL_BASE / 2)) /
						Math.sin((Math.PI - changeInHeading) / 2);
			}

			direction = "SCW";
		}

		// Algorithm for when the robot is spinning counterclockwise
		else if (changeInEncoderLeft <= 0  && changeInEncoderRight >= 0) {
			// Use a system of equation to find where inside the wheel base the point of rotation is.
			PORtoLeft = (Math.abs(changeInEncoderLeft) * RobotMap.WHEEL_BASE) / 
					(Math.abs(changeInEncoderLeft) + Math.abs(changeInEncoderRight));
			PORtoRight = RobotMap.WHEEL_BASE - PORtoLeft;

			// If the point of rotation is farther from the left side...
			if (PORtoLeft >= PORtoRight) {
				/* ...use the left changes in the left encoder to calculate your
				 * change in distance and heading.
				 */
				// Find the change in heading using the law of cosines.
				changeInHeading = -Math.acos((Math.pow(changeInEncoderLeft, 2) 
						- (2 * Math.pow(PORtoLeft, 2))) / (-2 * Math.pow(PORtoLeft, 2)));
				// Find the change in heading using the law of sines/
				changeInDistance = -Math.sin(changeInHeading) * (PORtoLeft - (RobotMap.WHEEL_BASE / 2)) /
						Math.sin((Math.PI - changeInHeading) / 2);
			} 

			// If the point of rotation is farther from the right side...
			else {
				/* ...use the right changes in the left encoder to calculate your
				 * change in distance and heading.
				 */
				// Find the change in heading using the law of cosines.
				changeInHeading = -Math.acos((Math.pow(changeInEncoderRight, 2) 
						- (2 * Math.pow(PORtoRight, 2))) / (-2 * Math.pow(PORtoRight, 2)));
				// Find the change in heading using the law of sines/
				changeInDistance = -Math.sin(changeInHeading) * (PORtoRight - (RobotMap.WHEEL_BASE / 2)) /
						Math.sin((Math.PI - changeInHeading) / 2);
			}

			direction = "SCCW";
		}


		// Calculate the angle of change for the bot and the change in heading.
		angle = (heading + (changeInHeading / 2));
		heading = modAngle(heading + changeInHeading);

		// Calculates the change in the X and Y directions
		changeInX = changeInDistance * Math.sin(angle);
		changeInY = changeInDistance * Math.cos(angle);

		// Calculates the new position of the robot in inches.
		clickPosX += changeInX;
		clickPosY += changeInY;
		posX = clickPosX / RobotMap.CLICKS_PER_INCH;
		posY = clickPosY / RobotMap.CLICKS_PER_INCH;

		// Calculates the speed of the robot in feet per second
		speed = ((Math.sqrt(Math.pow(changeInX, 2) + Math.pow(changeInY, 2)) 
				/ RobotMap.CLICKS_PER_INCH) / 12) / (timePassed);

		cycle++;
	}

	/**
	 * Translates the location of a cube from being relative to the robot
	 * to being an absolute field location.
	 * @param cycle The time when the message asking for a cube location was sent
	 * @param relativeCubeLocation The cube location relative to the robot.
	 * @return The absolute cube location.
	 */
	public double[] cubeLocation(int cycle, double[] relativeCubeLocation) {
		return new double[] {
				relativeCubeLocation[0]	+ navLog.get(cycle)[5],
				relativeCubeLocation[1] + navLog.get(cycle)[6]
		};

	}

	/**
	 * Refreshes dashboard values and logs values
	 */
	public void updateDashboard() {
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(heading));
		SmartDashboard.putNumber("Speed", speed);
		SmartDashboard.putNumber("DeltaTime", timePassed);
		SmartDashboard.putNumber("Delta Encoder Left", changeInEncoderLeft);
		SmartDashboard.putNumber("Delta Encoder Right", changeInEncoderRight);
		SmartDashboard.putNumber("Last Encoder Left", lastEncoderLeft);
		SmartDashboard.putNumber("Last Encoder Right", lastEncoderRight);
		SmartDashboard.putNumber("Left Encoder", encoderLeftValue);
		SmartDashboard.putNumber("Right Encoder", encoderRightValue);
		SmartDashboard.putString("Direction", direction);
	}

	public void updateLog(int state) {
		navLog.add(new double[] {
				(double) cycle,
				encoderLeftValue,
				encoderRightValue,
				time,
				heading,
				posX,
				posY,
				clickPosX,
				clickPosY,
				(double) state
		});
	}

	/**
	 * Writes the logged data to a file on the RoboRIO
	 */
	public void writeToLogger() {
		int fileID = 1;

		do {
			String fileName = "/tmp/navData" + fileID;
			navData = new File(fileName);
			fileID++;
		} while (navData.exists());

		try {
			fos = new FileOutputStream(navData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bw = new BufferedWriter(new OutputStreamWriter(fos));

		for (int i = 0; i < navLog.size(); i++) {
			try {
				bw.append(String.format("%d,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f\n",
						(int) navLog.get(i)[0],
						navLog.get(i)[1],
						navLog.get(i)[2],
						navLog.get(i)[3],
						navLog.get(i)[4],
						navLog.get(i)[5],
						navLog.get(i)[6],
						navLog.get(i)[7],
						navLog.get(i)[8], 
						navLog.get(i)[9]));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Updates n and n-1 encoder value variables.
	 */
	private void updateEncoderVals() {
		// Saves the change in time
		lastTime = time;
		time = System.currentTimeMillis() / 1000.0;

		// Gets the encoder values from the drive train.
		int[] vals = drive.getEncoderVals();

		// If the tracker has been calibrated...
		if (calibrated) {
			// ...set the current values to be the last values...
			lastEncoderLeft = encoderLeftValue;
			lastEncoderRight = encoderRightValue;
		}
		// Otherwise the tracker has not been calibrated...
		else {
			// ...so set the last value to be equal to the current value...
			lastEncoderLeft = vals[0];
			lastEncoderRight = vals[1];
			calibrated = true;
		}

		// ...and set the new encoder values.
		encoderLeftValue = vals[0];
		encoderRightValue = vals[1];
	}

	/**
	 * Resets tracking values to 0 or default.
	 */
	public void reset() {
		drive.resetEncoderPositions();

		encoderLeftValue = 0;
		encoderRightValue = 0;

		// Sets certain location calculating values back to zero
		clickPosX = 0;
		clickPosY = 0;
		cycle = 0;
		direction = "forward";
		lastChangeInEncoderLeft = 0;
		lastChangeInEncoderRight = 0;
		lastEncoderLeft = 0;
		lastEncoderRight = 0;
		heading = 0;
		posX = 0;
		posY = 0;

		calibrated = false;
	}

	/**
	 * Accessor to get the position logged by the encoders in an array.
	 * @return Returns a double array in format {x, y}
	 */
	public double[] getPos() {
		return new double[] {posX, posY};
	}

	/**
	 * @return Returns the heading (in radians between 0 and 2Pi) logged by the encoders.
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * @return Returns the data for the navigation class
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @return Returns the number of times the code has been run
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * Converts the angle measurement to a range of 0 - 2Pi.
	 * @param angle Angle measurement in radians.
	 * @return Angle measurement in radians from 0 - 2Pi.
	 */
	public static double modAngle(double angle) {
		if (angle < 0){
			angle += 2 * Math.PI;
		}
		angle %= 2 * Math.PI;
		return angle;
	}
}