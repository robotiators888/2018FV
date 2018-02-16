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

	int pincerDesiredPosition;

	String positioning;

	TalonSRX pincerMotor;

	DoubleSolenoid pincerPiston;
	
	int pincerClicks;
	
	boolean pincerClosed = false;

	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);
		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		pincerClicks = pincerMotor.getSelectedSensorPosition(0);
	}

	//Sets the position drivers want the pincer at
	public void setDesiredPincerPosition() {
		switch (Robot.oi.getPOV()) {
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
			break;
		}
	}

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

	public void movePincer(double speed) {
		pincerMotor.set(ControlMode.PercentOutput, speed);
	}

	
	public void testPincer() {
		SmartDashboard.putNumber("Pincer Encoder", pincerClicks);
	}
	
	public void pince() {
		if (!pincerClosed && Robot.oi.getGamepadY()) {
			pincerPiston.set(DoubleSolenoid.Value.kForward);
		} else if (pincerClosed && Robot.oi.getGamepadY()) {
			pincerPiston.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void initDefaultCommand() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
		setDefaultCommand(new DefaultMovement());
	}
}