package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.Navigation;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NavigationScheduler extends Command {

	protected Navigation navigation;
	protected OI oi;

	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;
	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;

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
		if(oi.getTriggers()) {
			leftBaseDriveOutput = oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
			rightBaseDriveOutput = oi.getRightStickAxis(RobotMap.R_Y_AXIS);
		} else {
			leftBaseDriveOutput = 0.7 * oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
			rightBaseDriveOutput = 0.7* oi.getRightStickAxis(RobotMap.R_Y_AXIS);
		}

		if(Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.3 &&
				Math.abs(oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.3){
			leftBaseDriveOutput = 0.0;
			rightBaseDriveOutput = 0.0;
		}

		if (input == true && lastInput == false) {
			press = true;
		} else {
			press = false;
		}

		if (press) {
			output = !output;
		}

		lastInput = input;
		input = oi.getLeftStickButton(2) || oi.getRightStickButton(2);

		if(output) {
			leftDriveOutput = leftBaseDriveOutput;
			rightDriveOutput = rightBaseDriveOutput;
		} else {
			leftDriveOutput = -rightBaseDriveOutput;
			rightDriveOutput = -leftBaseDriveOutput;
		}

		SmartDashboard.putNumber("leftOutput", leftDriveOutput);
		SmartDashboard.putNumber("rightOutput", rightDriveOutput); 

		navigation.navigationExecute(leftDriveOutput, rightDriveOutput);
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