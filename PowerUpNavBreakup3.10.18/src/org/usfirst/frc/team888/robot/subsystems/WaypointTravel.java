package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class WaypointTravel extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;
	
	protected double maxOutput = 1.0;
	
	protected int state = 0;
	
	public WaypointTravel (DriveTrain p_drive, DeadReckon p_location) {
		drive = p_drive;
		location = p_location;
		
		state = 0;
	}
	
	
	public boolean goToWaypoint(double desiredX, double desiredY, double desiredHeading) {
		double[] pos = location.getPos();
		double heading = location.getHeading();
		boolean arrived = false;
		double headingDifference = DeadReckon.modAngle(desiredHeading - heading);

		switch (state) {
		case 0:
			if ((Math.abs(desiredX - pos[0]) < 6) && 
					(Math.abs(desiredY - pos[1]) < 6)) {
				drive.move(0.0, 0.0);
				state = 1;
			}
			else {
				double[] adjustments = moveToWaypoint(desiredX, desiredY);
				drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
						RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
			}
			break;
		case 1:
			if (headingDifference > Math.PI) {
				headingDifference = Math.PI - headingDifference;
			}
			if (Math.abs(headingDifference) > (Math.PI / 48)) {
				double[] rotationSpeed = moveToOrientation(desiredHeading);
				drive.move(rotationSpeed[0], rotationSpeed[1]);
			}
			else {
				drive.move(0.0, 0.0);
				state = 2;
			}
			break;
		case 2:
			arrived = true;
			state = 0;
		default:;
		}

		SmartDashboard.putNumber("waypoint state", state);
		SmartDashboard.putBoolean("arrived", arrived);
		
		return arrived;
	}
	
	public double[] moveToWaypoint(double desiredX, double desiredY) {
		String direction = location.getDirection();
		double heading = location.getHeading();
		double rightSideAdjustment = 0;
		double leftSideAdjustment = 0;
		double[] targetData = calculateTurn(desiredX, desiredY);


		/**
		 * If the robot is moving in a positive direction...
		 */

		if (direction.equals("forward")) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(heading - targetData[0]) >
					DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + targetData[1])
						<= maxOutput) {
					leftSideAdjustment = targetData[1];
					rightSideAdjustment = 0.0;

				}
				else {
					rightSideAdjustment = -targetData[1];
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(heading - targetData[0]) <
			DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((RobotMap.RIGHT_AUTO_SPEED + targetData[1]) <= maxOutput) {			
					rightSideAdjustment = targetData[1];
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -targetData[1];
					rightSideAdjustment = 0.0;
				}
			}

			/**
			 * If the robot is moving in a negative direction...
			 */

		} else if(direction.equals("backward")) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(heading - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((RobotMap.LEFT_AUTO_SPEED - targetData[1]) >= -maxOutput) {
					leftSideAdjustment = -targetData[1];
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = targetData[1];
					leftSideAdjustment = 0.0;
				}

				/**
 			/* If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(heading - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((RobotMap.RIGHT_AUTO_SPEED - targetData[1]) >= -maxOutput) {
					rightSideAdjustment = -targetData[1];
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = targetData[1];
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

		}

		else {
			leftSideAdjustment = 0.0;
			rightSideAdjustment = 0.0;
		}

		double[] adjustments = {
				leftSideAdjustment,
				rightSideAdjustment
		};

		SmartDashboard.putNumber("left adjustment", leftSideAdjustment);
		SmartDashboard.putNumber("right adjustment", rightSideAdjustment);
		
		return adjustments;		

	}

	public double[] moveToOrientation(double desiredHeading) {
		double heading = location.getHeading();
		double leftTurnSpeed = 0;
		double rightTurnSpeed = 0;

		if (DeadReckon.modAngle(heading - desiredHeading) >=
				DeadReckon.modAngle(desiredHeading - heading)) {

			leftTurnSpeed = RobotMap.LEFT_AUTO_SPEED * 1.5;
			rightTurnSpeed = -RobotMap.RIGHT_AUTO_SPEED * 1.5;

		}
		
		else {

			leftTurnSpeed = -RobotMap.LEFT_AUTO_SPEED * 1.5;
			rightTurnSpeed = RobotMap.RIGHT_AUTO_SPEED * 1.5;		
		}

		double[] turnSpeeds = {
				leftTurnSpeed,
				rightTurnSpeed
		};

		SmartDashboard.putNumber("left adjustment", leftTurnSpeed);
		SmartDashboard.putNumber("right adjustment", rightTurnSpeed);
		
		return turnSpeeds;	
	}
	
	/**Finds the heading and adjustments to add for waypoint movement
	 * @return An array with the heading the robot should travel and the adjustment to add to the motor output
	 */
	public double[] calculateTurn(double desiredX, double desiredY) {
		double heading = location.getHeading();
		
		//Calculates the direction the robot should travel in to get to the next waypoint.
		double[] pos = location.getPos();

		double desiredHeading = DeadReckon.modAngle(Math.atan2(desiredX - pos[0],
				desiredY - pos[1]));
		
		double headingDifference = DeadReckon.modAngle(desiredHeading - heading);
		if (headingDifference > Math.PI) {
			headingDifference = Math.PI - headingDifference;
		}
		
		//Calculates the adjustment based on how much the robot needs to turn
		double driveAdjustment = 0.26666; //Math.max(0.03, Math.min(0.3, (Math.abs(headingDifference) / Math.PI * 0.3)));

		double[] i = {
				desiredHeading,
				driveAdjustment
		};

		SmartDashboard.putNumber("desired x", desiredX);
		SmartDashboard.putNumber("desired y", desiredY);
		SmartDashboard.putNumber("desired heading", Math.toDegrees(desiredHeading));
		SmartDashboard.putNumber("ajustment proportion", driveAdjustment);
		
		return i;
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}