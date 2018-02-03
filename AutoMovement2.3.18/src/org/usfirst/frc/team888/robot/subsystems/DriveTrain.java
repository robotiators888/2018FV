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

		leftStick = new Joystick(RobotMap.LEFT_JOYSTICK);
		rightStick = new Joystick(RobotMap.RIGHT_JOYSTICK);

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
		if((leftSpeed > -0.2 && leftSpeed < 0.2) && (rightSpeed > -0.2 && rightSpeed < 0.2)) {
			rearLeft.set(0.0);
			frontLeft.set(0.0);
			
			rearRight.set(0.0);
			frontRight.set(0.0);
		}
		else if(leftStick.getRawButton(RobotMap.L_TRIGGER) && rightStick.getRawButton(RobotMap.R_TRIGGER)) {
			rearLeft.set(-leftSpeed);
			frontLeft.set(-leftSpeed);

			rearRight.set(rightSpeed);
			frontRight.set(rightSpeed);
		}
		else {
			rearLeft.set(-leftSpeed * 0.7);
			frontLeft.set(-leftSpeed * 0.7);

			rearRight.set(rightSpeed * 0.7);
			frontRight.set(rightSpeed * 0.7);
		}
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
	
	public int getLeftEncoder() {
		int leftCounts = leftEncoder.get();
		return leftCounts;
	}
	
	public int getRightEncoder() {
		int rightCounts = rightEncoder.get();
		return rightCounts;
	}
	
}