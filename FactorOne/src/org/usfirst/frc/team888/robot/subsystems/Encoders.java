package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.EncoderScheduler;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	First part of the reworking of Charlie's Navigation algorithms with localized variables,
 *	and support for scheduled regular updates.
 */
public class Encoders extends Subsystem {

	double  encoderLeftValue, encoderRightValue,
			lastEncoderLeft, lastEncoderRight,
			changeInEncoderLeft, changeInEncoderRight,
			posX, posY, heading, avgMovement;

    public void initDefaultCommand() {
        setDefaultCommand(new EncoderScheduler()); //Automatically calls the Command that schedules this class to update its values.
    }
    
    /**
     * Updates and calculates all tracked values.
     */
    public void updateTracker() {
    	updateEncoderVals();
    	changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
    	changeInEncoderRight = encoderRightValue - lastEncoderRight;
    	avgMovement = (changeInEncoderLeft + changeInEncoderRight) / 2;
    	
    	heading = calculateHeading(heading, changeInEncoderLeft, changeInEncoderRight);
    	posX += calculateX(changeInEncoderLeft, avgMovement, heading);
    	posY += calculateY(changeInEncoderRight, avgMovement, heading);
    }
    
    /**
     * Updates n and n-1 encoder value variables.
     */
    private void updateEncoderVals() {
    	lastEncoderLeft = encoderLeftValue;
    	lastEncoderRight = encoderRightValue;
    	
    	int[] vals = Robot.drive.getEncoderVals();
    	encoderLeftValue = vals[0];
    	encoderRightValue = vals[1];
    }
    
    /**
     * Calculates the current heading based on given data.
     * @param oldHeading The previous heading.
     * @param changeInLeft The change in registered encoder values from the left encoder.
     * @param changeInRight The change in registered encoder values from the right encoder.
     * @return The new heading measurement in a range between 0 and 360.
     */
    private double calculateHeading(double oldHeading, double changeInLeft, double changeInRight) {
    	return angleAbs(((changeInLeft - changeInRight) / RobotMap.WIDTH_BETWEEN_ENCODERS) + oldHeading);
    }
    
    /**
     * Calculates the change in X based on given data.
     * @param changeInLeft The change in registered encoder values from the left encoder.
     * @param avgMovement The average travel value based on registered encoder value changes.
     * @param heading The current heading measurement.
     * @return The change in X
     */
    private double calculateX(double changeInLeft, double avgMovement, double heading) {
    	return (heading * Math.cos(heading + (changeInLeft / (2 * RobotMap.WIDTH_BETWEEN_ENCODERS))));
    }
    
    /**
     * Calculates the change in Y based on given data.
     * @param changeInRight The change in registered encoder values from the right encoder.
     * @param avgMovement The average travel value based on registered encoder value changes.
     * @param heading The current heading measurement.
     * @return The change in Y
     */
    private double calculateY(double changeInRight, double avgMovement, double heading) {
    	return (heading * Math.sin(heading + (changeInRight / (2 * RobotMap.WIDTH_BETWEEN_ENCODERS))));
    }
    
    /**
     * Resets tracking values to 0 or default.
     */
    public void reset() {
    	encoderLeftValue = 0;
    	encoderRightValue = 0;
    	
    	updateEncoderVals();
    	
    	heading = 0;
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
     * @return Returns the X position logged by the encoders.
     */
    public double getX() {
    	return posX;
    }
    
    /**
     * @return Returns the Y position logged by the encoders.
     */
    public double getY() {
    	return posY;
    }
    
    /**
     * @return Returns the heading (between 0 and 360 degrees) logged by the encoders.
     */
    public double getHeading() {
    	return heading;
    }
    
    /**
     * Converts the angle measurement to a range of 0 - 360.
     * @param angle Angle measurement in degrees
     * @return Angle measurement in degrees between 0 and 360
     */
    private static double angleAbs(double angle) {
    	if(angle < 0) angle += 360;
    	else if(angle > 360) angle -= 360;
    	if(angle < 0 || angle > 360) return angleAbs(angle); //Recursively calls itself until angle is within the range.
    	return angle;
    }
}

