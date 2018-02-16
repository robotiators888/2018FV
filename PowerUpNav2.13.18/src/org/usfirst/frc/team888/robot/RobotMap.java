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
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	public static final int GAMEPAD_PORT = 2;
	
	//Button id values on the Joysticks
	public static final int L_TRIGGER= 1; // Left Joystick
	public static final int R_TRIGGER = 1; // Right Joystick
	
	//CAN bus IDs for the motor controllers for the drive train.
	public static final int MOTOR_REAR_LEFT = 2;
	public static final int MOTOR_FRONT_LEFT = 3;
	public static final int MOTOR_REAR_RIGHT = 1;
	public static final int MOTOR_FRONT_RIGHT = 0;
	public static final int CLIMBER_MOTOR = 5;
	public static final int PINCER_MOTOR = 4;
	
	public static final int PROXIMITY_SENSOR = 9; /**CAHNGE THIS ID**/
	public static final int LIMIT_SWITCH = 10; /**CAHNGE THIS ID**/
	
	//The width between the two wheels the encoders measure off of.
	public static final double WHEEL_BASE = 2996;
	
	//Conversion factor for clicks to inches
	public static final double CLICKS_PER_INCH = 107;
	public static final double CLIMBER_MOTOR_SPEED = 0.4;
	public static final double PINCER_MOTOR_SPEED = 0.2;
	
	public static final double HIGH_DROPOFF_LOW_RANGE = 600;
	public static final double HIGH_DROPOFF_HIGH_RANGE = 200;
	public static final double DROPOFF_LOW_POSITION = 1000;
	public static final double DROPOFF_HIGH_POSITION = 800;
	public static final double PICKUP_LOW_POSITION = 1600;
	public static final double PICKUP_HIGH_POSITION = 1200;
	
	//Sets the speed for autonomous and the adjustments to add for driving straight.
	public static final double DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT = 0.1;
	public static final double LEFT_AUTO_SPEED = 0.3;
	public static final double RIGHT_AUTO_SPEED = 0.3; 
	public static final double[] DESIRED_LOCATION = {36, 120};
	
}