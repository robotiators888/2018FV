package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.Navigation;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

import edu.wpi.first.wpilibj.command.Command;

public class SubsystemScheduler extends Command {

	//Instates the objects run by the compressor
	protected Navigation navigation;
	protected RunCompressor compressor;
	protected Climber climber;
	protected Pincer pincer;

	public SubsystemScheduler(Navigation p_nav, RunCompressor p_compressor, Climber p_climber, Pincer p_pincer) {
		/*
		 * Sets it so only this scheduler can use the objects 
		 * and declares them as the objects passed into the scheduler by Robot
		 */
		requires(p_nav);
		navigation = p_nav;
		
		requires(p_compressor);
		compressor = p_compressor;

		requires(p_climber);
		climber = p_climber;

		requires(p_pincer);
		pincer = p_pincer;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		//Runs the initialize method for objects that require it
		navigation.navigationInit();
		compressor.compressorInit();
		pincer.pincerInit();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		navigation.navigationExecute();
		
		pincer.pincerExecute();
		climber.climberExecute();
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