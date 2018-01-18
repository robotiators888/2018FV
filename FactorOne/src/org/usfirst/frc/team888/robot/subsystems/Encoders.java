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
			posX, posY, lastPosX, lastPosY,
			changeInX, changeInY, heading,
			changeInPos, changeInAngle, distanceTraveled;

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
    	changeInPos = (changeInEncoderLeft + changeInEncoderRight) / 2;
    	changeInAngle = (changeInEncoderLeft - changeInEncoderRight) / RobotMap.WIDTH_BETWEEN_ENCODERS;
    	heading = angleAbs(heading + changeInAngle);
    	changeInX = changeInPos * Math.cos(heading + (changeInEncoderLeft / (2 * RobotMap.WIDTH_BETWEEN_ENCODERS)));
    	changeInY = changeInPos * Math.sin(heading + (changeInEncoderRight / (2 * RobotMap.WIDTH_BETWEEN_ENCODERS)));
    	distanceTraveled = Math.sqrt(Math.pow(changeInX, 2) + Math.pow(changeInY, 2));
    	posX += changeInX;
    	posY += changeInY;
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
    	
    	return angle;
    }
}

