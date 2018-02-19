package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.Navigation;

import edu.wpi.first.wpilibj.command.Command;

public class NavigationScheduler extends Command {

	protected Navigation navigation;
	protected OI oi;

	public NavigationScheduler(Navigation p_nav, OI p_oi) {
		requires(p_nav);
		navigation = p_nav;

		oi = p_oi;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		navigation.navigationInit();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		if ((oi.getLeftStickAxis(RobotMap.L_Y_AXIS) > -0.2 
				&& oi.getLeftStickAxis(RobotMap.L_Y_AXIS) < 0.2) 
				&& (oi.getRightStickAxis(RobotMap.R_Y_AXIS) > -0.2 
						&& oi.getRightStickAxis(RobotMap.R_Y_AXIS) < 0.2)) {
			
			navigation.navigationExecute(0.0, 0.0);

		} else if (oi.getTriggers()) {
			
			navigation.navigationExecute(oi.getLeftStickAxis(RobotMap.L_Y_AXIS),
					oi.getRightStickAxis(RobotMap.R_Y_AXIS));

		} else {
			
			navigation.navigationExecute(oi.getLeftStickAxis(RobotMap.L_Y_AXIS) * 0.7,
					oi.getRightStickAxis(RobotMap.R_Y_AXIS) * 0.7);
		}
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