package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class PneumaticScheduler extends Command {

	protected RunCompressor compressor;
	protected Climber climber;
	protected Pincer pincer;
	protected OI oi;

	protected boolean lock;
	protected double desiredPosition = 2115;

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
		//Climber moves via gamepad triggers
		if (oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			climber.climberMoves(oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
		} else if (oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			climber.climberMoves(-oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
		} else {
			climber.climberMoves(0);
		}

		pincer.displaySensorValues();
		if(oi.getGamepadButton(1)){
			desiredPosition = 825;
		}
		if(oi.getGamepadButton(2)){
			desiredPosition = 1650;
		}
		if(oi.getGamepadButton(3)){
			desiredPosition = 2115;
		}
		pincer.setPincerPosition(desiredPosition, oi.getGamepadButton(8), oi.getGamepadAxis(1));

		pincer.pince(oi.getRightStickButton(3));

		if (oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			climber.pneumaticLocking(lock);
			SmartDashboard.putBoolean("Locked?", !lock);

			if(!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}

		} else if (oi.getGamepadButton(RobotMap.GP_R_BUTTON)) {
			lock = false;
			climber.pneumaticLocking(lock);

			SmartDashboard.putBoolean("locked?", !lock);

			if(!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}

		} else {
			SmartDashboard.putBoolean("Locked?", !lock);
			if (!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}
		}
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
