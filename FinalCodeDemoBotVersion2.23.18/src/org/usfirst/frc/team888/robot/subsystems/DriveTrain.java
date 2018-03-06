package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveTrain extends Subsystem {

	protected Victor rearLeft;
	protected Victor frontLeft;
	protected Victor rearRight;
	protected Victor frontRight;

	protected Encoder leftEncoder;
	protected Encoder rightEncoder;


	public DriveTrain() {
		rearLeft = new Victor(RobotMap.MOTOR_REAR_LEFT);
		frontLeft = new Victor(RobotMap.MOTOR_FRONT_LEFT);
		rearRight = new Victor(RobotMap.MOTOR_REAR_RIGHT);
		frontRight = new Victor(RobotMap.MOTOR_FRONT_RIGHT);

		leftEncoder = new Encoder(2, 3, true, CounterBase.EncodingType.k4X);
		rightEncoder = new Encoder(0, 1, false, CounterBase.EncodingType.k4X);
	}

	public void resetEncoderPositions() {
	}

	public void move(double leftSpeed, double rightSpeed) {
		rearLeft.set(leftSpeed);
		frontLeft.set(leftSpeed);

		rearRight.set(-rightSpeed);
		frontRight.set(-rightSpeed);
	}

	public int[] getEncoderVals() {
		int leftClicks = leftEncoder.get();
		int rightClicks = rightEncoder.get();

		int[] i = {leftClicks, rightClicks};

		return i;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}