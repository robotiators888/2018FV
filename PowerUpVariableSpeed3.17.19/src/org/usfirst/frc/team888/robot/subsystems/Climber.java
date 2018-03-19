package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Climber extends Subsystem {

	protected OI oi;
	
	protected Spark lights;

	protected Spark climberMotorLeft;
	protected Spark climberMotorRight;

	protected Solenoid climberPistonLeft;
	protected Solenoid climberPistonRight;

	protected boolean lock;

	protected double time;

	public Climber(OI p_oi) {
		oi = p_oi;

		climberMotorLeft = new Spark(RobotMap.CLIMBER_MOTOR_LEFT);
		climberMotorRight = new Spark(RobotMap.CLIMBER_MOTOR_RIGHT);
		climberPistonLeft = new Solenoid(5, 0); //module number, channel
		climberPistonRight = new Solenoid(5, 1); //module number, channel

		time = 0;
		lights = new Spark(RobotMap.LIGHTS);
	}

	public void climberExecute() {

		if (oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			climberMoves(oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
			lights.set(-0.07);
		} else if (oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			climberMoves(-oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
			lights.set(-0.07);
		} else {
			climberMoves(0);
		}

		if (oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			pneumaticLocking(lock);


			if(!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}

		} else if (oi.getGamepadButton(RobotMap.GP_R_BUTTON)) {
			lock = false;
			pneumaticLocking(lock);

			SmartDashboard.putBoolean("locked?", !lock);

			if(!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}

		} else {
			SmartDashboard.putBoolean("Locked?", !lock);
			if (!lock){
				SmartDashboard.putString("Climber Status:", "Locked");
			} else {
				SmartDashboard.putString("Climber Status:", "Unlocked");
			}
		}
	}

	public void climberMoves(double speed) {
		climberMotorLeft.set(speed);
		climberMotorRight.set(speed);
	}

	public void pneumaticLocking(boolean lock) {
		climberPistonLeft.set(lock);
		climberPistonRight.set(lock);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}