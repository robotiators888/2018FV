package org.usfirst.frc.team888.robot.workers;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.Compressor;

public class RunCompressor {

	private static RunCompressor compressor;
	
	//Instantiates a compressor object from WPI
    protected Compressor mainCompressor;
    
    private RunCompressor() {
    	//Declares the main compressor as an object of type Compressor
    	mainCompressor = new Compressor(RobotMap.COMPRESSOR);
    }
    
    public static RunCompressor getInstance() {
    	if (compressor != null) {
			synchronized(RunCompressor.class) {
				if (compressor != null) {
					compressor = new RunCompressor();
				}
			}
		}
		
		return compressor;
    }
    
    /**
     * Starts the compressor
     */
    public void compressorInit() {
    	mainCompressor.start();
    }
}