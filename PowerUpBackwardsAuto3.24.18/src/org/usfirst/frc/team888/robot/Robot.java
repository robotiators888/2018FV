/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team888.robot.commands.SubsystemScheduler;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.DeadReckon;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.Navigation;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;
import org.usfirst.frc.team888.robot.subsystems.Vision;
import org.usfirst.frc.team888.robot.subsystems.WaypointTravel;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
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

	//Instantiates scheduler object
	protected static Command subsystemScheduler;

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

		//Declares the SubsystemScheduler as a child class of Command
		subsystemScheduler = new SubsystemScheduler(navigation, compressor, climber, pincer);

		//Sends the start position selector to the dashboard
		SmartDashboard.putData("Start Position", navigation.startPosition);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		//If the scheduler is running...
		if (subsystemScheduler != null) {
			//...cancel it
			subsystemScheduler.cancel();
		}
		
		SmartDashboard.putData("Start Position", navigation.startPosition);
	}

	@Override
	public void disabledPeriodic() {
		//Asks the scheduler what to do
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {		
		//If the subsystem is not running...
		if (!subsystemScheduler.isRunning()) {
			//...start it.
			subsystemScheduler.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Asks the scheduler what to do
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		//Initialize the logger
		//location.deadReckonInit();

		//If the subsystem is not running...
		if (!subsystemScheduler.isRunning()) {
			//...start it.
			subsystemScheduler.start();
		}

	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		//Asks the scheduler what to do
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}