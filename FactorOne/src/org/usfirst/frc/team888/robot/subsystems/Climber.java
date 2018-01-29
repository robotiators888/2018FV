package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Subsystem for controlling the climber.
 */
public class Climber extends Subsystem {

    Spark climbMotor;

    public Climber() {
    	climbMotor = new Spark(RobotMap.CLIMB_MOTOR);
    }
    
    public void initDefaultCommand() {
        //TODO add default command
    }
    
    /**
     * Sets the speed of the motor to the value.
     * @param speed A value between -1.0 and 1.0
     */
    public void setSpeed(double speed) {
    	climbMotor.set(speed);
    }
}

