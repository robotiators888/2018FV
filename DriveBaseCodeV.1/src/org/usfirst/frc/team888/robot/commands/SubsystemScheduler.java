package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.subsystems.Navigation;

import edu.wpi.first.wpilibj.command.Command;

public class SubsystemScheduler extends Command {

	//Instates the objects run by the compressor
	protected Navigation navigation;

	public SubsystemScheduler(Navigation p_nav) {
		/*
		 * Sets it so only this scheduler can use the objects 
		 * and declares them as the objects passed into the scheduler by Robot
		 */
		requires(p_nav);
		navigation = p_nav;
		
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		navigation.navigationExecute();
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}