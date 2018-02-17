package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pincer extends Subsystem {

	TalonSRX pincerMotor;

	DoubleSolenoid pincerPiston;
	
	int pincerDesiredPosition;
	
	int pincerClicks;
	
	String positioning;
	
	boolean pincerOpen = true;

	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);
		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		pincerClicks = pincerMotor.getSelectedSensorPosition(0);
	}

	//Sets the position drivers want the pincer at
	public void setDesiredPincerPosition() {
		switch (Robot.oi.getGamepadPOV()) {
		case 0:
			pincerDesiredPosition = RobotMap.RESTING_POSITION;
			break;
		case 90:
			pincerDesiredPosition = RobotMap.HIGH_DROPOFF_POSITION;
			break;
		case 180:
			pincerDesiredPosition = RobotMap.LOW_DROPOFF_POSITION;
			break;
		case 270:
			pincerDesiredPosition = RobotMap.PICKUP_POSITION;
			break;
		default:
			//figure out what this is
			break;
		}
	}

	//Move pincer to set positions through buttons
	public void setPincerPosition() {
		setDesiredPincerPosition();
		
		if (pincerClicks > (pincerDesiredPosition + 50)) {
			movePincer(RobotMap.PINCER_MOTOR_SPEED);
		} else if (pincerClicks < (pincerDesiredPosition - 50)){
			movePincer(-RobotMap.PINCER_MOTOR_SPEED);
		} else {
			movePincer(0.0);
		}
	}

	//Manually move pincer through gamepad axis
	public void movePincer(double speed) {
		pincerMotor.set(ControlMode.PercentOutput, speed * 0.3);
	}

	
	public void testPincer() {
		SmartDashboard.putNumber("Pincer Encoder", pincerClicks);
	}
	
	//Uses pistons to close pincer
	public void pince() {
		if(Robot.oi.getRightStickButton(1)) {
			if(pincerOpen) {
				pincerPiston.set(DoubleSolenoid.Value.kReverse);
			} else {
				pincerPiston.set(DoubleSolenoid.Value.kForward);
			}
		}
	}

	public void initDefaultCommand() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
		setDefaultCommand(new DefaultMovement());
	}
}