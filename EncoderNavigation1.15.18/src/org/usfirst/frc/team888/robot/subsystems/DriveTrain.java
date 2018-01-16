package org.usfirst.frc.team888.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

/**
 *
 */

public class DriveTrain extends Subsystem {	
	
	public static CANTalon rearLeftMotor;
	
	public static void encoder() {
		rearLeftMotor = new CANTalon(RobotMap.rearLeftSRX);
		rearLeftMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

