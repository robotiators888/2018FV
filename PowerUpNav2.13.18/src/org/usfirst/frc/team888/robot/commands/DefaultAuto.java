package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
//import org.usfirst.frc.team888.robot.subsystems.Encoders;
import org.usfirst.frc.team888.robot.subsystems.HeadingAdjust;
//import org.usfirst.frc.team888.robot.subsystems.StraightDrive;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Default autonomous code to run (in testing/development phases)
 */
public class DefaultAuto extends Command {

	DriveTrain dt;
	HeadingAdjust m_compass; //StraightDrive sd;
	//Encoders m_encoders;
	//int i = 0;
	double[] adjustments;
	
    public DefaultAuto() {
        requires(Robot.drive);
        this.dt = Robot.drive;
        
        //requires(Robot.encoders);
        //this.m_encoders = Robot.encoders;
        
        requires(Robot.compass); //straight);
        this.m_compass = Robot.compass; //this.sd = Robot.straight;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	adjustments = m_compass.reset(); //sd.reset():
    	//dt.resetEncoders();
    	//m_encoders.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//if ((Math.abs(36 - Robot.encoders.getX()) <= 1) && (Math.abs(120 - Robot.encoders.getY()) <= 1 )) {
    		adjustments = m_compass.getAdjustments(); //sd.getAdjustments();
    		dt.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
    	//} else {
    	//dt.move(0, 0);
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
