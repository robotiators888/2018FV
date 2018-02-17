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
	
	//Returns value between -1 and 1 for left joystick
	public double getLeftStickAxis(int axis) {
		return leftStick.getRawAxis(axis);
	}
	
	//Returns value between -1 and 1 for right joystick
	public double getRightStickAxis(int axis) {
		return rightStick.getRawAxis(axis);
	}
	
	//Returns value between -1 and 1 for gamepad joystick
	public double getGamepadAxis(int axis) {
		return gamepad.getRawAxis(axis);
	}
	
	public int getGamepadPOV() {
		return gamepad.getPOV();
	}
	
	//Returns true if button is pressed
	public boolean getLeftStickButton(int button) {
		return leftStick.getRawButton(button);
	}
	
	//Returns true if button is pressed
	public boolean getRightStickButton(int button) {
		return gamepad.getRawButton(button);
	}
	
	//Returns true if button is pressed
	public boolean getGamepadButton(int button) {
		return gamepad.getRawButton(button);
	}
	
	//Returns true if both joystick triggers are pressed.
	public boolean getTriggers() {
		return (leftStick.getRawButton(1) && rightStick.getRawButton(1));
	}

}
