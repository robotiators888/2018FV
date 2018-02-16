package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Sets the compressor to run continuously.
 */
public class CompressorScheduler extends Command {

	RunCompressor m_compressor;
	
    public CompressorScheduler() {
    	requires(Robot.compressor);
        m_compressor = Robot.compressor;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	m_compressor.startCompressor();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}