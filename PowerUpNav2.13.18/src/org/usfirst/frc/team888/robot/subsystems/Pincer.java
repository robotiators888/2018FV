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

	public PincerPositions findPincerPosition() {
		switch (Robot.oi.getPOV()) {
		case 0:
			return pincerPosition.resting;
		case 90:
			return pincerPosition.highDropOff;
		case 180:
			return pincerPosition.dropOff;
		case 270:
			return pincerPosition.pickUp;
		default:
			return pincerPosition;
		}
	}

	public PincerPositions getPincerPosition() {
		return pincerPosition;
	}

	public void movePincer(double speed) {
		pincerMotor.set(ControlMode.PercentOutput, speed);
	}
	
	public void pincerPositions() {
		
		switch (pincerPosition) {
		case resting:
			if (pincerClicks > 50) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}				
			break;
		case highDropOff:
			if (pincerClicks != 600+50 || pincerClicks != 600-50) {
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks != 400+50 || pincerClicks != 400-50){
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		case dropOff:
			if (pincerClicks != 600+50 || pincerClicks != 600-50) {
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks != 400+50 || pincerClicks != 400-50) {
				movePincer(-RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		case pickUp:
			if (pincerClicks != 600+50 || pincerClicks != 600-50) {
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else if (pincerClicks != 400+50 || pincerClicks != 400-50) {
				movePincer(RobotMap.PINCER_MOTOR_SPEED);
			} else {
				movePincer(0.0);
			}
			break;
		default:
		} 
	}
	public void pincerPosition() {

	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}

