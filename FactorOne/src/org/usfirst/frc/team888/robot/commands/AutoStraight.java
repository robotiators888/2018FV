package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.nav.StraightDrive;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Drives straight automagically. Cookie to whichever of Charlie or Biegel can explain how I did it without using outside help.
 */
public class AutoStraight extends Command implements StraightDrive {

	DriveTrain dt;
	
    public AutoStraight() {
        requires(Robot.drive);
        this.dt = Robot.drive;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	dt.move(RobotMap.LEFT_AUTO_SPEED, RobotMap.RIGHT_AUTO_SPEED, getAdjustments());
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
