package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import java.lang.Object;
import edu.wpi.first.wpilibj.DriverStation;

public class Climber extends Subsystem {

	protected OI oi;

	protected Spark climberMotorLeft;
	protected Spark climberMotorRight;
	protected Spark lights;
	protected DriverStation newDriverStation;

	protected Solenoid climberPistonLeft;
	protected Solenoid climberPistonRight;

	protected boolean lock;
	protected boolean climbingLights;

	protected double matchTime;

	protected double time;

	public Climber(OI p_oi) {
		oi = p_oi;

		climberMotorLeft = new Spark(RobotMap.CLIMBER_MOTOR_LEFT);
		climberMotorRight = new Spark(RobotMap.CLIMBER_MOTOR_RIGHT);
		climberPistonLeft = new Solenoid(5, 0); //module number, channel
		climberPistonRight = new Solenoid(5, 1); //module number, channel
		matchTime = newDriverStation.getMatchTime();
		lights = new Spark(RobotMap.LIGHTS);


		time = 0;
	}

	public void climberExecute() {

		if (climbingLights) {
			lights.set(RobotMap.fastRainbow);
		} 
		
		if (oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			climberMoves(oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
			climbingLights = true;
		} else if (oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			climberMoves(-oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
			climbingLights = true;
		} else {
			climberMoves(0);
			climbingLights = false;
		}

		if (oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			pneumaticLocking(lock);
			lights.set(RobotMap.strobeGold);					//sets lights to strobe gold when pneumatics is locked


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


		//If match time is 90 seconds, fast rainbow will light up
		/*if (matchTime == 90) {
			lights.set(RobotMap.fastRainbow);
		}*/
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