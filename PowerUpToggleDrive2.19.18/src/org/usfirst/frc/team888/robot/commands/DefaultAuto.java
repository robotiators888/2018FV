package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
//import org.usfirst.frc.team888.robot.subsystems.Encoders;
import org.usfirst.frc.team888.robot.subsystems.HeadingAdjust;
//import org.usfirst.frc.team888.robot.subsystems.StraightDrive;
import org.usfirst.frc.team888.robot.subsystems.Pincer;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Default autonomous code to run (in testing/development phases)
 */
public class DefaultAuto extends Command {
	DriveTrain dt;
	Pincer pince;
	HeadingAdjust m_compass;
	double[] adjustments;
	int testCounter = 0;
	public DefaultAuto() {
		this.dt = Robot.drive;
		this.m_compass = Robot.compass;
		this.pince = Robot.pincer;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		adjustments = m_compass.reset();
		dt.startCompressor();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		adjustments = m_compass.getAdjustments();
		SmartDashboard.putString("autoTester", "I'm running, " + testCounter + ", " + RobotMap.LEFT_AUTO_SPEED + ", " + RobotMap.RIGHT_AUTO_SPEED);
		testCounter = testCounter+1;
		dt.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
		pince.setPincerPosition(0);
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