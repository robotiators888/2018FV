package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Climber extends Subsystem {

	protected Spark climberMotorLeft;
	protected Spark climberMotorRight;
	protected Solenoid climberPistonLeft;
	protected Solenoid climberPistonRight;

	public Climber() {
		climberMotorLeft = new Spark(RobotMap.CLIMBER_MOTOR_LEFT);
		climberMotorRight = new Spark(RobotMap.CLIMBER_MOTOR_RIGHT);
		climberPistonLeft = new Solenoid(5, 0); //module number, channel
		climberPistonRight = new Solenoid(5, 1); //module number, channel
	}

	public void climberMoves(double speed) {
		climberMotorLeft.set(speed);
		climberMotorRight.set(speed);
	}

	public void pneumaticLocking(boolean lock) {
			climberPistonLeft.set(lock);
			climberPistonRight.set(lock);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
	}
}