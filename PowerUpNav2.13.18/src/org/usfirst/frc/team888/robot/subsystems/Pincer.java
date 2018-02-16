package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Pincer extends Subsystem {

	PincerPositions pincerPosition;

	String positioning;
	TalonSRX pincerMotor;
	int pincerClicks;

	public enum PincerPositions {
		pickUp, dropOff, resting, highDropOff		
	}

	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);
		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		pincerClicks = pincerMotor.getSelectedSensorPosition(0);
	}

	//Sets the position drivers want the pincer at
	public void setDesiredPincerPosition() {
		switch (Robot.oi.getPOV()) {
		case 0:
			pincerPosition = pincerPosition.resting;
			break;
		case 90:
			pincerPosition = pincerPosition.highDropOff;
			break;
		case 180:
			pincerPosition = pincerPosition.dropOff;
			break;
		case 270:
			pincerPosition = pincerPosition.pickUp;
			break;
		default:
			break;
		}
	}

	public PincerPositions getPincerPosition() {
		return pincerPosition;
	}

	public void movePincer(double speed) {
		pincerMotor.set(ControlMode.PercentOutput, speed);
	}
	
	public void setPincer() {
		
		switch (pincerPosition) {
		case resting:
			if (pincerClicks > 50) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}				
			break;
		case highDropOff:
			if (pincerClicks > (250+50)) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks < (250-50)){
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		case dropOff:
			if (pincerClicks > (500+50)) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks < (500-50)){
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		case pickUp:
			if (pincerClicks > (750+50)) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks < (750-50)){
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		default:
		} 
	}
	
	public void pince() {
		if(Robot.oi.getLeftStickButton3()) {
			
		} else if(Robot.oi.getRightStickButton3()) {
			
		}
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}

