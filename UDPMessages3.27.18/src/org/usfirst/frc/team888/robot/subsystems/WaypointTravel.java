package org.usfirst.frc.team888.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WaypointTravel extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;

	protected int state = 0;

	public WaypointTravel (DriveTrain p_drive, DeadReckon p_location) {
		drive = p_drive;
		location = p_location;

		state = 0;
	}

	/**
	 * Moves the robot to a waypoint
	 * @param desiredX The X position of the waypoint
	 * @param desiredY The Y position of the waypoint
	 * @param desiredHeading The heading of the waypoint
	 * @return Whether or not the robot has arrived at the waypoint
	 */
	public boolean goToWaypoint(double desiredX, double desiredY, double desiredHeading, double speed) {
		// Finds where the robot is on the field
		double[] pos = location.getPos();
		double heading = location.getHeading();
		// Initializes the arrived boolean to false
		boolean arrived = false;
		// Finds the difference between the current and desired headings
		double headingDifference = DeadReckon.modAngle(desiredHeading - heading);

		switch (state) {
		case 0:
			// If the robot is within 6 inches of the target waypoint...
			if ((Math.abs(desiredX - pos[0]) < 6) && 
					(Math.abs(desiredY - pos[1]) < 6)) {
				// ...stop moving and go to the next step.
				drive.move(0.0, 0.0);
				state = 1;
			}
			// Otherwise...
			else {
				// ...go to that waypoint.
				double[] adjustments = moveToWaypoint(desiredX, desiredY, speed);
				drive.move(speed + adjustments[0], 
						speed + adjustments[1]);
			}
			break;
		case 1:
			// If the difference in the actual and desired headings is greater than pi radians...
			if (headingDifference > Math.PI) {
				// ...go the other way around the circle.
				headingDifference = Math.PI - headingDifference;
			}

			// If the robot is not within pi/48 radians of its target heading...
			if (Math.abs(headingDifference) > (Math.PI / 48)) {
				// ...go to that heading.
				double[] rotationSpeed = moveToOrientation(desiredHeading, speed);
				drive.move(rotationSpeed[0], rotationSpeed[1]);
			}
			// Otherwise...
			else {
				// ...stop moving and go to the next step.
				drive.move(0.0, 0.0);
				state = 2;
			}
			break;
		case 2:
			// Set the arrived boolean to true and reset the state to zero
			arrived = true;
			state = 0;
		default:;
		}

		//SmartDashboard.putNumber("waypoint heading", Math.toDegrees(desiredHeading));
		//SmartDashboard.putBoolean("arrived", arrived);

		// Return whether or not the robot has arrived
		return arrived;
	}

	/**
	 * @param desiredX The X position of the waypoint
	 * @param desiredY
	 * @return The left and right adjustments to add to get the robot to the waypoint
	 */
	public double[] moveToWaypoint(double desiredX, double desiredY, double speed) {
		String direction = location.getDirection();
		double heading = location.getHeading();
		double rightSideAdjustment = 0;
		double leftSideAdjustment = 0;
		double[] targetData = calculateTurn(desiredX, desiredY, speed);

		// If the robot is going forward...
		if (direction.equals("forward")) {

			//If the heading is to the right of the robot's current heading...
			if (DeadReckon.modAngle(heading - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - heading)) {

				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((speed + targetData[1]) <= 1) {
					leftSideAdjustment = targetData[1];
					rightSideAdjustment = 0.0;

				}
				else {
					leftSideAdjustment = 1 - speed;
					rightSideAdjustment = -(targetData[1] - (1 - speed));
				}	
			}
			//If the heading is to the left of the robot's current heading...
			else if (DeadReckon.modAngle(heading - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - heading)) {

				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((speed + targetData[1]) <= 1) {			
					rightSideAdjustment = targetData[1];
					leftSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = 1 - speed;
					leftSideAdjustment = -(targetData[1] - (1 - speed));
				}
			}
		}

		// If the robot is going backward...
		else if(direction.equals("backward")) {
			double course = heading + Math.PI;

			//If the heading is to the right of the robot's current heading...
			if (DeadReckon.modAngle(course - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - course)) {

				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((speed - targetData[1]) >= -1) {
					leftSideAdjustment = -targetData[1];
					rightSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -1 - speed;
					rightSideAdjustment = (targetData[1] + (-1 - speed));
				}
			}

			//If the heading is to the right of the robot's current heading...
			else if (DeadReckon.modAngle(course - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - course)) {

				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((speed - targetData[1]) >= -1) {
					rightSideAdjustment = -targetData[1];
					leftSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = -1 - speed;
					leftSideAdjustment = (targetData[1] + (-1 - speed));
				}
			}
		}

		//Otherwise make no adjustments
		else {
			leftSideAdjustment = 0.0;
			rightSideAdjustment = 0.0;
		}

		//Creates an array with the adjustments
		double[] adjustments = {
				leftSideAdjustment,
				rightSideAdjustment
		};

		//SmartDashboard.putNumber("left adjustment", leftSideAdjustment);
		//SmartDashboard.putNumber("right adjustment", rightSideAdjustment);

		return adjustments;		

	}

	/**
	 * @param desiredHeading The heading of the waypoint
	 * @return The speeds to turn the robot in place to get it to the correct heading
	 */
	public double[] moveToOrientation(double desiredHeading, double speed) {
		// Gets the current heading of the robot
		double heading = location.getHeading();
		// Initializes the turn speeds to zero
		double leftTurnSpeed = 0;
		double rightTurnSpeed = 0;

		// If the desired heading is to the right of the robot...
		if (DeadReckon.modAngle(heading - desiredHeading) >=
				DeadReckon.modAngle(desiredHeading - heading)) {
			// ...turn clockwise.
			leftTurnSpeed = Math.abs(speed) * 1.25;
			rightTurnSpeed = -Math.abs(speed) * 1.25;

		}

		// If the desired heading is to the left of the robot...
		else {
			// ...turn counterclockwise.
			leftTurnSpeed = -Math.abs(speed) * 1.25;
			rightTurnSpeed = Math.abs(speed) * 1.25;		
		}

		return new double[] {
				leftTurnSpeed,
				rightTurnSpeed
		};

		//SmartDashboard.putNumber("left adjustment", leftTurnSpeed);
		//SmartDashboard.putNumber("right adjustment", rightTurnSpeed);
	}

	/**
	 * Finds the heading and adjustments to add for waypoint movement
	 * @param desiredX The X position of the waypoint
	 * @param desiredY The Y position of the waypoint
	 * @return An array with the heading the robot should travel and the adjustment to add to the motor output
	 */
	public double[] calculateTurn(double desiredX, double desiredY, double speed) {
		double heading = location.getHeading();

		//Calculates the direction the robot should travel in to get to the waypoint
		double[] pos = location.getPos();

		double desiredHeading = DeadReckon.modAngle(Math.atan2(desiredX - pos[0],
				desiredY - pos[1]));

		double headingDifference = DeadReckon.modAngle(desiredHeading - heading);
		if (headingDifference > Math.PI) {
			headingDifference -= (Math.PI * 2);
		}

		//Calculates the adjustment based on how much the robot needs to turn
		double driveAdjustment = Math.max(0, Math.min(1, (Math.abs(headingDifference) / Math.PI)));

		//SmartDashboard.putNumber("desired x", desiredX);
		//SmartDashboard.putNumber("desired y", desiredY);
		SmartDashboard.putNumber("desired heading", Math.toDegrees(desiredHeading));
		SmartDashboard.putNumber("ajustment proportion", driveAdjustment);
		SmartDashboard.putNumber("headingDifference", Math.toDegrees(headingDifference));

		return new double[] {
				desiredHeading,
				driveAdjustment
		};
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}