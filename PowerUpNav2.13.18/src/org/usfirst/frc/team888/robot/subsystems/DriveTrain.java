package org.usfirst.frc.team888.robot.subsystems;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain extends Subsystem {

	DigitalInput proximitySensor, limitSwitch;
	

	TalonSRX rearLeft,
	frontLeft,
	rearRight,
	frontRight;
	
	Compressor mainCompressor;

	public DriveTrain() {
		rearLeft = new TalonSRX(RobotMap.MOTOR_REAR_LEFT);
		frontLeft = new TalonSRX(RobotMap.MOTOR_FRONT_LEFT);

		rearRight = new TalonSRX(RobotMap.MOTOR_REAR_RIGHT);
		frontRight = new TalonSRX(RobotMap.MOTOR_FRONT_RIGHT);
		
		mainCompressor = new Compressor(RobotMap.COMPRESSOR);

		//Configure encoders.
		rearLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		rearRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

	}

	public void initDefaultCommand() {
		rearLeft.setSelectedSensorPosition(0, 0, 0);
		rearRight.setSelectedSensorPosition(0, 0, 0);
		setDefaultCommand(new DefaultMovement());
	}

	public void startCompressor() {
		mainCompressor.start();
	}
	
	/**
	 * Sets the motor speeds.
	 * @param leftSpeed A value between -1.0 and 1.0
	 * @param rightSpeed A value between -1.0 and 1.0
	 */
	public void move(double leftSpeed, double rightSpeed) {

		rearLeft.set(ControlMode.PercentOutput, -leftSpeed);
		frontLeft.set(ControlMode.PercentOutput, -leftSpeed);

		rearRight.set(ControlMode.PercentOutput, rightSpeed);
		frontRight.set(ControlMode.PercentOutput, rightSpeed);
	}

	/**
	 *  Polls the TalonSRX's for the Encoder values.
	 * @return Encoder values in {left, right} format.
	 */

	public int[] getEncoderVals() {

		int leftClicks = rearLeft.getSelectedSensorPosition(0);
		int rightClicks = -rearRight.getSelectedSensorPosition(0);

		SmartDashboard.putNumber("Left Encoder", leftClicks);
		SmartDashboard.putNumber("Right Encoder", rightClicks);

		int[] i = {
				leftClicks,
				rightClicks };

		return i;
	}

	public void resetEncoders() {
		rearRight.setSelectedSensorPosition(0, 0, 0);
		rearLeft.setSelectedSensorPosition(0, 0, 0);
	}

}