package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain extends Subsystem {

    TalonSRX rearLeft;
    TalonSRX frontLeft;
    TalonSRX rearRight;
    TalonSRX frontRight;
    

    public DriveTrain() {
    	rearLeft = new TalonSRX(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new TalonSRX(RobotMap.MOTOR_FRONT_LEFT);
    	rearRight = new TalonSRX(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new TalonSRX(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	rearLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	rearRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    }
    
    public void resetEncoderPositions() {
    	rearLeft.setSelectedSensorPosition(0, 0, 0);
    	rearRight.setSelectedSensorPosition(0, 0, 0);
    }
    
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(ControlMode.PercentOutput, leftSpeed);
    	frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    	
    	rearRight.set(ControlMode.PercentOutput, -rightSpeed);
    	frontRight.set(ControlMode.PercentOutput, -rightSpeed);
    }
    
    public int[] getEncoderVals() {
    	int leftClicks = rearLeft.getSelectedSensorPosition(0);
    	int rightClicks = -rearRight.getSelectedSensorPosition(0);
    	
    	SmartDashboard.putNumber("Left Encoder", leftClicks);
    	SmartDashboard.putNumber("Right Encoder", rightClicks);
    	
    	int[] i = {leftClicks, rightClicks};
    	
    	return i;
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}