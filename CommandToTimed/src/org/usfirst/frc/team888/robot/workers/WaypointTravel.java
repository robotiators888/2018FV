package org.usfirst.frc.team888.robot.workers;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WaypointTravel {

	private static WaypointTravel guidence;
	
	protected DriveTrain drive;
	protected DeadReckon location;

	protected int state = 0;

	private WaypointTravel () {
		drive = DriveTrain.getInstance();
		location = DeadReckon.getInstance();

		state = 0;
	}
	
	/**
	 * Accessor method for the WaypointTravel Singleton.
	 * @return The object of WaypointTravel
	 */
	public static WaypointTravel getInstance() {
		if (guidence != null) {
			synchronized(WaypointTravel.class) {
				if (guidence != null) {
					guidence = new WaypointTravel();
				}
			}
		}
		
		return guidence;
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
				//TODO Make this not break when x=0
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
		double[] adjustments = null;
		double[] targetData = calculateTurn(desiredX, desiredY, speed);

		//If the robot is going forward...
		if (direction.equals("forward")) {

			//If the heading is to the right of the robot's current heading...
			if (DeadReckon.modAngle(heading - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - heading)) {

				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				adjustments = ((speed + targetData[1]) <= 1)
						? new double[] {targetData[1], 0.0}
						: new double[] {1 - speed, -(targetData[1] - (1 - speed))};

			}

			//If the heading is to the left of the robot's current heading...
			else if (DeadReckon.modAngle(heading - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - heading)) {

				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				adjustments = (speed + targetData[1]) <= 1 
						? new double[] {0.0, targetData[1]} 
						: new double[] {-(targetData[1] - (1 - speed)), 1 - speed};
			}
		}

		//If the robot is going backward...
		else if(direction.equals("backward")) {
			double course = DeadReckon.modAngle(heading + Math.PI);

			//If the heading is to the right of the robot's current heading...
			if (DeadReckon.modAngle(course - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - course)) {

				/*
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				adjustments = ((speed - targetData[1]) >= -1)
						? new double[] {-targetData[1], 0.0} 
						: new double[] {-1 - speed, targetData[1] + (-1 - speed)};
			}

			//If the heading is to the right of the robot's current heading...
			else if (DeadReckon.modAngle(course - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - course)) {

				/*
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				adjustments = ((speed - targetData[1]) >= -1) 
						? new double[] {0.0, -targetData[1]}
						: new double[] {targetData[1] + (-1 - speed), -1 - speed};
			}
		}

		//Otherwise make no adjustments
		else {
			adjustments = new double[] {0.0, 0.0};
		}

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
		double[] turn = null;

		// If the desired heading is to the right of the robot...
		turn = (DeadReckon.modAngle(heading - desiredHeading) >= DeadReckon.modAngle(desiredHeading - heading))
				? new double[] {Math.abs(speed) * 1.25, -Math.abs(speed) * 1.25}
				: new double[] {-Math.abs(speed) * 1.25, Math.abs(speed) * 1.25};

				return turn;
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
		double driveAdjustment = Math.max(0, Math.min(0.5, (Math.abs(headingDifference))));

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
}