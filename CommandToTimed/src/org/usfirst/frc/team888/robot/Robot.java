/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.DeadReckon;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.Navigation;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;
import org.usfirst.frc.team888.robot.subsystems.Vision;
import org.usfirst.frc.team888.robot.subsystems.WaypointTravel;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {

	//Instantiates OI object
	protected static OI oi;

	//Instantiates drive train, location finding, and navigating objects
	protected static DriveTrain drive;
	protected static DeadReckon location;
	protected static Navigation navigation;
	protected static WaypointTravel gps;

	protected static Vision vision;

	//Instantiates compressor, climber, and pincer objects
	protected static RunCompressor compressor;
	protected static Climber climber;
	protected static Pincer pincer;

	protected static long disabledCounter;

	private boolean systemRun = false;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Declares OI object
		oi = new OI();

		//Declares drive and location finding objects
		drive = new DriveTrain();
		location = new DeadReckon(drive);
		gps = new WaypointTravel(drive, location);

		vision = new Vision();

		//Declares pneumatic-dependent objects
		compressor =  new RunCompressor();
		climber = new Climber(oi);
		pincer = new Pincer(oi);

		//Dclares navigating object and passes in classes called by navigation
		navigation =  new Navigation(drive, location, pincer, vision, gps, climber, oi);


		//Sends the start position selector to the dashboard
		SmartDashboard.putData("Start Position", navigation.startPosition);
	}

	/**
	 * This function is called once at the beginning of autonomous.
	 */
	@Override
	public void autonomousInit() {
		navigation.navigationInit();
		compressor.compressorInit();
		pincer.pincerInit();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		navigation.navigationExecute();

		pincer.pincerExecute();
		climber.climberExecute();
	}

	/**
	 * This function is called once at the beginning of the operator control.
	 */
	@Override
	public void teleopInit() {
		navigation.navigationInit();
		compressor.compressorInit();
		pincer.pincerInit();
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		navigation.navigationExecute();

		pincer.pincerExecute();
		climber.climberExecute();
	}

	/**
	 * This function is called once when the robot enters a disabled state.
	 */
	@Override
	public void disabledInit() {
		SmartDashboard.putData("Start Position", navigation.startPosition);
		SmartDashboard.putData("Stratagy", navigation.strategy);

		disabledCounter = System.currentTimeMillis();
	}

	/**
	 * This function is called periodically while the robot is disabled.
	 */
	@Override
	public void disabledPeriodic() {
		long t = System.currentTimeMillis();
		if (systemRun && ((t - disabledCounter) >= 5000)) {
			location.writeToLogger();
			systemRun = false;
			System.out.println("Logging sucessful");
		}
	}
}
