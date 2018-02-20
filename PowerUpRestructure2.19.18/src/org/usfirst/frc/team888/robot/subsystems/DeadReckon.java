package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DeadReckon extends Subsystem {

	protected Timer timer;
	protected DriveTrain drive;
	
    protected double encoderLeftValue;
    protected double encoderRightValue;
    protected double lastEncoderLeft;
    protected double lastEncoderRight;
    protected double lastHeading;
    protected double changeInEncoderLeft;
    protected double changeInEncoderRight;
    protected double avgChangeInEncoder;
    protected double avgMovement;
    protected double changeInX;
    protected double changeInY;
    protected double clickPosX;
    protected double clickPosY;
    protected double displayHeading;
    protected double time;
    protected double lastTime;
    protected double timePassed;
    protected double speed;
    protected double posX;
    protected double posY;
    protected double heading;

    public DeadReckon(DriveTrain p_drive) {
    	timer = new Timer();
    	drive = p_drive;
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
    	displayHeading = Math.toDegrees(heading);
    	
    	SmartDashboard.putNumber("X Position", posX);
    	SmartDashboard.putNumber("Y Position", posY);
    	SmartDashboard.putNumber("Heading", displayHeading);
    	SmartDashboard.putNumber("Speed", speed);
    	SmartDashboard.putNumber("DeltaTime", timePassed);
    }
    
    /**
     * Updates n and n-1 encoder value variables.
     */
    private void updateEncoderVals() {
    	
    	lastEncoderLeft = encoderLeftValue;
    	lastEncoderRight = encoderRightValue;
    	lastHeading = heading;
    	lastTime = time;
    	time = timer.get();
    	
    	int[] vals = drive.getEncoderVals();
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
     * @param heading The current heading measurement in radians.
     * @return The change in X
     */
    private double calculateX(double avgChangeInEncoder, double heading) {
    	return -(avgMovement * Math.sin(heading));
    }
    
    /**
     * Calculates the change in Y based on given data.
     * @param avgChangeInEncoder The difference between the number of encoder clicks.
     * @param heading The current heading measurement in radians.
     * @return The change in Y
     */
    private double calculateY(double avgChangeInEncoder, double heading) {
    	return -(avgMovement * Math.cos(heading));
    	
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
    
    public void startTimer() {
    	timer.reset();
    	timer.start();
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
     * @return Returns the data for the navigation class
     */
    public double[] getNavLocationData() {
    	double[] i = {changeInEncoderLeft, changeInEncoderRight, heading};
    	return i;
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