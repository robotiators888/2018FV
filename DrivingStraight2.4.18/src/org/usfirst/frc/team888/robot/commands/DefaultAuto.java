package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.StraightDrive;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Default autonomous code to run (in testing/development phases)
 */
public class DefaultAuto extends Command {

	DriveTrain dt;
	StraightDrive sd;
	//int i = 0;
	double[] adjustments;
	
    public DefaultAuto() {
        requires(Robot.drive);
        this.dt = Robot.drive;
        
        requires(Robot.straight);
        this.sd = Robot.straight;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	adjustments = sd.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//i++;
    	//if (Robot.encoders.getX() < 36) {
        	/*if(i % 5 == 0) {
        		adjustments = sd.getAdjustments();
             } */
    		adjustments = sd.getAdjustments();
    		dt.move((RobotMap.LEFT_AUTO_SPEED + adjustments[0]), (RobotMap.RIGHT_AUTO_SPEED + adjustments[1]));
    	//	dt.move(0, 0);
    	//}
    }

    // Sets this command to never end.
    protected boolean isFinished() {
        return false;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	dt.move(0, 0); //Stops motor controllers so they are ready to be taken over by other command.
    }
}
