package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Reworking of Charlie's DriveTrain system. Encoders moved to their own subsystem.
 */

public class DriveTrain extends Subsystem {

    TalonSRX rearLeft,
    		 frontLeft,
    		 rearRight,
    		 frontRight;

	public DriveTrain() {
    	rearLeft = new TalonSRX(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new TalonSRX(RobotMap.MOTOR_FRONT_LEFT);
    	
    	rearRight = new TalonSRX(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new TalonSRX(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	//Configure encoders.
    	rearLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	rearLeft.setSensorPhase(false);
    	rearRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	rearRight.setSensorPhase(false);
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
    			rearLeft.getSelectedSensorPosition(0),
    			rearRight.getSelectedSensorPosition(0)};
    	
    	return i;
    }
}

