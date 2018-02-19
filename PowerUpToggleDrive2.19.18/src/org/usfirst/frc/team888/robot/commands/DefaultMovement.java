package org.usfirst.frc.team888.robot.commands;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.Climber;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;
import org.usfirst.frc.team888.robot.subsystems.Pincer;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Command that maintains manual control over the robot as long as it is not
 * being controlled by another command affecting the DriveTrain subsystem.
 */
public class DefaultMovement extends Command {

	DriveTrain dt;
	Climber m_climb;
	Pincer m_pince;
	
	boolean lock = false;
	double leftBaseDriveOutput = 0.0;
	double rightBaseDriveOutput = 0.0;	
	double leftDriveOutput = 0.0;
	double rightDriveOutput = 0.0;
    boolean input = false;
    boolean lastInput = false;
    boolean output = false;
    boolean press = false;

	public DefaultMovement() {
		requires(Robot.drive);
		this.dt = Robot.drive;

		/** NOT SURE IF THIS IS HOW TO IMPLEMENT THE CLIMBER OR PINCER CLASS**/
		requires(Robot.climb);
		this.m_climb = Robot.climb;

		requires(Robot.pincer);
		this.m_pince = Robot.pincer;

	}

	// Called just before this Command runs the first time
	protected void initialize() {
		dt.startCompressor();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
        if(Robot.oi.getTriggers()) {
        	leftBaseDriveOutput = Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
        	rightBaseDriveOutput = Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS);
        }
        else {
        	leftBaseDriveOutput = 0.7*Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
        	rightBaseDriveOutput = 0.7*Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}
        if(Math.abs(Robot.oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.3 &&  Math.abs(Robot.oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.3){
        	leftBaseDriveOutput = 0.0;
        	rightBaseDriveOutput = 0.0;
		}
		if(input == true && lastInput == false) {
            press = true;
        	}
        else {
            press = false;
        	}
        if(press) {
            output = !output;
        	}
        lastInput = input;
        input = Robot.oi.getLeftStickButton(2) || Robot.oi.getRightStickButton(2);
        if(output) {
			leftDriveOutput = leftBaseDriveOutput;
			rightDriveOutput = rightBaseDriveOutput;
        	}
        else {
			leftDriveOutput = -rightBaseDriveOutput;
			rightDriveOutput = -leftBaseDriveOutput;
        	}
        SmartDashboard.putNumber("leftOutput", leftDriveOutput);
        SmartDashboard.putNumber("rightOutput", rightDriveOutput);       
		dt.move(leftDriveOutput, rightDriveOutput);

		//Climber moves via gamepad triggers
		if (Robot.oi.getGamepadAxis(RobotMap.GP_L_TRIGGER) > 0.2) {
			m_climb.climberMoves(Robot.oi.getGamepadAxis(RobotMap.GP_L_TRIGGER));
		} else if (Robot.oi.getGamepadAxis(RobotMap.GP_R_TRIGGER) > 0.2) {
			m_climb.climberMoves(-Robot.oi.getGamepadAxis(RobotMap.GP_R_TRIGGER));
		} else {
			m_climb.climberMoves(0);
		}

		m_pince.testPincer();
		
		m_pince.setPincerPosition(Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) * 0.4);
		
		m_pince.pince();
		
		if(Robot.oi.getGamepadButton(RobotMap.GP_L_BUTTON)) {
			lock = true;
			m_climb.pneumaticLocking(lock);
			SmartDashboard.putBoolean("locked?", !lock);
			if(!lock){
				SmartDashboard.putString("AJ is Needy", "Locked");
			}
			else{
				SmartDashboard.putString("AJ is Needy", "Unlocked");
			}
		} else if (Robot.oi.getGamepadButton(RobotMap.GP_R_BUTTON)) {
			lock = false;
			m_climb.pneumaticLocking(lock);
			SmartDashboard.putBoolean("locked?", !lock);
			if(!lock){
				SmartDashboard.putString("AJ is Needy", "Locked");
			}
			else{
				SmartDashboard.putString("AJ is Needy", "Unlocked");
			}
		} else {
			SmartDashboard.putBoolean("locked?", !lock);
			if(!lock){
				SmartDashboard.putString("AJ is Needy", "Locked");
			}
			else{
				SmartDashboard.putString("AJ is Needy", "Unlocked");
			}
			//m_climb.pneumaticLocking(lock);
		}
		
	}

	// Sets this command to never end.
	protected boolean isFinished() {
		return false;
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		dt.move(0, 0); //Stops motor controllers so they are ready to be taken over by other command.
	}
}
