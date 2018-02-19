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

	
	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);

		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		pincerMotor.configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		
		pincerPiston = new DoubleSolenoid(5, 2, 3);
		
		pincerEncoder = new AnalogInput(0);
    	pincerEncoder.setOversampleBits(2);
    	pincerEncoder.setAverageBits(2);
    	
		pincerPiston = new DoubleSolenoid(5,2,3);
		
		proximity = new DigitalInput(0);
		
		topLimit = new DigitalInput(1);
		bottomLimit = new DigitalInput(2);
	}

	public void pincerInit() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
	}
	
	public void setPincerPosition(double pincerSpeed) {
		pincerMotor.set(ControlMode.PercentOutput, pincerSpeed);
		SmartDashboard.putNumber("pincer output", pincerSpeed);
	}
	
	public void displaySensorValues() {
		//SmartDashboard.putNumber("Pincer Encoder", pincerClicks);
		SmartDashboard.putBoolean("proximity", !proximity.get());
		SmartDashboard.putNumber("analogEncoderRaw", pincerEncoder.getValue());
		SmartDashboard.putBoolean("topLimit", topLimit.get());
		SmartDashboard.putBoolean("bottomLimit", bottomLimit.get());
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