package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

public class Climber extends Subsystem {

	boolean isClimbingUp = false;

	Spark climberMotor;

	Compressor climberCompressor = new Compressor(1);
	DoubleSolenoid climberPistonLeft = new DoubleSolenoid(0, 1);



	public Climber() {
		climberMotor = new Spark(RobotMap.CLIMBER_MOTOR);
	}

	public void climberMoves(double speed) {
		climberMotor.set(speed);
	}

	public void pneumaticLocking() {

	}
	public void climberMovesThroughAxis() {

	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand() {
		setDefaultCommand(new DefaultMovement());
	}
}

