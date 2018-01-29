/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	//USB id in the DS for the controller.
	public static final int GAMEPAD_PORT = 2;
	public static final int LeftJoystick = 0;
	public static final int RightJoystick = 1;
	
	//Button id values on the Joysticks
	public static final int L_TRIGGER= 1; // Left Joystick
	
	public static final int R_TRIGGER = 1; // Right Joystick
	
	//Constants for id values of the TalonSRX controllers for the drive train.
	public static final int MOTOR_REAR_LEFT = 7;
	public static final int MOTOR_FRONT_LEFT = 9;
	public static final int MOTOR_REAR_RIGHT = 6;
	public static final int MOTOR_FRONT_RIGHT = 8;
	
	//The width between the two wheels the encoders measure off of.
	public static final double RADIUS_BETWEEN_ENCODERS = 10.5;
	
	//Conversion factor for clicks to inches
	public static final double CLICKS_PER_INCH = 107.5;
}
