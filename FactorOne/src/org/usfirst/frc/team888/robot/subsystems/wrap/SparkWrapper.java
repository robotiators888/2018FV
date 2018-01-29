package org.usfirst.frc.team888.robot.subsystems.wrap;

import edu.wpi.first.wpilibj.Spark;

/**
 * Motor controller wrapper, uses MotorGeneric interface for polymorphic calls.
 */
public class SparkWrapper implements MotorGeneric {

	Spark m_spark;
	
	/**
	 * Creates the controller from the given channel
	 * @param channel
	 */
	public SparkWrapper(int channel) {
		m_spark = new Spark(channel);
	}
	
	/**
	 * Creates the controller from the given controller reference
	 * @param spark
	 */
	public SparkWrapper(Spark spark) {
		m_spark = spark;
	}
	
	/**
	 * Sets the speed of the motor
	 */
	public void set(double speed) {
		m_spark.set(speed);
	}
	
}
