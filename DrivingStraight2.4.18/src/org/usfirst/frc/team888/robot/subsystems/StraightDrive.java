package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class StraightDrive extends Subsystem {

	
	double driveStraightAdjustAmount = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
	 
	double leftBaseOutput = RobotMap.LEFT_AUTO_SPEED;
	double rightBaseOutput = RobotMap.RIGHT_AUTO_SPEED;
	double maxOutput = 1.0;
	
	double leftSideAdjustment, rightSideAdjustment;
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
       }
    
    
	
	public double[] updateEncoderVals() {
		
		double[] i = {
				Robot.encoders.getChangeInEncoderLeft(),
				Robot.encoders.getChangeInEncoderRight(),
				Math.abs(Robot.encoders.getChangeInEncoderLeft()),
				Math.abs(Robot.encoders.getChangeInEncoderRight())};
		
		return i;		
	}
	
	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */
	
	public double[] getAdjustments() {	
		
		double[] changeInEncoders = updateEncoderVals();

		/**
		 * If the robot is moving in a positive direction...
		 */
		
		if ((changeInEncoders[0] > 0) && (changeInEncoders[1] > 0)) {

			/**
			 * If the left side is moving slower than right...
			 */
			
			if(changeInEncoders[0] < changeInEncoders[1]) {
				
				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				
				if 	((leftBaseOutput + driveStraightAdjustAmount) <= maxOutput) {			
					leftSideAdjustment = driveStraightAdjustAmount;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = -driveStraightAdjustAmount;
					leftSideAdjustment = 0.0;
				}
			}
			
			if(changeInEncoders[0] > changeInEncoders[1]) {
				
				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */
				
				if 	((rightBaseOutput + driveStraightAdjustAmount) <= maxOutput) {			
					rightSideAdjustment = driveStraightAdjustAmount;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -driveStraightAdjustAmount;
					rightSideAdjustment = 0.0;
				}
			}

		/**
		 * If the robot is moving in a negative direction...
		 */

		} else if((changeInEncoders[0] < 0) && (changeInEncoders[1] < 0)) {

			if(changeInEncoders[2] > changeInEncoders[3]) {
				
				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				
				if ((leftBaseOutput - driveStraightAdjustAmount) >= -maxOutput) {
					leftSideAdjustment = -driveStraightAdjustAmount;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = driveStraightAdjustAmount;
					leftSideAdjustment = 0.0;
				}
			}
			
			if(changeInEncoders[2] < changeInEncoders[3]) {
				
				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */
				
				if ((rightBaseOutput - driveStraightAdjustAmount) >= -maxOutput) {
					rightSideAdjustment = -driveStraightAdjustAmount;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = driveStraightAdjustAmount;
					rightSideAdjustment = 0.0;
				}
			}
		
		/**
		 * If the robot is not moving or is moving straight already add no adjustments.	
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
	
}