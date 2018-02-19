package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PneumaticScheduler extends Command {

	protected RunCompressor compressor;
	protected Climber climber;
	protected Pincer pincer;
	protected OI oi;

	public PneumaticScheduler(RunCompressor p_compressor, Climber p_climber, Pincer p_pincer, OI p_oi) {
		requires(p_compressor);
		compressor = p_compressor;
		
		requires(p_climber);
		climber = p_climber;
		
		requires(p_pincer);
		pincer = p_pincer;
		
		oi = p_oi;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		compressor.startCompressor();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		
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
