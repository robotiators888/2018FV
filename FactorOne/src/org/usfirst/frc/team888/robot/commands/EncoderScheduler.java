package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.subsystems.Encoders;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Second part of the reworking of Charlie's Navigation algorithms, sets the values to regularly update.
 */
public class EncoderScheduler extends Command {

	Encoders m_encoders;
	
    public EncoderScheduler() {
        requires(Robot.encoders);
        m_encoders = Robot.encoders;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	m_encoders.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	m_encoders.updateTracker();
    }

    // Sets this command to never end.
    protected boolean isFinished() {
        return false;
    }
}
