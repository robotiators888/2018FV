package org.usfirst.frc.team888.robot.commands;

import java.io.IOException;

import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.Navigation;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

import edu.wpi.first.wpilibj.command.Command;

public class SubsystemScheduler extends Command {

	protected Navigation navigation;
	protected RunCompressor compressor;
	protected Climber climber;
	protected Pincer pincer;

	public SubsystemScheduler(Navigation p_nav, RunCompressor p_compressor, Climber p_climber, Pincer p_pincer) {
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
		navigation.navigationInit();
		compressor.compressorInit();
		pincer.pincerInit();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		try {
			navigation.navigationExecute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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