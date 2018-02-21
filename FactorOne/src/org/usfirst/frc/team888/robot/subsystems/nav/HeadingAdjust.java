package org.usfirst.frc.team888.robot.subsystems.nav;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Optimized implementation of Charlie's HeadingAdjust class.
 */
public interface HeadingAdjust {
	
	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @param desiredWaypoint The waypoint that needs to be pointed to. Only [x, y] needed.
	 * @return An array containing the adjustments for the left and right sides in that order
	 */
	default double[] getAdjustments(double[] desiredWaypoint) {	
		double[] adjustments = null;
		double[] changeInEncoders = {
				Robot.encoders.getChangeInEncoderLeft(),
				Robot.encoders.getChangeInEncoderRight()
		};
		double heading = Robot.encoders.getHeading();
		double desiredHeading = calculateDesiredHeading(desiredWaypoint);
		
		//If the robot is moving in a positive direction...
		if ((changeInEncoders[0] > 0) && (changeInEncoders[1] > 0)) {

			//If the left side is moving slower than right...			
			if (AngleMath.absAngle(heading - desiredHeading) > AngleMath.absAngle(desiredHeading - heading)) {
				
				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				adjustments = ((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= RobotMap.MAX_AUTO_OUTPUT) //if...
						? new double[] {RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0} //true
						: new double[] {0.0, -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT}; //false
			
			//If the right side is moving slower than left...	
			} else if (AngleMath.absAngle(heading - desiredHeading) < AngleMath.absAngle(desiredHeading - heading)) {
				
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
			if (AngleMath.absAngle(heading - desiredHeading) < AngleMath.absAngle(desiredHeading - heading)) {
				
				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */
				adjustments = ((RobotMap.LEFT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -RobotMap.MAX_AUTO_OUTPUT)
						? new double[] {-RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0}
						: new double[] {0.0, RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT};
				
			//If the right side is moving slower than left...
			} else if (AngleMath.absAngle(heading - desiredHeading) > AngleMath.absAngle(desiredHeading - heading)) {
				
				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */
				adjustments = ((RobotMap.RIGHT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -RobotMap.MAX_AUTO_OUTPUT)
						? new double[] {0.0, -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT}
						: new double[] {RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT, 0.0};
						
			} 			
		} 
		
		//If the adjustments are not assigned (eg, if the robot's not moving, or already moving straight) assign no adjustments.
		if(adjustments == null) adjustments = new double[] {0.0, 0.0};
		
		SmartDashboard.putNumber("Left Adjustments", adjustments[0]);
		SmartDashboard.putNumber("Right Adjustments", adjustments[1]);
		
		return adjustments;		
	}
	
	/**
	 * Given a set of waypoints, calculates the heading the robot needs to facing to get there.
	 * @return The desired heading of the robot in radians
	 */
	default double calculateDesiredHeading(double[] desiredWaypoint) {
		double[] pos = Robot.encoders.getPos();
		double[] posToDesired = {0,0};
		for (int i = 0; i < pos.length; i++) posToDesired[i] = pos[i] - desiredWaypoint[i];
		return AngleMath.absAngle(Math.atan2(posToDesired[0], posToDesired[1]));
	}
}