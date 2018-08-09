/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.usfirst.frc.team888.robot.workers.Climber;
import org.usfirst.frc.team888.robot.workers.DeadReckon;
import org.usfirst.frc.team888.robot.workers.DriveTrain;
import org.usfirst.frc.team888.robot.workers.Mouse;
import org.usfirst.frc.team888.robot.workers.Navigation;
import org.usfirst.frc.team888.robot.workers.Pincer;
import org.usfirst.frc.team888.robot.workers.RunCompressor;
import org.usfirst.frc.team888.robot.workers.Vision;
import org.usfirst.frc.team888.robot.workers.WaypointTravel;

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

    // Instantiates OI object
    protected static OI oi;

    // Instantiates drive train, location finding, and navigating objects
    protected static DriveTrain drive;
    protected static DeadReckon location;
    protected static Navigation navigation;
    protected static WaypointTravel guidance;

    protected Mouse mouse;
    protected ScheduledExecutorService pool;

    protected static Vision vision;

    // Instantiates compressor, climber, and pincer objects
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

        // Declares OI object
        oi = OI.getInstance();

        // Declares drive and location finding objects
        drive = DriveTrain.getInstance();

        location = DeadReckon.getInstance();
        guidance = WaypointTravel.getInstance();

        vision = Vision.getInstance();

        // Declares pneumatic-dependent objects
        compressor = RunCompressor.getInstance();
        climber = Climber.getInstance();
        pincer = Pincer.getInstance();

        // Declares navigating object and passes in classes called by navigation
        navigation = Navigation.getInstance();

        mouse = Mouse.getInstance();
        pool = Executors.newScheduledThreadPool(1);
        pool.schedule(mouse, 10, TimeUnit.MILLISECONDS);

        // Sends the start position selector to the dashboard
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
        systemRun = true;
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

        systemRun = true;
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
        // If the robot has been disabled for 5 seconds then log all data
        if (systemRun && ((t - disabledCounter) >= 5000)) {
            location.writeToLogger();
            systemRun = false;
            System.out.println("Logging sucessful");
            systemRun = false;
        }
    }
}