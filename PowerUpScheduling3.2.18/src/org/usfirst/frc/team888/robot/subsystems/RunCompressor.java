package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RunCompressor extends Subsystem {

    protected Compressor mainCompressor;
    
    public RunCompressor() {
    	mainCompressor = new Compressor(RobotMap.COMPRESSOR);
    }
    
    public void compressorInit() {
    	mainCompressor.start();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}