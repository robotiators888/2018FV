package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Reworking of Charlie's DriveTrain system. Encoders moved to their own subsystem.
 */

public class DriveTrain extends Subsystem {

    Victor rearLeft,
    		 frontLeft,
    		 rearRight,
    		 frontRight;
    
    Joystick leftStick,
    			rightStick;
    
    Encoder leftEncoder,
    			rightEncoder;

	public DriveTrain() {
    	rearLeft = new Victor(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new Victor(RobotMap.MOTOR_FRONT_LEFT);
    	
    	rearRight = new Victor(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new Victor(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	leftStick = new Joystick(RobotMap.LeftJoystick);
    	rightStick = new Joystick(RobotMap.RightJoystick);
    	
    	//Configure encoders.
    	leftEncoder = new Encoder(2, 3, true, CounterBase.EncodingType.k4X);
    	rightEncoder = new Encoder(0, 1, false, CounterBase.EncodingType.k4X);
    }
    
    public void initDefaultCommand() {
        setDefaultCommand(new DefaultMovement());
    }
    
    /**
     * Sets the motor speeds.
     * @param leftSpeed A value between -1.0 and 1.0
     * @param rightSpeed A value between -1.0 and 1.0
     */
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(leftStick.getRawAxis(1));
    	frontLeft.set(leftStick.getRawAxis(1));
    	
    	rearRight.set(rightStick.getRawAxis(1));
    	frontRight.set(rightStick.getRawAxis(1));
    	
    }
    
    /**
     *  Polls the TalonSRX's for the Encoder values.
     * @return Encoder values in {left, right} format.
     */
    public int[] getEncoderVals() {
    	
    	double leftCounts = leftEncoder.get();
		double rightCounts = rightEncoder.get();
    	SmartDashboard.putNumber("Left Encoder", leftCounts);
		SmartDashboard.putNumber("Right Encoder", rightCounts);
    	
    	int[] i = {
    			(int) leftCounts,
    			(int) rightCounts };
    	
    	return i;
    }
}

