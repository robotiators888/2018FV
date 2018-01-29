package org.usfirst.frc.team888.robot.subsystems.wrap;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Wrapper that can handle synchronizing two solenoids, or controlling only one.
 */
public class SolenoidWrapper {
	
	Solenoid m_s1, m_s2;
	boolean dual = false;
	
	/**
	 * Creates a single solenoid
	 * @param solenoidChannel PCM Channel ID of the solenoid
	 */
	public SolenoidWrapper(int solenoidChannel) {
		m_s1 = new Solenoid(solenoidChannel);
	}
	
	/**
	 * Creates two solenoids
	 * @param solenoidChannel1 PCM Channel ID of the first solenoid
	 * @param solenoidChannel2 PCM Channel ID of the second solenoid
	 */
	public SolenoidWrapper(int solenoidChannel1, int solenoidChannel2) {
		dual = true;
		m_s1 = new Solenoid(solenoidChannel1);
		m_s2 = new Solenoid(solenoidChannel2);
	}
	
	/**
	 * Toggles the state of [both] solenoid(s)
	 */
	public void toggle() {
		toggleSol(m_s1);
		if(dual) toggleSol(m_s2);
	}
	
	/**
	 * Toggles the state of the given solenoid
	 * @param toToggle Solenoid to toggle
	 */
	private void toggleSol(Solenoid toToggle) {
		if(toToggle.get()) toToggle.set(false);
		else if(!toToggle.get()) toToggle.set(true);
	}
	
}
