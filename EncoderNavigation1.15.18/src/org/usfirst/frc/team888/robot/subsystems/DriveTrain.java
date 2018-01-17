package org.usfirst.frc.team888.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

/**
 *
 */

public class DriveTrain extends Subsystem {	
	
	public static CANTalon rearLeftMotor;
	public static CANTalon rearRightMotor;
	public static CANTalon frontLeftMotor;
	public static CANTalon frontRightMotor;
	
	public static void drivetrainInt() {
		
		rearLeftMotor = new CANTalon(RobotMap.rearLeftSRX);
		rearRightMotor = new CANTalon(RobotMap.rearRightSRX);
		frontLeftMotor = new CANTalon(RobotMap.frontLeftSRX);
		frontRightMotor = new CANTalon(RobotMap.frontRightSRX);
		
		rearLeftMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rearRightMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
	}
	
	public static void getEncoderValue() {
		
		RobotMap.encoderLeftValue = rearLeftMotor.getEncPosition();
		RobotMap.encoderRightValue = rearLeftMotor.getEncPosition();
		
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

