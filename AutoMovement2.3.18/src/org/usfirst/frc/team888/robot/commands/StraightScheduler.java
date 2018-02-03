package org.usfirst.frc.team888.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.subsystems.StraightDrive;

/**
 *
 */
public class StraightScheduler extends Command {

	StraightDrive sd;
	
    public StraightScheduler() {
    	requires(Robot.straight);
        sd = Robot.straight;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	sd.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	sd.getAdjustments();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}
