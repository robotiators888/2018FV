package org.usfirst.frc.team888.robot.workers;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	private static DriveTrain drive;
	
	// Instantiates the Talon SRX motor objects for the drive train
    protected TalonSRX rearLeft;
    protected TalonSRX frontLeft;
    protected TalonSRX rearRight;
    protected TalonSRX frontRight;
    

    private DriveTrain() {
    	// Declares the motors as objects of the TalonSRX class and passes them their CAN bus IDs
    	rearLeft = new TalonSRX(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new TalonSRX(RobotMap.MOTOR_FRONT_LEFT);
    	rearRight = new TalonSRX(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new TalonSRX(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	// Configures the encoders for the drive train motors
    	rearLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	rearRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    }
    
	/**
	 * Accessor method for the DriveTrain Singleton.
	 * @return The object of DriveTrain
	 */
    public static DriveTrain getInstance() {
    	if (drive != null) {
    		synchronized(DriveTrain.class) {
    			if (drive != null) {
    				drive = new DriveTrain();
    			}
    		}
    	}
    	
    	return drive;
    }
    
    /**
     * Resets the encoder positions to zero 
     */
    public void resetEncoderPositions() {
    	rearLeft.setSelectedSensorPosition(0, 0, 0);
    	rearRight.setSelectedSensorPosition(0, 0, 0);
    }
    
    /**
     * Sends an output value to the motors
     * @param leftSpeed Percent output for the two left motors
     * @param rightSpeed Percent output for the two right motors
     */
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(ControlMode.PercentOutput, leftSpeed);
    	frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    	
    	rearRight.set(ControlMode.PercentOutput, -rightSpeed);
    	frontRight.set(ControlMode.PercentOutput, -rightSpeed);
    }
    
    /**
     * Gets the position from the encoders
     * @return An array of left and right clicks
     */
    public int[] getEncoderVals() {
    	int leftClicks = -rearLeft.getSelectedSensorPosition(0);
    	int rightClicks = rearRight.getSelectedSensorPosition(0);
    	
    	
    	int[] i = {leftClicks, rightClicks};
    	
    	SmartDashboard.putNumber("Raw Left Clicks", leftClicks);
    	SmartDashboard.putNumber("Raw Right Clicks", rightClicks);
    	SmartDashboard.putNumber("Left Talon Current", rearLeft.getOutputCurrent());
    	SmartDashboard.putNumber("Right Talon Current", rearRight.getOutputCurrent());
    	
    	return i;
    }
}