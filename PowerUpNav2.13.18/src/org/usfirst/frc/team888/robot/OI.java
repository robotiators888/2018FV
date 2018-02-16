/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team888.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	Joystick gamepad = new Joystick(RobotMap.GAMEPAD_PORT);
	Joystick leftStick = new Joystick(RobotMap.LEFT_JOYSTICK);
	Joystick rightStick = new Joystick(RobotMap.RIGHT_JOYSTICK);
	
	//Returns true if both joystick triggers are pressed.
	public boolean getTriggers() {
		return (leftStick.getRawButton(1) && rightStick.getRawButton(1));
	}
	
	//Returns value between -1 and 1 for left joystick
	public double getLeftStickY() {
		return leftStick.getRawAxis(1);
	}
	
	//Returns value between -1 and 1 for right joystick
	public double getRightStickY() {
		return rightStick.getRawAxis(1);
	}
	
	//Returns true if center button on left joystick is pressed
	public boolean getLeftStickButton3() {
		return leftStick.getRawButton(3);
	}
	
	//Returns true if center button on right joystick is pressed
	public boolean getRightStickButton3() {
		return rightStick.getRawButton(3);
	}
	
	//Returns angle between 0 and 315 (starts at top and goes clockwise)
	//If no buttons are pressed returns -1
	public int getPOV()	{
		return gamepad.getPOV(0);
	}
	
	//Returns value between -1 and 1 for left axis on gamepad
	public double getGamepadAxisY() {
		return gamepad.getRawAxis(1);
	}
	
	//Returns true if gamepad button A is pressed
	public boolean getGamepadA() {
		return gamepad.getRawButton(1);
	}
	
	//Returns true if gamepad button B is pressed
	public boolean getGamepadB() {
		return gamepad.getRawButton(2);
	}
	
	//Returns true if gamepad button X is pressed
	public boolean getGamepadX() {
		return gamepad.getRawButton(3);
	}
	
	//Returns true if gamepad button Y is pressed
	public boolean getGamepadY() {
		return gamepad.getRawButton(4);
	}
	
	//Returns true if gamepad left button is pressed
	public boolean getGamepadLeft() {
		return gamepad.getRawButton(5);
	}
	
	//Returns true if gamepad right button is pressed
	public boolean getGamepadRight() {
		return gamepad.getRawButton(6);
	}
	
	
}
