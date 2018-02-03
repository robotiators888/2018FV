package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class StraightDrive extends Subsystem {

	double driveStraightAdjustAmount = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
	double leftBaseOutput = RobotMap.LEFT_AUTO_SPEED;
	double rightBaseOutput = RobotMap.RIGHT_AUTO_SPEED;
	double maxOutput = 1.0;
	
	double leftSideAdjustment, rightSideAdjustment;
	
	public double[] updateEncoderVals() {
		
		double[] i = {
				Robot.encoders.getChangeInEncoderLeft(),
				Robot.encoders.getChangeInEncoderRight(),
				Math.abs(Robot.encoders.getChangeInEncoderLeft()),
				Math.abs(Robot.encoders.getChangeInEncoderRight())};
		
		return i;		
	}
	
	public double[] getAdjustments() {	

		double[] changeInEncoders = updateEncoderVals();

		/**
		 * If the robot is moving in a positive direction...
		 */
		if ((changeInEncoders[0] > 0) && (changeInEncoders[1] > 0)) {

			if((leftBaseOutput + driveStraightAdjustAmount) <= maxOutput){
				if(changeInEncoders[0] < changeInEncoders[1]){
					leftSideAdjustment = driveStraightAdjustAmount;
				} else{
					rightSideAdjustment = driveStraightAdjustAmount;
				}
			}

			if((rightBaseOutput + driveStraightAdjustAmount) <= maxOutput){

				if(changeInEncoders[1] < changeInEncoders[0]){
					rightSideAdjustment = driveStraightAdjustAmount;
				} else{
					rightSideAdjustment = 0.0;
				}
			}
			//------------------IF OUTPUT IS GREATER THAN MAX OUTPUT--------------------------------------------------

			if((leftBaseOutput + driveStraightAdjustAmount) > maxOutput){
				if(changeInEncoders[0] < changeInEncoders[1]){
					rightSideAdjustment = -driveStraightAdjustAmount;
				}  else{
					rightSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) > maxOutput){

				if(changeInEncoders[1] < changeInEncoders[0]){
					leftSideAdjustment = -driveStraightAdjustAmount;
				} else{
					leftSideAdjustment = 0.0;
				}
			}
			///////////-----Backwards-----//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		} else if((changeInEncoders[0] < 0) && (changeInEncoders[1] < 0)) {

			if((leftBaseOutput + driveStraightAdjustAmount) <= maxOutput){
				if(changeInEncoders[2] < changeInEncoders[3]){
					leftSideAdjustment = (driveStraightAdjustAmount);
				} else{
					leftSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) <= maxOutput){

				if(changeInEncoders[3] < changeInEncoders[2]){
					rightSideAdjustment = (driveStraightAdjustAmount);
				} else{
					rightSideAdjustment = 0.0;
				}
			}
			//------------------IF OUTPUT IS GREATER THAN MAX OUTPUT-----------------------------------------------------------------

			if((leftBaseOutput + driveStraightAdjustAmount) > maxOutput){
				if(changeInEncoders[2] < changeInEncoders[3]){
					rightSideAdjustment = -driveStraightAdjustAmount;
				}			
				else{
					rightSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) > maxOutput){

				if(changeInEncoders[3] < changeInEncoders[2]){
					leftSideAdjustment = -driveStraightAdjustAmount;
				}
				else{
					leftSideAdjustment = 0.0;
				}

			}
		}
		
		double[] adjustments = new double[1];
		adjustments[0] = leftSideAdjustment;
		adjustments[1] = rightSideAdjustment;
		return adjustments;
	}

	public double[] reset() {
		double[] j = {0,0};
		return j;
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

