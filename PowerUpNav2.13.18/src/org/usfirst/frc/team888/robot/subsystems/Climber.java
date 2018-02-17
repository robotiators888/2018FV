package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Climber extends Subsystem {

	Spark climberMotorLeft, climberMotorRight;
	Solenoid climberPiston;

	public Climber() {
		climberMotorLeft = new Spark(RobotMap.CLIMBER_MOTOR_LEFT);
		climberMotorRight = new Spark(RobotMap.CLIMBER_MOTOR_RIGHT);
		climberPiston = new Solenoid(5, 0); //module number, channel
	}

	public void climberMoves(double speed) {
		climberMotorLeft.set(speed);
		climberMotorRight.set(speed);
	}

	//Need to edit this.
	public void pneumaticLocking(boolean lock) {
		if (lock) {
			climberPiston.set(true);
		} else {
			climberPiston.set(false);
		}
	}
	
	public void initDefaultCommand() {
		setDefaultCommand(new DefaultMovement());
	}
}
