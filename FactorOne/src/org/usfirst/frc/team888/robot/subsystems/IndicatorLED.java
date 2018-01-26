package org.usfirst.frc.team888.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Class for controlling an indicator LED.
 */
public class IndicatorLED extends Subsystem {

	DigitalOutput dio;
	
    public IndicatorLED(int dio_port) {
    	dio = new DigitalOutput(dio_port);
    }

    public void initDefaultCommand() {}
    
    /**
     * Sets the state of the output.
     * @param state State for the logic signal to be set to.
     */
    public void setState(boolean state) {
    	dio.set(state);
    }
}