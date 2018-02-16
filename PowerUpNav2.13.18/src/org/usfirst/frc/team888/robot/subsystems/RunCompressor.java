package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.commands.CompressorScheduler;

import edu.wpi.first.wpilibj.command.Subsystem;


public class RunCompressor extends Subsystem {

    RunCompressor mainCompressor;
    
    public void startCompressor() {
    	mainCompressor.startCompressor();
    }

    public void initDefaultCommand() {
        setDefaultCommand(new CompressorScheduler());
    }
}