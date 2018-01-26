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
	
	Joystick m_joystick = new Joystick(RobotMap.GAMEPAD_CONTROLLER);
	
	public OI() {
		//TODO Add button triggers.
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getLeftStickY() {
		return m_joystick.getRawAxis(1);
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getRightStickY() {
		return m_joystick.getRawAxis(5);
	}
}
