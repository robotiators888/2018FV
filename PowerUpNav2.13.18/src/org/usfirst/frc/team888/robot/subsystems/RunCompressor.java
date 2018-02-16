package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.CompressorScheduler;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;


public class RunCompressor extends Subsystem {

    Compressor mainCompressor;
    
    public RunCompressor() {
    	mainCompressor = new Compressor(RobotMap.COMPRESSOR);
    }
    
    public void compressorStart() {
    	mainCompressor.start();
    }

    public void initDefaultCommand() {
        setDefaultCommand(new CompressorScheduler());
    }
}