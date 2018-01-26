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
	//Drive Train values.
	//Talon SRX Motor Controller IDs.
	public static int FRONT_LEFT_MOTOR;
	public static int REAR_LEFT_MOTOR;
	public static int FRONT_RIGHT_MOTOR;
	public static int REAR_RIGHT_MOTOR;
	
	//IDs of the Talon SRXs that the encoders are associated with.
	public static int LEFT_ENCODER_ATTACHED_TO;
	public static int RIGHT_ENCODER_ATTACHED_TO;
	
	//Calculation constants.
	public static final double WIDTH_BETWEEN_ENCODERS = 33;
	
	//Manipulator values.
	//Arm tilt actuation values.
	public static int ARM_TILT_MOTOR;
	public static int ENCODER_ID;
	
	//Grabber actuation values.
	public static int CLAW_SOLENOID;
	public static int CLAW_SOLENOID2;
	
	//Manipulator arg flags.
	public static boolean IS_DUAL_SOLENOID;
	public static boolean IS_ARM_TALON_CONTROLLED;
	
	//Driver station values.
	//Control device IDs.
	public static int LEFT_JOYSTICK;
	public static int RIGHT_JOYSTICK;
	public static int GAMEPAD_CONTROLLER;
	
	//Diagnostic values.
	public static boolean JSON_READ_SUCCESSFUL = false; //Indicates if the JSON file read successfully
}
