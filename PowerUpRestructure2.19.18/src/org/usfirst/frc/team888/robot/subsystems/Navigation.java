package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;

	protected double maxOutput = 1.0;
	protected double leftSideAdjustment;
	protected double rightSideAdjustment;
	protected double desiredHeading;

	public Navigation(DriveTrain p_drive, DeadReckon p_location) {
		drive = p_drive;
		location = p_location;
	}
	
	public void navigationInit() {
		drive.resetEncoderPositions();
	}
	
	public void navigationExecute(double leftSpeed, double rightSpeed) {
		location.updateTracker();
		drive.move(leftSpeed, rightSpeed);
	}
	
	public double calculateDesiredHeading() {
		double[] pos = location.getPos();
		double[] posToDesired = {0,0};

		for (int i = 0; i < pos.length; i++) {
			posToDesired[i] = pos[i] - RobotMap.DESIRED_LOCATION[i];
		}

		desiredHeading = DeadReckon.absAngle(Math.atan2(posToDesired[0], posToDesired[1]));
		return desiredHeading;
	}

	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */

	public double[] getAdjustments() {	

		double[] navData = location.getNavLocationData();
		desiredHeading = calculateDesiredHeading();

		/**
		 * If the robot is moving in a positive direction...
		 */

		if ((navData[0] > 0) && (navData[1] > 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.absAngle(navData[2] - desiredHeading) >
			DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT)
						<= maxOutput) {			
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.absAngle(navData[2] - desiredHeading) <
					DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((RobotMap.RIGHT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= maxOutput) {			
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				}

				/**
				 * If the robot is already moving straight add no adjustments
				 */

			} else {
				leftSideAdjustment = 0.0;
				rightSideAdjustment = 0.0;
			}	

			/**
			 * If the robot is moving in a negative direction...
			 */

		} else if((navData[0] < 0) && (navData[1] < 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.absAngle(navData[2] - desiredHeading) <
					DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((RobotMap.LEFT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
			/* If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.absAngle(navData[2] - desiredHeading) >
			DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((RobotMap.RIGHT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				}

				/**
			/* If the robot is already moving straight add no adjustments
				 */	

			} else {
				leftSideAdjustment = 0.0;
				rightSideAdjustment = 0.0;
			}	

			/**
			 * If the robot is not moving or turning, add no adjustments.
			 */

		} else {
			leftSideAdjustment = 0.0;
			rightSideAdjustment = 0.0;
		}

		SmartDashboard.putNumber("Left Adjustments", leftSideAdjustment);
		SmartDashboard.putNumber("Right Adjustments", rightSideAdjustment);

		double[] adjustments = {
				leftSideAdjustment,
				rightSideAdjustment
		};
		return adjustments;		
	}


	/**
	 * @return The array with zeros for both adjustments
	 */

	public double[] reset() {
		double[] j = {0,0};
		return j;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}