package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.wrap.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Subsystem for controlling everything relating to the Manipulator
 */
public class Manipulator extends Subsystem {

    EncoderWrapper m_encoder;
    MotorGeneric m_motor;
    SolenoidWrapper m_sol;
    
    /**
     * Sets internal object assignments based on RobotMap values
     */
    public Manipulator() {
    	if(RobotMap.IS_ARM_TALON_CONTROLLED) {
    		TalonSRX t = new TalonSRX(RobotMap.ARM_TILT_MOTOR);
    		m_motor = new TalonWrapper(t);
    		m_encoder = new TalonEncoder(t);
    	} else {
    		Spark s = new Spark(RobotMap.ARM_TILT_MOTOR);
    		m_motor = new SparkWrapper(s);
    		m_encoder = new EncoderWrapper(RobotMap.ENCODER_ID_A, RobotMap.ENCODER_ID_B);
    	}
    	if(RobotMap.IS_DUAL_SOLENOID) {
    		m_sol = new SolenoidWrapper(RobotMap.CLAW_SOLENOID, RobotMap.CLAW_SOLENOID2);
    	} else {
    		m_sol = new SolenoidWrapper(RobotMap.CLAW_SOLENOID);
    	}
    }
    
    public void initDefaultCommand() {
        //TODO Add default command.
    }
    
    /**
     * Sets the speed for the tilt motor
     * @param speed
     */
    public void travel(double speed) {
    	m_motor.set(speed);
    }
    
    /**
     * @return The number of encoder counts registered by the attached encoder
     */
    public int getEncoderVal() {
    	return m_encoder.getCounts();
    }
    
    /**
     * Toggles the state of the claw
     */
    public void toggleClaw() {
    	m_sol.toggle();
    }

}

