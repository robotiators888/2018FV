package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Reworking of Charlie's DriveTrain system. Encoders moved to their own subsystem.
 */

public class DriveTrain extends Subsystem {

    TalonSRX rearLeft,
    		 frontLeft,
    		 rearRight,
    		 frontRight,
    		 leftEncoderHost,
    		 rightEncoderHost;

	public DriveTrain() {
    	rearLeft = new TalonSRX(RobotMap.REAR_LEFT_MOTOR);
    	frontLeft = new TalonSRX(RobotMap.FRONT_LEFT_MOTOR);
    	
    	rearRight = new TalonSRX(RobotMap.REAR_RIGHT_MOTOR);
    	frontRight = new TalonSRX(RobotMap.FRONT_RIGHT_MOTOR);
    	
    	initializeEncoders();
    }
	
	/**
	 * Initializes the encoders on the Talon SRXs based on RobotMap values.
	 */
	private void initializeEncoders() {
		//Left encoder.
		if(RobotMap.LEFT_ENCODER_ATTACHED_TO == RobotMap.REAR_LEFT_MOTOR) leftEncoderHost = rearLeft;
		else if(RobotMap.LEFT_ENCODER_ATTACHED_TO == RobotMap.FRONT_LEFT_MOTOR) leftEncoderHost = frontLeft;
		else leftEncoderHost = new TalonSRX(RobotMap.LEFT_ENCODER_ATTACHED_TO);
		leftEncoderHost.setSensorPhase(false);
		
		//Right encoder.
		if(RobotMap.RIGHT_ENCODER_ATTACHED_TO == RobotMap.REAR_RIGHT_MOTOR) rightEncoderHost = rearRight;
		else if(RobotMap.RIGHT_ENCODER_ATTACHED_TO == RobotMap.FRONT_RIGHT_MOTOR) rightEncoderHost = frontRight;
		else rightEncoderHost = new TalonSRX(RobotMap.RIGHT_ENCODER_ATTACHED_TO);
		rightEncoderHost.setSensorPhase(false);
	}
    
    public void initDefaultCommand() {
        setDefaultCommand(new DefaultMovement());
    }
    
    /**
     * Sets the motor speeds.
     * @param leftSpeed A value between -1.0 and 1.0
     * @param rightSpeed A value between -1.0 and 1.0
     */
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(ControlMode.PercentOutput, leftSpeed);
    	frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    	
    	rearRight.set(ControlMode.PercentOutput, -rightSpeed);
    	frontRight.set(ControlMode.PercentOutput, -rightSpeed);
    }
    
    /**
     *  Polls the TalonSRX's for the Encoder values.
     * @return Encoder values in {left, right} format.
     */
    public int[] getEncoderVals() {
    	int[] i = {
    			leftEncoderHost.getSelectedSensorPosition(0),
    			rightEncoderHost.getSelectedSensorPosition(0)};
    	
    	return i;
    }
}

