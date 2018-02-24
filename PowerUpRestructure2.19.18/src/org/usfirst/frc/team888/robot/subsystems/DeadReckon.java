package org.usfirst.frc.team888.robot.subsystems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DeadReckon extends Subsystem {

	protected Timer timer;
	protected DriveTrain drive;

	double[][] deadReckonData = new double[1500][8];
	int sampleCount = 0;

	protected double angle;
	protected double encoderLeftValue;
	protected double encoderRightValue;
	protected double lastEncoderLeft;
	protected double lastEncoderRight;
	protected double lastHeading;
	protected double changeInEncoderLeft;
	protected double changeInEncoderRight;
	protected double changeInHeading;
	protected double changeInDistance;
	protected double changeInX;
	protected double changeInY;
	protected double clickPosX;
	protected double clickPosY;
	protected double time;
	protected double lastTime;
	protected double timePassed;
	protected double speed;
	protected double posX;
	protected double posY;
	protected double heading;
	protected double avgMovement;
	protected double avgChangeInEncoder;

	protected boolean calibrated;
	private BufferedWriter bw;

	public DeadReckon(DriveTrain p_drive) {
		timer = new Timer();
		drive = p_drive;

		clickPosX = 0;
		clickPosY = 0;
		lastEncoderLeft = 0;
		lastEncoderRight = 0;
		heading = 0;
		posX = 0;
		posY = 0;

		reset();
	}

    public void updateTracker() {
    	updateEncoderVals();

    	changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
    	changeInEncoderRight = encoderRightValue - lastEncoderRight;
    	avgMovement = (changeInEncoderLeft + changeInEncoderRight) / 2;
    	avgChangeInEncoder = changeInEncoderLeft - changeInEncoderRight;
    	
    	timePassed = time - lastTime;
    	
    	heading = absAngle(lastHeading + calculateHeading(heading, avgChangeInEncoder));
    	changeInX = calculateX(avgChangeInEncoder, heading);
    	changeInY = calculateY(avgChangeInEncoder, heading);
    	clickPosX += changeInX;
    	clickPosY += changeInY;
    	
    	speed = ((Math.sqrt(Math.pow(changeInX, 2) + Math.pow(changeInY, 2)) / RobotMap.CLICKS_PER_INCH) / 12)
    			/ (timePassed);
    	
    	posX = clickPosX / RobotMap.CLICKS_PER_INCH;
    	posY = clickPosY / RobotMap.CLICKS_PER_INCH;
    }
    
    /**
     * Calculates the current heading based on given data.
     * @param oldHeading The previous heading in radians.
     * @param avgChangeInEncoder The difference between the number of encoder clicks.
     * @return The new heading measurement in radians.
     */
    private double calculateHeading(double oldHeading, double avgChangeInEncoder) {
    	return ((avgChangeInEncoder / RobotMap.WHEEL_BASE));
    }
    
    /**
     * Calculates the change in X based on given data.
     * @param avgChangeInEncoder The difference between the number of encoder clicks.
     * @param heading The current heading measurement in radians.
     * @return The change in X
     */
    private double calculateX(double avgChangeInEncoder, double heading) {
    	return (avgMovement * Math.sin(heading));
    }
    
    /**
     * Calculates the change in Y based on given data.
     * @param avgChangeInEncoder The difference between the number of encoder clicks.
     * @param heading The current heading measurement in radians.
     * @return The change in Y
     */
    private double calculateY(double avgChangeInEncoder, double heading) {
    	return (avgMovement * Math.cos(heading));
    	
    }

	public void updateDashborad() throws IOException {
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(heading));
		SmartDashboard.putNumber("Speed", speed);
		SmartDashboard.putNumber("DeltaTime", timePassed);
		SmartDashboard.putNumber("Left Encoder", encoderLeftValue);
		SmartDashboard.putNumber("Right Encoder", encoderRightValue);

		if (sampleCount < 1500) {
			deadReckonData[sampleCount][0] = encoderLeftValue;
			deadReckonData[sampleCount][1] = encoderRightValue;
			deadReckonData[sampleCount][2] = time;
			deadReckonData[sampleCount][3] = heading;
			deadReckonData[sampleCount][4] = posX;
			deadReckonData[sampleCount][5] = posY;
			deadReckonData[sampleCount][6] = calibrated ? 1.0 : 0.0;
			deadReckonData[sampleCount][6] = (double) sampleCount;
			
			sampleCount++;
					
		} else if (sampleCount == 1500) {
			
			File fData = new File("/tmp/fData");
			FileOutputStream fos = new FileOutputStream(fData);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (int i = 0; i < 1500; i++) {
				bw.write(String.format("%d,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f,%.9f\n",
				(int) deadReckonData[i][7],
				deadReckonData[i][0],
				deadReckonData[i][1],
				deadReckonData[i][2],
				deadReckonData[i][3],
				deadReckonData[i][4],
				deadReckonData[i][5],
				deadReckonData[i][6]));
				bw.flush();
			}
			bw.close();
			sampleCount++;
		}
	}

	/**
	 * Updates n and n-1 encoder value variables.
	 */
	private void updateEncoderVals() {

		lastHeading = heading;
		lastTime = time;
		time = Timer.getFPGATimestamp();

		int[] vals = drive.getEncoderVals();
		if (calibrated) {
			lastEncoderLeft = encoderLeftValue;
			lastEncoderRight = encoderRightValue;
		} else {
			lastEncoderLeft = vals[0];
			lastEncoderRight = vals[1];
			calibrated = true;
		}
		encoderLeftValue = vals[0];
		encoderRightValue = vals[1];
	}

	/**
	 * Resets tracking values to 0 or default.
	 */
	public void reset() {
		drive.resetEncoderPositions();

		timer.reset();
		timer.start();

		encoderLeftValue = 0;
		encoderRightValue = 0;

		calibrated = false;
	}

	/**
	 * Accessor to get the position logged by the encoders in an array.
	 * 
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
	public double[] getNavLocationData() {
		double[] i = {changeInEncoderLeft, changeInEncoderRight, heading};
		return i;
	}

	/**
	 * Converts the angle measurement to a range of 0 - 2Pi.
	 * @param angle Angle measurement in radians.
	 * @return Angle measurement in radians from 0 - 2Pi.
	 */
	public static double absAngle(double angle) {
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