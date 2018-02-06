package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.subsystems.Climber;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Defines default behavior for the climber.
 */
public class DefaultClimber extends Command {

	Climber m_c;
	
    public DefaultClimber() {
        requires(Robot.climber);
        m_c = Robot.climber;
    }

    protected void initialize() {
    	m_c.setSpeed(0);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	m_c.setSpeed(Robot.oi.getClimbUpVal());
    }

    // Make this command never end on its own.
    protected boolean isFinished() {
        return false;
    }

    protected void interrupted() {
    	m_c.setSpeed(0);
    }
}
