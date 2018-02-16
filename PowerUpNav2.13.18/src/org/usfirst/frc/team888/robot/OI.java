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
	
	/**
	 * @return True if both triggers are pressed.
	 */
	public boolean getTriggers() {
		return (leftStick.getRawButton(1) && rightStick.getRawButton(1));
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getLeftStickY() {
		return leftStick.getRawAxis(1);
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getRightStickY() {
		return rightStick.getRawAxis(1);
	}
	
	public int getPOV()	{
		return gamepad.getPOV(0);
	}
	
	public double getGamepadAxisY() {
		return gamepad.getRawAxis(1);
	}
	
	public boolean getGamepadButton(int buttonNum) {
		return gamepad.getRawButton(buttonNum);
	}
}