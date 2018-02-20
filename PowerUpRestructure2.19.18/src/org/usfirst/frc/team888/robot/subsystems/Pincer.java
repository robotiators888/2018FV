package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pincer extends Subsystem {

	protected TalonSRX pincerMotor;
	protected DoubleSolenoid pincerPiston;
	
	protected AnalogInput pincerEncoder;
	
	protected DigitalInput proximity;
	protected DigitalInput topLimit;
	protected DigitalInput bottomLimit;
	
	protected int pincerDesiredPosition;
	protected int pincerClicks;	
	
	protected boolean pincerOpen = true;

	protected double currentAngle = 0;
	protected double lastAngle = 0;
	protected double angleThreshold = 50;
	protected double pincerPower = 0;
	protected double maintainerConstant = 0;
	protected double movementThreshold = 30;
	protected double maintainerConstantIterator = 0.00005;
	protected double reflexLow = 0.1;
	protected double reflexHigh = 0.3;
	protected int reflexLength = 0;
	protected int reflexTimer = 0;
	protected int reflexStart = 0;
	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);

		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		pincerMotor.configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		
		pincerPiston = new DoubleSolenoid(5, 2, 3);
		
		pincerEncoder = new AnalogInput(0);
    	pincerEncoder.setOversampleBits(2);
    	pincerEncoder.setAverageBits(2);
		
		proximity = new DigitalInput(0);
		
		topLimit = new DigitalInput(1);
	}

	public void pincerInit() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
	}
	/*
	public void setPincerPosition(double pincerSpeed) {
		pincerMotor.set(ControlMode.PercentOutput, pincerSpeed);
		SmartDashboard.putNumber("pincer output", pincerSpeed);
	}
	*/
	public void setPincerPosition(double desiredAngle){
		SmartDashboard.putNumber("desiredAngle", desiredAngle);
		SmartDashboard.putNumber("currentAngle", currentAngle);
		SmartDashboard.putNumber("maintainerConstant", maintainerConstant);
		SmartDashboard.putNumber("pincerPower", pincerPower);
		SmartDashboard.putNumber("change", currentAngle - lastAngle);
		
		if(currentAngle > (desiredAngle + angleThreshold)){
			if(Math.abs(currentAngle - lastAngle) < movementThreshold){
				maintainerConstant = maintainerConstant + maintainerConstantIterator;
			}
			pincerPower = maintainerConstant*Math.abs(currentAngle - desiredAngle);
			if(pincerPower > 0.4){
				pincerPower = 0.4;
			}
			//stuff to bring down to angle
		}
		else if(currentAngle < (desiredAngle - angleThreshold)){
			if(Math.abs(currentAngle - lastAngle) < movementThreshold){
				maintainerConstant = maintainerConstant + maintainerConstantIterator;
			}
			pincerPower = maintainerConstant*Math.abs(currentAngle - desiredAngle);
			if(pincerPower > 0.4){
				pincerPower = 0.4;
			}
			pincerPower = -pincerPower;
			//stuff to bring up to angle
		}
		else{
			reflexStart = reflexTimer;
			pincerPower = -pincerPower*(reflexLow/reflexHigh);
			if(reflexTimer-reflexStart > reflexLength){
				
			maintainerConstant = 0;
			pincerPower = 0;
			}
			//stuff to maintain position, want to do a thing where it puts slight 
			//power in the opposite direction to oppose movement and brake
		}
		lastAngle = currentAngle;
		currentAngle = pincerEncoder.getValue();
		pincerMotor.set(ControlMode.PercentOutput, pincerPower);
		reflexTimer = reflexTimer+1;
	}
	public void displaySensorValues() {
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