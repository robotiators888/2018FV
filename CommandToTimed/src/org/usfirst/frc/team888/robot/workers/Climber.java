package org.usfirst.frc.team888.robot.workers;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

/**
 * Manipulates parts of the climber subsystem, manipulates the lights.
 */
public class Climber {

	private static Climber climber;
	
	protected OI oi;

	protected Spark lights;

	protected Spark climberMotorLeft;
	protected Spark climberMotorRight;

	protected Encoder climberEncoder;

	protected Solenoid climberPistonLeft;
	protected Solenoid climberPistonRight;

	protected boolean climberRaised = false;

	protected boolean lock;

	protected double time;
	
	protected boolean blueAlliance;

	/**
	 * Constructor
	 * @param p_oi OI Object
	 */
	private Climber() {
		oi = OI.getInstance();

		climberMotorLeft = new Spark(RobotMap.CLIMBER_MOTOR_LEFT);
		climberMotorRight = new Spark(RobotMap.CLIMBER_MOTOR_RIGHT);
		climberPistonLeft = new Solenoid(5, 0); //module number, channel
		climberPistonRight = new Solenoid(5, 1); //module number, channel

		climberEncoder = new Encoder(4, 5, true, CounterBase.EncodingType.k4X);

		time = 0;
		lights = new Spark(RobotMap.LIGHTS);
	}

	/**
	 * Accessor method for the Climber Singleton.
	 * @return The object of Climber
	 */
	public static Climber getInstance() {
		if (climber != null) {
			synchronized(Climber.class) {
				if (climber != null) {
					climber = new Climber();
				}
			}
		}
		
		return climber;
	}
	
	/**
	 * Run at the begining of auto and teleop
	 */
	public void climberInit() {
		climberEncoder.reset();
		blueAlliance = DriverStation.getInstance().getAlliance() == Alliance.Blue;
	}

	/**
	 * Run periodically in auto and teleop
	 */
	public void climberExecute() {

		// Determines the color of the lights
		if (oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			climberMoves(oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
			if (blueAlliance) lights.set(-0.09);
			else lights.set(-0.11);
		}
		
		else if (oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			climberMoves(-oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
			if (blueAlliance) lights.set(-0.09);
			else lights.set(-0.11);
		}
		
		else {
			climberMoves(0);
			if (climberEncoder.get() >= 1500) climberRaised = true;
			if (climberRaised && (climberEncoder.get() < 10)) lights.set(-0.57);
			else {
				if (blueAlliance) lights.set(0.87);
				else lights.set(0.61);
			}
		}

		// Opens and closed the piston to lock the climber
		if (oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			pneumaticLocking(lock);
			if (!lock) SmartDashboard.putString("Climber Status:", "Locked");
			else SmartDashboard.putString("Climber Status:", "Unlocked");

		}

		else if (oi.getGamepadButton(RobotMap.GP_R_BUTTON)) {
			lock = false;
			pneumaticLocking(lock);
			SmartDashboard.putBoolean("locked?", !lock);
			if (!lock) SmartDashboard.putString("Climber Status:", "Locked");
			else SmartDashboard.putString("Climber Status:", "Unlocked");
		}

		else {
			SmartDashboard.putBoolean("Locked?", !lock);
			if (!lock) SmartDashboard.putString("Climber Status:", "Locked");
			else SmartDashboard.putString("Climber Status:", "Unlocked");
		}

		SmartDashboard.putNumber("ClimberEncoder", climberEncoder.get());
	}

	/**
	 * Moves the climber
	 * @param speed How fast the climber will move
	 */
	public void climberMoves(double speed) {
		climberMotorLeft.set(speed);
		climberMotorRight.set(speed);
	}

    /**
     * Locks the climber
     * @param lock True for lock, False for unlock
     */
	public void pneumaticLocking(boolean lock) {
		climberPistonLeft.set(lock);
		climberPistonRight.set(lock);
	}
}