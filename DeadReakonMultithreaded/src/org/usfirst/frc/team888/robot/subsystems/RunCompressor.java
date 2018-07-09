package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RunCompressor extends Subsystem {

	//Instantiates a compressor object from WPI
    protected Compressor mainCompressor;
    
    public RunCompressor() {
    	//Declares the main compressor as an object of type Compressor
    	mainCompressor = new Compressor(RobotMap.COMPRESSOR);
    }
    
    /**
     * Starts the compressor
     */
    public void compressorInit() {
    	mainCompressor.start();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}