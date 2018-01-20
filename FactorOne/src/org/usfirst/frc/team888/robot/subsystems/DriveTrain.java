package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Reworking of Charlie's DriveTrain system. Encoders moved to their own subsystem.
 */
@SuppressWarnings("deprecation")
public class DriveTrain extends Subsystem {

    CANTalon rearLeft,
    		 frontLeft,
    		 rearRight,
    		 frontRight;

	public DriveTrain() {
    	rearLeft = new CANTalon(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new CANTalon(RobotMap.MOTOR_FRONT_LEFT);
    	
    	rearRight = new CANTalon(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new CANTalon(RobotMap.MOTOR_FRONT_RIGHT);
    }
    
    public void initDefaultCommand() {
        setDefaultCommand(new DefaultMovement());
    }
    
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(ControlMode.PercentOutput, leftSpeed);
    	frontLeft.set(ControlMode.PercentOutput, leftSpeed);
    	
    	rearRight.set(ControlMode.PercentOutput, -rightSpeed);
    	frontRight.set(ControlMode.PercentOutput, -rightSpeed);
    }
}

