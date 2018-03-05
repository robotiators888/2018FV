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

import java.io.FileNotFoundException;

import org.usfirst.frc.team888.robot.commands.SubsystemScheduler;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.DeadReckon;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.Navigation;
import org.usfirst.frc.team888.robot.subsystems.Pincer;
import org.usfirst.frc.team888.robot.subsystems.RunCompressor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */

public class Robot extends TimedRobot {
	protected static OI oi;

	protected static DriveTrain drive;
	protected static DeadReckon location;
	protected static Navigation navigation;

	protected static RunCompressor compressor;
	protected static Climber climber;
	protected static Pincer pincer;

	protected static Command subsystemScheduler;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();

		drive = new DriveTrain();
		try {
			location = new DeadReckon(drive);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		compressor =  new RunCompressor();
		climber = new Climber(oi);
		pincer = new Pincer(oi);
		
		navigation =  new Navigation(drive, location, pincer, oi);

		subsystemScheduler = new SubsystemScheduler(navigation, compressor, climber, pincer);

		SmartDashboard.putData("Start Position", navigation.startPosition);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		if (subsystemScheduler != null) {
			subsystemScheduler.cancel();
		}

	}

	@Override
	public void disabledPeriodic() {
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
		//m_autonomousCommand = m_chooser.getSelected();
		location.deadReckonInit();
		
		if (!subsystemScheduler.isRunning()) {
			subsystemScheduler.start();
		}

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		//if (m_autonomousCommand != null) {
		//	m_autonomousCommand.start();
		//}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		/* 	This makes sure that the autonomous stops running when
			teleop starts running. If you want the autonomous to
			continue until interrupted by another command, remove
			this line or comment it out.
		 */
		//if (m_autonomousCommand != null) {
		//	m_autonomousCommand.cancel();
		//}
		
		if (!subsystemScheduler.isRunning()) {
			subsystemScheduler.start();
		}

	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}