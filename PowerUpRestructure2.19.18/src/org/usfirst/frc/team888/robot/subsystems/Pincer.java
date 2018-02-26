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
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	protected double angleThreshold = 100;
	protected double pincerPower = 0;
	protected double manualPower = 0;
	protected double maintainerConstant = 0;
	protected double movementThreshold = 30;
	protected double maintainerConstantIterator = 0.001;
	protected double maxSpeed = 0;
	protected double reflexHigh = 0.5;
	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean press = false;
	protected boolean output = false;
	protected int reflexLength = 1000;
	protected int reflexTimer = 0;
	protected int reflexStart = 0;
	
	protected double batteryVoltage = 0;
	protected double maxBatteryVoltage = 12.0;
	protected double withCubePercent = 0.55;
	protected double withoutCubePercent = 0.35;
	protected double withCubeReflex = 0.15;
	protected double withoutCubeReflex = 0.05;

	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);

		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		pincerMotor.configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);

		pincerPiston = new DoubleSolenoid(5, 2, 3);

		pincerEncoder = new AnalogInput(0);
		pincerEncoder.setOversampleBits(2);
		pincerEncoder.setAverageBits(2);
		
		proximity = new DigitalInput(0);

		bottomLimit = new DigitalInput(2);
		topLimit = new DigitalInput(1);
	}

	public void pincerInit() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
	}
	public void setPincerPosition(double desiredAngle, boolean button, double axis){
		batteryVoltage = pincerMotor.getBusVoltage();
		SmartDashboard.putNumber("busVoltage", pincerMotor.getBusVoltage());
		SmartDashboard.putNumber("currentDraw", pincerMotor.getOutputCurrent());
		SmartDashboard.putNumber("desiredAngle", desiredAngle);
		SmartDashboard.putNumber("currentAngle", currentAngle);
		SmartDashboard.putNumber("maintainerConstant", maintainerConstant);
		SmartDashboard.putNumber("pincerPower", pincerPower);
		SmartDashboard.putNumber("manualPower", manualPower);
		SmartDashboard.putNumber("timer", reflexTimer);
		
		if (proximity.get()){
			maxSpeed = withoutCubePercent*(batteryVoltage/maxBatteryVoltage);
		} else {
			maxSpeed = withCubePercent*(batteryVoltage/maxBatteryVoltage);
		}

		if(currentAngle > (desiredAngle + angleThreshold)){
			if(Math.abs(currentAngle - lastAngle) < movementThreshold){
				maintainerConstant = maintainerConstant + maintainerConstantIterator;
			}
			
			pincerPower = maintainerConstant*Math.abs(currentAngle - desiredAngle);
			
			if(pincerPower > (maxSpeed-0.05)){
				pincerPower = maxSpeed-0.05;
			}
			reflexStart = reflexTimer;
			SmartDashboard.putBoolean("I'm here", false);
			//stuff to bring down to angle
		}
		else if(currentAngle < (desiredAngle - angleThreshold)){
			if(Math.abs(currentAngle - lastAngle) < movementThreshold){
				maintainerConstant = maintainerConstant + maintainerConstantIterator;
			}
			pincerPower = maintainerConstant*Math.abs(currentAngle - desiredAngle);
			if(pincerPower > maxSpeed){
				pincerPower = maxSpeed;
			}
			pincerPower = -pincerPower;
			//stuff to bring up to angle
			reflexStart = reflexTimer;
			SmartDashboard.putBoolean("I'm here", false);
		}
		else{
			SmartDashboard.putBoolean("I'm here", true);
			pincerPower = (-pincerPower*Math.abs(currentAngle - desiredAngle)*(reflexHigh)*0.0025)/Math.abs(pincerPower);
			if(reflexTimer-reflexStart > reflexLength){
				maintainerConstant = 0;
				pincerPower = 0;
			}
			if(reflexTimer-reflexStart < reflexLength){
				if(!proximity.get()){
					pincerPower = -withCubeReflex*(batteryVoltage/maxBatteryVoltage);
				}
				if(proximity.get()){
					pincerPower = -withoutCubeReflex*(batteryVoltage/maxBatteryVoltage);
				}
			}
			else{
				pincerPower = 0;
			}
			//stuff to maintain position, want to do a thing where it puts slight 
			//power in the opposite direction to oppose movement and brake
		}
		lastAngle = currentAngle;
		currentAngle = pincerEncoder.getValue();
		reflexTimer = reflexTimer+1;
		manualPower = 0.55*axis;
		if(topLimit.get()){
			if(pincerPower < 0){
				pincerPower = 0;
			}
			if(manualPower < 0){
				manualPower = 0;
			}
		}
		if(bottomLimit.get()){
			if(pincerPower > 0.16){
				pincerPower = 0;
			}
			if(manualPower > 0){
				manualPower = 0;
			}
		}
		if(button){
		pincerMotor.set(ControlMode.PercentOutput, pincerPower);
		}
		else{
			pincerMotor.set(ControlMode.PercentOutput, manualPower);
		}
	}
	public void displaySensorValues() {
		SmartDashboard.putBoolean("proximity", proximity.get());
	}

	//Uses pistons to close pincer
	public void pince(boolean button) {
		if (input == true && lastInput == false) {
			press = true;
		} else {
			press = false;
		}

		if (press) {
			output = !output;
		}

		lastInput = input;
		input = button;

		if(output) {
			pincerPiston.set(DoubleSolenoid.Value.kForward);
		} else {
			pincerPiston.set(DoubleSolenoid.Value.kReverse);
		}

	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}