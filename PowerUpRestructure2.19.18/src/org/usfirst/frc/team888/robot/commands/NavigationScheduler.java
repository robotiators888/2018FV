package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.subsystems.Navigation;

import edu.wpi.first.wpilibj.command.Command;

public class NavigationScheduler extends Command {

	protected Navigation navigation;

	public NavigationScheduler(Navigation p_nav) {
		requires(p_nav);
		navigation = p_nav;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		navigation.navigationInit();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		//navigation.navigationExecute(0.3, 0.3);
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