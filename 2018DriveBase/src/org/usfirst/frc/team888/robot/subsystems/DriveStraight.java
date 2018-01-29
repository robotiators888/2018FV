package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.EncoderScheduler;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveStraight extends Subsystem {

	private double leftSideAdjustment = 0.0;
	private double rightSideAdjustment = 0.0;
	private double driveStraightAdjustAmount = 0.1;
	private double rightBaseOutput = 0.85;
	private double leftBaseOutput = 0.85;
	private double leftSpeed = -700;
	private double rightSpeed = - 600;
	private double backwardsLeftSpeed = Math.abs(leftSpeed);
	private double backwardsRightSpeed = Math.abs(rightSpeed);
	private double maxOutput = 1.0;


	public int getLeftEncoder() {
		Encoder leftEncoder = new Encoder(2, 3, true, CounterBase.EncodingType.k4X);
		int leftEncoderValue = leftEncoder.get();

		return leftEncoderValue;
	}

	public int getRightEncoder() {
		Encoder rightEncoder = new Encoder(0, 1, false, CounterBase.EncodingType.k4X);
		int rightEncoderValue = rightEncoder.get();

		return rightEncoderValue;

	}

	public double[] driveStraight(double encoderValueLeft, double encoderValueRight) {	

		encoderValueLeft = leftSpeed;
		encoderValueRight = rightSpeed;
		////////////-----FORWARDS-----//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if ((leftSpeed > 0) && (rightSpeed > 0)) {

			if((leftBaseOutput + driveStraightAdjustAmount) <= maxOutput){
				if(leftSpeed < rightSpeed){
					leftSideAdjustment = driveStraightAdjustAmount;
				} else{
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) <= maxOutput){

				if(rightSpeed < leftSpeed){
					rightSideAdjustment = driveStraightAdjustAmount;
				} else{
					rightSideAdjustment = 0.0;
				}
			}
			//------------------IF OUTPUT IS GREATER THAN MAX OUTPUT--------------------------------------------------

			if((leftBaseOutput + driveStraightAdjustAmount) > maxOutput){
				if(leftSpeed < rightSpeed){
					rightSideAdjustment = -driveStraightAdjustAmount;
				}  else{
					rightSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) > maxOutput){

				if(rightSpeed < leftSpeed){
					leftSideAdjustment = -driveStraightAdjustAmount;
				} else{
					leftSideAdjustment = 0.0;
				}
			}
			///////////-----Backwards-----//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		} else if((leftSpeed < 0) && (rightSpeed < 0)) {

			if((leftBaseOutput + driveStraightAdjustAmount) <= maxOutput){
				if(backwardsLeftSpeed < backwardsRightSpeed){
					leftSideAdjustment = (driveStraightAdjustAmount);
				} else{
					leftSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) <= maxOutput){

				if(backwardsRightSpeed < backwardsLeftSpeed){
					rightSideAdjustment = (driveStraightAdjustAmount);
				} else{
					rightSideAdjustment = 0.0;
				}
			}
			//------------------IF OUTPUT IS GREATER THAN MAX OUTPUT-----------------------------------------------------------------

			if((leftBaseOutput + driveStraightAdjustAmount) > maxOutput){
				if(backwardsLeftSpeed < backwardsRightSpeed){
					rightSideAdjustment = -driveStraightAdjustAmount;
				}			
				else{
					rightSideAdjustment = 0.0;
				}
			}
			if((rightBaseOutput + driveStraightAdjustAmount) > maxOutput){

				if(backwardsRightSpeed < backwardsLeftSpeed){
					leftSideAdjustment = -driveStraightAdjustAmount;
				}
				else{
					leftSideAdjustment = 0.0;
				}

			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		System.out.println("Left side: " + leftSpeed + "\tAdjustment: " + leftSideAdjustment);
		System.out.println("Right side " + rightSpeed + "\tAdjustment: " + rightSideAdjustment);
		System.out.println(leftSideAdjustment + rightSideAdjustment);

		double[] adjustments = new double[2];
		adjustments[0] = leftSideAdjustment;
		adjustments[1] = rightSideAdjustment;
		return adjustments;
	}


	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub

	}
}
