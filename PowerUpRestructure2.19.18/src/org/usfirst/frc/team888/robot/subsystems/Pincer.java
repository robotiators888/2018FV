package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pincer extends Subsystem {

	protected TalonSRX pincerMotor;
	protected DoubleSolenoid pincerPiston;
	
	protected int pincerDesiredPosition;
	protected int pincerClicks;	
	boolean pincerOpen = true;

	
	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);

		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		pincerMotor.configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		
		pincerPiston = new DoubleSolenoid(5, 2, 3);
		
		pincerClicks = pincerMotor.getSelectedSensorPosition(0); //NEEDS TO BE MOVED!!!!
	}

	public void pincerInit() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
	}
	
	/*
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
	*/


	/*public void movePincer() {
		if (Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) > 0.1 || Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) < -0.1) {
			//pincerDesiredPosition = (int) pincerClicks + (Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS * 5);
		} else if (Robot.oi.getGamepadButton(RobotMap.A_BUTTON)) {
			pincerDesiredPosition = RobotMap.PICKUP_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.B_BUTTON)) {
			pincerDesiredPosition = RobotMap.HIGH_DROPOFF_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.X_BUTTON)) {
			pincerDesiredPosition = RobotMap.LOW_DROPOFF_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.Y_BUTTON)) {
			pincerDesiredPosition = RobotMap.RESTING_POSITION;
			setPincerPosition();
		}
	} */
	
	//Move pincer to set positions through buttons
	public void setPincerPosition(double pincerSpeed) {
		/*
		if (pincerClicks > (pincerDesiredPosition + 50)) {
			pincerMotor.set(ControlMode.PercentOutput, RobotMap.PINCER_MOTOR_SPEED);
		} else if (pincerClicks < (pincerDesiredPosition - 50)){
			pincerMotor.set(ControlMode.PercentOutput, -RobotMap.PINCER_MOTOR_SPEED);
		} else {
			pincerMotor.set(ControlMode.PercentOutput, 0);
		}
		*/
		pincerMotor.set(ControlMode.PercentOutput, pincerSpeed);
	}
	
	public void testPincer() {
		SmartDashboard.putNumber("Pincer Encoder", pincerClicks);
	}
	
	//Uses pistons to close pincer
	public void pince(boolean button) {
		if(button) {
			pincerOpen = true;
			pincerPiston.set(DoubleSolenoid.Value.kReverse);
		} else if (button) {
			pincerOpen = false;
			pincerPiston.set(DoubleSolenoid.Value.kForward);
		}
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
	}
}