package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.Pincer;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command that maintains manual control over the robot as long as it is not
 * being controlled by another command affecting the DriveTrain subsystem.
 */
public class DefaultMovement extends Command {

	DriveTrain dt;
	Climber m_climb;
	Pincer m_pince;

	public DefaultMovement() {
		requires(Robot.drive);
		this.dt = Robot.drive;

		/** NOT SURE IF THIS IS HOW TO IMPLEMENT THE CLIMBER OR PINCER CLASS**/
		requires(Robot.climb);
		this.m_climb = Robot.climb;

		requires(Robot.pincer);
		this.m_pince = Robot.pincer;

	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {

		boolean isClimbingUp = false;

		if((Robot.oi.getLeftStickY() > -0.2 && Robot.oi.getLeftStickY() < 0.2) 
				&& (Robot.oi.getRightStickY() > -0.2 && Robot.oi.getRightStickY() < 0.2)) {
			dt.move(0.0, 0.0);

		} else if(Robot.oi.getTriggers()) {
			dt.move(Robot.oi.getLeftStickY(), Robot.oi.getRightStickY());

		} else {
			dt.move(Robot.oi.getLeftStickY() * 0.7, Robot.oi.getRightStickY() * 0.7);
		}

		//-------------------------------

		//Climber moves via an axis
		if (Robot.oi.getGamepadAxisY() > 0.0) {
			m_climb.climberMoves(RobotMap.CLIMBER_MOTOR_SPEED);
		} else {
			m_climb.climberMoves(-RobotMap.CLIMBER_MOTOR_SPEED);
		}

		//Climber moves via a button
		if (Robot.oi.getGamepadButton(3) && isClimbingUp == false) {
			m_climb.climberMoves(RobotMap.CLIMBER_MOTOR_SPEED);
			isClimbingUp = true;

		} else if (Robot.oi.getGamepadButton(3) && isClimbingUp == true) {
			m_climb.climberMoves(-RobotMap.CLIMBER_MOTOR_SPEED);
			isClimbingUp = false;
		}

		m_pince.testPincer();
	}

	// Sets this command to never end.
	protected boolean isFinished() {
		return false;
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		dt.move(0, 0); //Stops motor controllers so they are ready to be taken over by other command.
	}
}
