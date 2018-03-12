package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pincer extends Subsystem {

	protected OI oi;

	protected TalonSRX pincerMotor;
	protected DoubleSolenoid pincerPiston;
	
	protected Spark lights;

	protected AnalogInput pincerEncoder;

	protected DigitalInput proximity;
	protected DigitalInput topLimit;
	protected DigitalInput bottomLimit;
	protected DigitalInput bottomBanner;

	protected PowerDistributionPanel pdp;

	protected String pincerPosition;

	protected int pincerDesiredPosition;
	protected int pincerClicks;	

	protected boolean manualControl = false;

	protected double currentAngle = 0;
	protected double lastAngle = 0;
	protected double angleThreshold = 150;
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

	protected double desiredPosition = 2115;

	public Pincer(OI p_oi) {
		oi = p_oi;

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
		bottomBanner = new DigitalInput(3);

		lights = new Spark(RobotMap.LIGHTS);
	}
	
	public void pincerInit() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
	}

	public void pincerExecute() {

		if (!DriverStation.getInstance().isAutonomous()) {
			
			if(oi.getGamepadButton(1)){
				desiredPosition = 700;
			}
			if(oi.getGamepadButton(2)){
				desiredPosition = 1700;
			}
			if(oi.getGamepadButton(4)){
				desiredPosition = 2115;
			}
			setPincerPosition(desiredPosition, oi.getGamepadButton(8), oi.getGamepadAxis(1));

			pince(oi.getRightStickButton(3) || oi.getLeftStickButton(3));

			updateDashboard();
		}
	}

	public void setPincerPosition(double desiredAngle, boolean button, double axis){
		batteryVoltage = pincerMotor.getBusVoltage();

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

		}
		else{
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
			else {
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
		if(bottomLimit.get() || bottomBanner.get()){
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
			pincerPosition = "Open";
		} else {
			pincerPiston.set(DoubleSolenoid.Value.kReverse);
			pincerPosition = "Closed";
		}
		if(DriverStation.getInstance().isDisabled()){
			lights.set(1.0);
		}
		else{
			lights.set(-0.11);
		}

	}

	public void updateDashboard() {
		SmartDashboard.putString("Pincer Position", pincerPosition);
		SmartDashboard.putBoolean("Cube Present?", !proximity.get());
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}