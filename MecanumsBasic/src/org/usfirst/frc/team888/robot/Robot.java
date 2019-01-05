/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
    Jaguar frontLeftUpperMotor;
    Jaguar frontLeftLowerMotor;
    Jaguar rearLeftUpperMotor;
    Jaguar rearLeftLowerMotor;
    Jaguar frontRightUpperMotor;
    Jaguar frontRightLowerMotor;
    Jaguar rearRightUpperMotor;
    Jaguar rearRightLowerMotor;

    SpeedControllerGroup frontLeft;
    SpeedControllerGroup rearLeft;
    SpeedControllerGroup frontRight;
    SpeedControllerGroup rearRight;

    Joystick joystick;

    MecanumDrive dt;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        frontLeftUpperMotor = new Jaguar(0);
        frontLeftUpperMotor = new Jaguar(0);
        rearLeftUpperMotor = new Jaguar(1);
        rearLeftLowerMotor = new Jaguar(1);
        frontRightUpperMotor = new Jaguar(2);
        frontRightLowerMotor = new Jaguar(2);
        rearRightUpperMotor = new Jaguar(3);
        rearRightLowerMotor = new Jaguar(3);

        frontLeft = new SpeedControllerGroup(frontLeftUpperMotor,
                frontLeftUpperMotor);
        rearLeft = new SpeedControllerGroup(rearLeftUpperMotor,
                rearLeftLowerMotor);
        frontRight = new SpeedControllerGroup(frontRightUpperMotor,
                frontRightLowerMotor);
        rearRight = new SpeedControllerGroup(rearRightUpperMotor,
                rearRightLowerMotor);

        joystick = new Joystick(0);

        dt = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>
     * You can add additional auto modes by adding additional comparisons to the
     * switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        dt.driveCartesian(joystick.getY(), joystick.getX(), joystick.getZ());
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }
}
