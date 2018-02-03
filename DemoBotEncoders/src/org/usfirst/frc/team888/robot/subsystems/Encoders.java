package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.EncoderScheduler;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	First part of the reworking of Charlie's Navigation algorithms with localized variables,
 *	and support for scheduled regular updates.
 */
public class Encoders extends Subsystem {

	double  encoderLeftValue, encoderRightValue,
			lastEncoderLeft, lastEncoderRight, lastHeading,
			changeInEncoderLeft, changeInEncoderRight, avgChangeInEncoder,
			clickPosX, clickPosY, displayHeading,
			posX, posY, heading, avgMovement;

    public void initDefaultCommand() {
        setDefaultCommand(new EncoderScheduler());
        //Automatically calls the Command that schedules this class to update its values.
    }
    
    /**
     * Updates and calculates all tracked values.
     * Sends current position data to the SmartDashboard.
     */
    public void updateTracker() {
    	updateEncoderVals();
    	changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
    	changeInEncoderRight = encoderRightValue - lastEncoderRight;
    	avgMovement = (changeInEncoderLeft + changeInEncoderRight) / 2;
    	avgChangeInEncoder = changeInEncoderLeft - changeInEncoderRight;
    	
    	clickPosX += calculateX(avgChangeInEncoder, avgMovement, heading);
    	clickPosY += calculateY(avgChangeInEncoder, avgMovement, heading);
    	heading = absAngle(lastHeading + calculateHeading(heading, avgChangeInEncoder));
    	
    	posX = clickPosX / RobotMap.CLICKS_PER_INCH;
    	posY = clickPosY / RobotMap.CLICKS_PER_INCH;
    	displayHeading = Math.toDegrees(heading);
    	
    	SmartDashboard.putNumber("X Position", posX);
    	SmartDashboard.putNumber("Y Position", posY);
    	SmartDashboard.putNumber("Heading", displayHeading);
    }
    
    /**
     * Updates n and n-1 encoder value variables.
     */
    private void updateEncoderVals() {
    	lastEncoderLeft = encoderLeftValue;
    	lastEncoderRight = encoderRightValue;
    	lastHeading = heading;
    	
    	int[] vals = Robot.drive.getEncoderVals();
    	encoderLeftValue = vals[0];
    	encoderRightValue = vals[1];
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
     * @param avgMovement The average travel value based on registered encoder value changes.
     * @param heading The current heading measurement in radians.
     * @return The change in X
     */
    private double calculateX(double avgChangeInEncoder, double avgMovement, double heading) {
    	return (avgMovement * 
    			Math.cos(heading + (avgChangeInEncoder / RobotMap.WHEEL_BASE)));
    }
    
    /**
     * Calculates the change in Y based on given data.
     * @param avgChangeInEncoder The difference between the number of encoder clicks.
     * @param avgMovement The average travel value based on registered encoder value changes.
     * @param heading The current heading measurement in radians.
     * @return The change in Y
     */
    private double calculateY(double avgChangeInEncoder, double avgMovement, double heading) {
    	return (avgMovement * 
    			Math.sin(heading + (avgChangeInEncoder / RobotMap.WHEEL_BASE)));
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
     * @return Returns the heading (in radians between 0 and 2Pi) logged by the encoders.
     */
    public double getHeading() {
    	return heading;
    }
    
    /**
     * @return Returns true if the robot is moving forward or stationary, false if backwards.
     */
    public boolean isForwards() {
    	if(avgMovement >= 0) return true;
    	else return false;
    }
    
    /**
     * Converts the angle measurement to a range of 0 - 2Pi.
     * @param angle Angle measurement in radians.
     * @return Angle measurement in radians from 0 - 2Pi.
     */
    private static double absAngle(double angle) {
    	angle %= 2 * Math.PI;
    	if (angle < 0){
    		angle = 2 * Math.PI - Math.abs(angle);
    		}
    	return angle;
    	}
}