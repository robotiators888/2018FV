package org.usfirst.frc.team888.robot.subsystems.wrap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Wrapper for a TalonSRX, implements MotorGeneric for polymorphic calls
 */
public class TalonWrapper implements MotorGeneric {

	TalonSRX m_talon;
	
	/**
	 * Creates a TalonSRX object from the given CAN bus ID
	 * @param talonID
	 */
	public TalonWrapper(int talonID) {
		m_talon = new TalonSRX(talonID);
	}
	
	/**
	 * Copies a pointer to the given TalonSRX object
	 * @param talon
	 */
	public TalonWrapper(TalonSRX talon) {
		m_talon = talon;
	}
	
	/**
	 * Sets the speed of the motor
	 */
	public void set(double speed) {
		m_talon.set(ControlMode.PercentOutput, speed);
	}
	
}
