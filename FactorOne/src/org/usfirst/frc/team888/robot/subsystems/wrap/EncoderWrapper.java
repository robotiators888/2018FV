package org.usfirst.frc.team888.robot.subsystems.wrap;

import edu.wpi.first.wpilibj.Encoder;

/**
 *  Wraps the encoder into a protected type.
 */
public class EncoderWrapper {
	
	Encoder m_encoder;
	
	/**
	 * Creates an encoder object with the given channel.
	 * @param channel_A
	 * @param channel_B
	 */
	public EncoderWrapper(int channel_A, int channel_B) {
		m_encoder = new Encoder(channel_A, channel_B);
	}
	
	public EncoderWrapper() {}
	
	/**
	 * @return The number of encoder counts registered by the encoder.
	 */
	public int getCounts() {
		return m_encoder.get();
	}
	
}
