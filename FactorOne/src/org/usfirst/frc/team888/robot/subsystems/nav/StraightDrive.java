package org.usfirst.frc.team888.robot.subsystems.nav;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Defines the getAdjustmentsForStraight() method for Straight(TM) driving. Highly optimized version of the Biegel/Chuck code.
 */
public interface StraightDrive {
	
	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */
	public default double[] getAdjustmentsForStraight() {	
		double[] adjustments = null;
		double[] changeInEncoders = {
				Robot.encoders.getChangeInEncoderLeft(),
				Robot.encoders.getChangeInEncoderRight(),
				Math.abs(Robot.encoders.getChangeInEncoderLeft()),
				Math.abs(Robot.encoders.getChangeInEncoderRight())
		};

		//If the robot is moving in a positive direction...
		if ((changeInEncoders[0] > 0) && (changeInEncoders[1] > 0)) {
			
			//If the left side is moving slower than right...
			if(changeInEncoders[0] < changeInEncoders[1]) {
				
				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				
				adjustments = ((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= RobotMap.MAX_AUTO_OUTPUT) 
						? new double[] {RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0} 
						: new double[] {0.0, -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT};
			
			//If the right side is moving slower than left...						
			} else if (changeInEncoders[0] > changeInEncoders[1]) {
				
				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */
				
				adjustments = ((RobotMap.RIGHT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= RobotMap.MAX_AUTO_OUTPUT)
						? new double[] {0.0, RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT}
						: new double[] {-RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0};
			}
			
		//If the robot is moving in a negative direction...
		} else if((changeInEncoders[0] < 0) && (changeInEncoders[1] < 0)) {

			//If the left side is moving slower than right...
			if(changeInEncoders[2] > changeInEncoders[3]) {
				
				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				
				adjustments = ((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= RobotMap.MAX_AUTO_OUTPUT) 
						? new double[] {RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0} 
						: new double[] {0.0, -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT};
			
			//If the right side is moving slower than left...
			} else if (changeInEncoders[2] < changeInEncoders[3]) {
				
				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */
				
				adjustments = ((RobotMap.RIGHT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= RobotMap.MAX_AUTO_OUTPUT)
						? new double[] {0.0, RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT}
						: new double[] {-RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0};
			}
		}
		
		//If the adjsutments are not assigned (eg, if the robot's not moving, or already moving straight) assign no adjustments.
		if(adjustments == null) adjustments = new double[] {0.0, 0.0};
		
		SmartDashboard.putNumber("Left Adjustments", adjustments[0]);
		SmartDashboard.putNumber("Right Adjustments", adjustments[1]);
		
		return adjustments;
	}
}