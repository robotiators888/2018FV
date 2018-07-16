package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveTrain extends Subsystem {

	//Instantiates the Talon SRX motor objects for the drive train
    protected TalonSRX rearLeft;
    protected TalonSRX frontLeft;
    protected TalonSRX rearRight;
    protected TalonSRX frontRight;
    

    public DriveTrain() {
    	//Declares the motors as objects of the TalonSRX class and passes them their CAN bus IDs
    	rearLeft = new TalonSRX(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new TalonSRX(RobotMap.MOTOR_FRONT_LEFT);
    	rearRight = new TalonSRX(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new TalonSRX(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	//Configures the encoders for the drive train motors
    	rearLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	rearRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
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
    	
    	return i;
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}