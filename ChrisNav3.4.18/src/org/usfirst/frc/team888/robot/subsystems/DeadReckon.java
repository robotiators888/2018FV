package org.usfirst.frc.team888.robot.subsystems;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DeadReckon extends Subsystem {

	//Instantiates drive train object
	protected DriveTrain drive;

	//Instantiates values for calculating location and speed
	protected double lastHeading = 0;
	protected double heading = 0;
	protected int lastEncoderLeft = 0;
	protected int lastEncoderRight = 0;
	protected int encoderLeftValue = 0;
	protected int encoderRightValue = 0;
	protected int changeInEncoderLeft = 0;
	protected int changeInEncoderRight = 0;
	protected double avgChangeInEncoder = 0;
	protected double avgMovement = 0;
	protected double changeInX = 0;
	protected double changeInY = 0;
	protected double clickPosX = 0;
	protected double clickPosY = 0;
	protected double posX = 0;
	protected double posY = 0;
	protected double clicksPerInch = 745.8299;
	protected double widthOfBot = 17060.859;
	protected double headingUsed = 0;
	protected double headingScaler = 1;
	protected double centerAdjustment = 0;
	protected int[] vals = {0,0};

	public DeadReckon(DriveTrain p_drive) throws FileNotFoundException {
		//Declares the drive object to be equal to the object passed in by Robot
		drive = p_drive;
	}
	public void deadReckonInit(){
		vals = drive.getEncoderVals();
		encoderLeftValue = vals[0];
		encoderRightValue = vals[1];
		posX = 0;
		posY = 0;
		heading = 0;
		lastHeading = 0;
		headingUsed = 0;
	}
	//Calculates the location of the robot
	public void updateTracker() {
		//Calls the method to get the most recent encoder data
		lastHeading = heading;
		lastEncoderLeft = encoderLeftValue;
		lastEncoderRight = encoderRightValue;
		vals = drive.getEncoderVals();
		encoderLeftValue = vals[0];
		encoderRightValue = vals[1];
		changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
		changeInEncoderRight = encoderRightValue - lastEncoderRight;
		avgMovement = (changeInEncoderLeft + changeInEncoderRight) / 2;
		avgChangeInEncoder = changeInEncoderLeft - changeInEncoderRight;
		heading = lastHeading + (avgChangeInEncoder / widthOfBot);
		headingUsed = heading/headingScaler;
		changeInX = (avgMovement * Math.sin(heading));
		changeInY = (avgMovement * Math.cos(heading));
		if(changeInX > 4096){
			changeInX = 0;
		}
		if(changeInY > 4096){
			changeInY = 0;
		}
		clickPosX += changeInX;
		clickPosY += changeInY;
		posX = -clickPosX / clicksPerInch;
		posY = -clickPosY / clicksPerInch;
		posX += centerAdjustment * Math.cos(headingUsed);
		posY += centerAdjustment * Math.sin(headingUsed);
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(headingUsed));
		SmartDashboard.putNumber("Left Encoder", encoderLeftValue);
		SmartDashboard.putNumber("Right Encoder", encoderRightValue);
	}

	//Refreshes dashboard values and logs values
	public void updateDashboard() throws IOException {
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(headingUsed));
		SmartDashboard.putNumber("Left Encoder", encoderLeftValue);
		SmartDashboard.putNumber("Right Encoder", encoderRightValue);
		}
	public double[] getPos() {
		double[] toReturn = {posX, posY};
		return toReturn;
	}

	/**
	 * @return Returns the heading (in radians between 0 and 2Pi) logged by the encoders.
	 */
	public double getHeading() {
		return headingUsed;
	}

	/**
	 * @return Returns the data for the navigation class
	 */
	public double[] getNavLocationData() {
		double[] i = {changeInEncoderLeft, changeInEncoderRight, heading};
		return i;
	}

	/**
	 * Converts the angle measurement to a range of 0 - 2Pi.
	 * @param angle Angle measurement in radians.
	 * @return Angle measurement in radians from 0 - 2Pi.
	 */
	public static double modAngle(double angle) {
		angle %= 2 * Math.PI;
		if (angle < 0){
			angle += 2 * Math.PI;
		}
		return angle;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}