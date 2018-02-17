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
	
	boolean lock = false;

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

		if((Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS) > -0.2 && Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS) < 0.2) 
				&& (Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS) > -0.2 && Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS) < 0.2)) {
			dt.move(0.0, 0.0);

		} else if(Robot.oi.getTriggers()) {
			dt.move(Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS), Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS));

		} else {
			dt.move(Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS) * 0.7, Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS) * 0.7);
		}


		//Climber moves via a button
		if (Robot.oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			m_climb.climberMoves(Robot.oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
		} else if (Robot.oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			m_climb.climberMoves(-Robot.oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
		} else {
			m_climb.climberMoves(0);
		}

		m_pince.testPincer();
		
		//m_pince.setPincerPosition();
		
		m_pince.movePincer(Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS));
		
		if(Robot.oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			m_climb.pneumaticLocking(lock);
		} else if (Robot.oi.getGamepadButton(RobotMap.GP_R_BUTTON)) {
			lock = false;
			m_climb.pneumaticLocking(lock);
		} else {
			//m_climb.pneumaticLocking(lock);
		}
		
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
