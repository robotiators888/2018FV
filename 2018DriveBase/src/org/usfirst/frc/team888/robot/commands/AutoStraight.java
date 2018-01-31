package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.subsystems.DriveStraight;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoStraight extends Command {

	DriveStraight s_straight;
	DriveTrain m_drive;
	int i = 0;

    public AutoStraight() {
    	s_straight = Robot.straight;
    	m_drive = Robot.drive;
    	requires(s_straight);
    	requires(m_drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	s_straight.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	i++;
    	if(i >= 5) {
    		s_straight.update();
    		i = 0;
    	}
    	double[] adjustmentsToAdd = s_straight.driveStraight(m_drive.getLeftEncoder(), m_drive.getRightEncoder());
		m_drive.move(0.5+adjustmentsToAdd[0], (0.5+adjustmentsToAdd[1]));
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
