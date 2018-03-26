package org.usfirst.frc.team888.robot.subsystems;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DeadReckon extends Subsystem {

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


	/*
	// Instantiates objects for logging
	private BufferedWriter bw;
	File encoderData;
	FileOutputStream fos;

	// Instantiates boolean to tell whether or not the log file has been opened
	protected boolean fileOpened = false;

	// Instantiates logging values
	protected double[][] deadReckonData = new double[1500][10];
	protected int sampleCount = 0;
	 */

	public DeadReckon(DriveTrain p_drive) {// throws FileNotFoundException {
		// Declares the drive object to be equal to the object passed in by Robot
		drive = p_drive;

		// Sets certain location calculating values back to zero
		clickPosX = 0;
		clickPosY = 0;
		direction = "forward";
		lastChangeInEncoderLeft = 0;
		lastChangeInEncoderRight = 0;
		lastEncoderLeft = 0;
		lastEncoderRight = 0;
		heading = 0;
		posX = 0;
		posY = 0;

		// Resets the encoder values
		reset();
	}

	// Initializes logger
	/*public void deadReckonInit() {		
		if (!fileOpened) {
			encoderData = new File("/tmp/encoderData");

			try {
				fos = new FileOutputStream(encoderData);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bw = new BufferedWriter(new OutputStreamWriter(fos));

			fileOpened = true;
		}
	}*/

	// Calculates the location of the robot
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

		/* Calculates the change in time since the last time the method was called.
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
		speed = ((Math.sqrt(Math.pow(changeInX, 2) + Math.pow(changeInY, 2)) / RobotMap.CLICKS_PER_INCH) / 12)
				/ (timePassed);
	}

	// Refreshes dashboard values and logs values
	public void updateDashboard() {//throws IOException {
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(heading));
		SmartDashboard.putNumber("Speed", speed);
		SmartDashboard.putNumber("DeltaTime", timePassed);
		SmartDashboard.putNumber("Left Encoder", encoderLeftValue);
		SmartDashboard.putNumber("Right Encoder", encoderRightValue);
		SmartDashboard.putString("Direction", direction);

		// Sends the values to an array for logging
		/*
		if (sampleCount < 1500) {
			deadReckonData[sampleCount][0] = encoderLeftValue;
			deadReckonData[sampleCount][1] = encoderRightValue;
			deadReckonData[sampleCount][2] = time;
			deadReckonData[sampleCount][3] = heading;
			deadReckonData[sampleCount][4] = posX;
			deadReckonData[sampleCount][5] = posY;
			deadReckonData[sampleCount][6] = calibrated ? 1.0 : 0.0;
			deadReckonData[sampleCount][7] = (double) sampleCount;
			deadReckonData[sampleCount][8] = clickPosX;
			deadReckonData[sampleCount][9] = clickPosY;

			sampleCount++;

		}

		// Writes the logged values to file
		else if (sampleCount == 1500) {
			for (int i = 0; i < 1500; i++) {
				bw.append(String.format("%d,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f\n",
						(int) deadReckonData[i][7],
						deadReckonData[i][0],
						deadReckonData[i][1],
						deadReckonData[i][2],
						deadReckonData[i][3],
						deadReckonData[i][4],
						deadReckonData[i][5],
						deadReckonData[i][6],
						deadReckonData[i][8],
						deadReckonData[i][9]));
				bw.flush();
			}
			bw.close();
			sampleCount++;
		} */
	}

	// Updates n and n-1 encoder value variables.
	private void updateEncoderVals() {
		// Saves the change in time
		lastTime = time;
		time = DriverStation.getInstance().getMatchTime();

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

		calibrated = false;
	}

	/**
	 * Accessor to get the position logged by the encoders in an array.
	 * @return Returns a double array in format {x, y}
	 */
	public double[] getPos() {
		double[] toReturn = {posX, posY};
		return toReturn;
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

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}