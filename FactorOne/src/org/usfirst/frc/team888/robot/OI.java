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
	Joystick m_leftJoystick = new Joystick(RobotMap.LEFT_JOYSTICK);
	Joystick m_rightJoystick = new Joystick(RobotMap.RIGHT_JOYSTICK);
	
	public OI() {
		//TODO Add button triggers.
	}
	
	/**
	 * @return True if both triggers are pressed.
	 */
	public boolean getTriggers() {
		return (m_leftJoystick.getTrigger() && m_rightJoystick.getTrigger());
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getLeftStickY() {
		return m_leftJoystick.getY();
	}
	
	public double getClimbUpVal() {
		if(m_leftJoystick.getRawButton(RobotMap.CLIMB_UP_BUTTON) && !m_leftJoystick.getRawButton(RobotMap.CLIMB_DOWN_BUTTON)) {
			return 1.0;
		} else if(!m_leftJoystick.getRawButton(RobotMap.CLIMB_UP_BUTTON) && m_leftJoystick.getRawButton(RobotMap.CLIMB_DOWN_BUTTON)) {
			return -1.0;
		} else return 0;
	}
	
	/**
	 * @return A value between 1.0 and -1.0
	 */
	public double getRightStickY() {
		return m_rightJoystick.getY();
	}
}
