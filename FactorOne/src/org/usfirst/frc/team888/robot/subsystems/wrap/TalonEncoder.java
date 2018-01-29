package org.usfirst.frc.team888.robot.subsystems.wrap;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Wrapper for an encoder slaved to a TalonSRX, child of EncoderWrapper for polymorphic calls
 */
public class TalonEncoder extends EncoderWrapper {

	TalonSRX m_host;
	
	/**
	 * Copies a pointer to the given TalonSRX for encoder references
	 * @param host
	 */
	public TalonEncoder(TalonSRX host) {
		m_host = host;
		initialize();
	}
	
	/**
	 * Initializes the encoder on the Talon
	 */
	private void initialize() {
		m_host.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		m_host.setSensorPhase(false);
	}
	
	/**
	 * @return The number of encoder counts registered by the encoder
	 */
	public int getCounts() {
		return m_host.getSelectedSensorPosition(0);
	}
	
}
