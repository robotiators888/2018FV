package algs;
import disc.data.Waypoint;

public class WaypointMovement {

    Waypoint pos;
    
    /**
     * Constructor for an object that uses a robot's position to get adjustments
     * so it can travel from it's current position to a waypoint
     * 
     * @param p_pos The position of the robot
     */
    public WaypointMovement(Waypoint p_pos) {
        pos = p_pos;
    }
    
    /**
     * Calculates the adjustments needed for a robot to travel from one point to
     * another
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @param speed
     *            The speed the robot should travel to get to the waypoint as a
     *            percentage of the robot's max speed (from -1 to 1)
     * @param direction
     *            The direction the should travel in to get to the waypoint
     *            ("forward" or "backward")
     * @return An array containing adjustments for the left and and right sides
     *         of the robot as a percentage of the robot's max speed (from -1 to
     *         1)
     */
    public double[] getTravelAdjustments(Waypoint desired,
            double speed, String direction) {
        double heading = pos.getHeading();
        double[] adjustments = null;
        double desiredAngle = angleToWaypoint(pos, desired);
        double speedAdjustment = speedAdjustments(pos, desired);

        // If the robot is going forward...
        if (direction.equals("forward")) {

            // If the heading is to the right of the robot's current heading...
            if (RobotMath.modAngle(heading - desiredAngle) >= RobotMath
                    .modAngle(desiredAngle - heading)) {

                /*
                 * If the speed plus the adjustment for the left side would be
                 * slower than the max speed add the adjustments to the left
                 * side. Otherwise, subtract the adjustments from the right
                 * side.
                 */

                adjustments = ((speed + speedAdjustment) <= 1)
                        ? new double[] { speedAdjustment, 0.0 }
                        : new double[] { 1 - speed,
                                -(speedAdjustment - (1 - speed)) };

            }

            // If the heading is to the left of the robot's current heading...
            else if (RobotMath.modAngle(heading - desiredAngle) < RobotMath
                    .modAngle(desiredAngle - heading)) {

                /*
                 * If the speed plus the adjustment for the right side would be
                 * slower than the max speed add the adjustments to the right
                 * side. Otherwise, subtract the adjustments from the left side.
                 */

                adjustments = (speed + speedAdjustment) <= 1
                        ? new double[] { 0.0, speedAdjustment }
                        : new double[] { -(speedAdjustment - (1 - speed)),
                                1 - speed };
            }
        }

        // If the robot is going backward...
        else if (direction.equals("backward")) {
            double course = RobotMath.modAngle(heading + Math.PI);

            // If the heading is to the right of the robot's current heading...
            if (RobotMath.modAngle(course - desiredAngle) <= RobotMath
                    .modAngle(desiredAngle - course)) {

                /*
                 * If the speed plus the adjustment for the left side would be
                 * slower than the max speed add the adjustments to the left
                 * side. Otherwise, subtract the adjustments from the right
                 * side.
                 */

                adjustments = ((speed - speedAdjustment) >= -1)
                        ? new double[] { -speedAdjustment, 0.0 }
                        : new double[] { -1 - speed,
                                speedAdjustment + (-1 - speed) };
            }

            // If the heading is to the right of the robot's current heading...
            else if (RobotMath.modAngle(course - desiredAngle) > RobotMath
                    .modAngle(desiredAngle - course)) {

                /*
                 * If the speed plus the adjustment for the right side would be
                 * slower than the max speed add the adjustments to the right
                 * side. Otherwise, subtract the adjustments from the left side.
                 */

                adjustments = ((speed - speedAdjustment) >= -1)
                        ? new double[] { 0.0, -speedAdjustment }
                        : new double[] { speedAdjustment + (-1 - speed),
                                -1 - speed };
            }
        }

        // Otherwise make no adjustments
        else {
            adjustments = new double[] { 0.0, 0.0 };
        }

        return adjustments;
    }

    /**
     * Calculates the speed needed for each wheel so a robot can orient itself
     * about a point
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @param speed
     *            The speed the robot should travel to get to the waypoint as a
     *            percentage of the robot's max speed (from -1 to 1)
     * @return An array containing the speed for the left and and right sides of
     *         the robot as a percentage of the robot's max speed (from -1 to 1)
     */
    public double[] getOrientationSpeeds(Waypoint desired, double speed) {
        // Gets the current heading of the robot
        double heading = pos.getHeading();
        double desiredHeading = desired.getHeading();
        // Initializes the turn speeds to zero
        double[] turn = null;

        // If the desired heading is to the right of the robot...
        turn = (RobotMath.modAngle(heading - desiredHeading) >= RobotMath
                .modAngle(desiredHeading - heading))
                        ? new double[] { Math.abs(speed) * 1.25,
                                -Math.abs(speed) * 1.25 }
                        : new double[] { -Math.abs(speed) * 1.25,
                                Math.abs(speed) * 1.25 };

        return turn;
    }
    
    /**
     * Calculates the adjustments needed for a robot to travel from one point to
     * another
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @param speed
     *            The speed the robot should travel to get to the waypoint as a
     *            percentage of the robot's max speed (from -1 to 1)
     * @param direction
     *            The direction the should travel in to get to the waypoint
     *            ("forward" or "backward")
     * @return An array containing adjustments for the left and and right sides
     *         of the robot as a percentage of the robot's max speed (from -1 to
     *         1)
     */
    public static double[] getTravelAdjustments(Waypoint pos, Waypoint desired,
            double speed, String direction) {
        double heading = pos.getHeading();
        double[] adjustments = null;
        double desiredAngle = angleToWaypoint(pos, desired);
        double speedAdjustment = speedAdjustments(pos, desired);

        // If the robot is going forward...
        if (direction.equals("forward")) {

            // If the heading is to the right of the robot's current heading...
            if (RobotMath.modAngle(heading - desiredAngle) >= RobotMath
                    .modAngle(desiredAngle - heading)) {

                /*
                 * If the speed plus the adjustment for the left side would be
                 * slower than the max speed add the adjustments to the left
                 * side. Otherwise, subtract the adjustments from the right
                 * side.
                 */

                adjustments = ((speed + speedAdjustment) <= 1)
                        ? new double[] { speedAdjustment, 0.0 }
                        : new double[] { 1 - speed,
                                -(speedAdjustment - (1 - speed)) };

            }

            // If the heading is to the left of the robot's current heading...
            else if (RobotMath.modAngle(heading - desiredAngle) < RobotMath
                    .modAngle(desiredAngle - heading)) {

                /*
                 * If the speed plus the adjustment for the right side would be
                 * slower than the max speed add the adjustments to the right
                 * side. Otherwise, subtract the adjustments from the left side.
                 */

                adjustments = (speed + speedAdjustment) <= 1
                        ? new double[] { 0.0, speedAdjustment }
                        : new double[] { -(speedAdjustment - (1 - speed)),
                                1 - speed };
            }
        }

        // If the robot is going backward...
        else if (direction.equals("backward")) {
            double course = RobotMath.modAngle(heading + Math.PI);

            // If the heading is to the right of the robot's current heading...
            if (RobotMath.modAngle(course - desiredAngle) <= RobotMath
                    .modAngle(desiredAngle - course)) {

                /*
                 * If the speed plus the adjustment for the left side would be
                 * slower than the max speed add the adjustments to the left
                 * side. Otherwise, subtract the adjustments from the right
                 * side.
                 */

                adjustments = ((speed - speedAdjustment) >= -1)
                        ? new double[] { -speedAdjustment, 0.0 }
                        : new double[] { -1 - speed,
                                speedAdjustment + (-1 - speed) };
            }

            // If the heading is to the right of the robot's current heading...
            else if (RobotMath.modAngle(course - desiredAngle) > RobotMath
                    .modAngle(desiredAngle - course)) {

                /*
                 * If the speed plus the adjustment for the right side would be
                 * slower than the max speed add the adjustments to the right
                 * side. Otherwise, subtract the adjustments from the left side.
                 */

                adjustments = ((speed - speedAdjustment) >= -1)
                        ? new double[] { 0.0, -speedAdjustment }
                        : new double[] { speedAdjustment + (-1 - speed),
                                -1 - speed };
            }
        }

        // Otherwise make no adjustments
        else {
            adjustments = new double[] { 0.0, 0.0 };
        }

        return adjustments;
    }

    /**
     * Calculates the speed needed for each wheel so a robot can orient itself
     * about a point
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @param speed
     *            The speed the robot should travel to get to the waypoint as a
     *            percentage of the robot's max speed (from -1 to 1)
     * @return An array containing the speed for the left and and right sides of
     *         the robot as a percentage of the robot's max speed (from -1 to 1)
     */
    public static double[] getOrientationSpeeds(Waypoint pos, Waypoint desired,
            double speed) {
        // Gets the current heading of the robot
        double heading = pos.getHeading();
        double desiredHeading = desired.getHeading();
        // Initializes the turn speeds to zero
        double[] turn = null;

        // If the desired heading is to the right of the robot...
        turn = (RobotMath.modAngle(heading - desiredHeading) >= RobotMath
                .modAngle(desiredHeading - heading))
                        ? new double[] { Math.abs(speed) * 1.25,
                                -Math.abs(speed) * 1.25 }
                        : new double[] { -Math.abs(speed) * 1.25,
                                Math.abs(speed) * 1.25 };

        return turn;
    }

    /**
     * Calculates the angle between a robot's heading and a desired waypoint
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @return The angle (in radians) between a robot's heading and the desired
     *         waypoint
     */
    public static double angleToWaypoint(Waypoint pos, Waypoint desired) {
        return RobotMath.modAngle(Math.atan2(desired.getX() - pos.getX(),
                desired.getY() - pos.getY()));
    }

    /**
     * Calculates the proportional speed adjustment for a turn based on the
     * difference between a robot's heading and the desired heading
     * 
     * @param pos
     *            The position of the robot
     * @param desired
     *            The desired position of the robot
     * @return A speed adjustment as a percentage of the robot's max speed (from
     *         -1 to 1)
     */
    public static double speedAdjustments(Waypoint pos, Waypoint desired) {

        double headingDifference = RobotMath
                .modAngle(angleToWaypoint(pos, desired) - pos.getHeading());
        if (headingDifference > Math.PI) {
            headingDifference -= (Math.PI * 2);
        }

        // Calculates the adjustment based on how much the robot needs to turn
        return Math.max(0, Math.min(0.5, (Math.abs(headingDifference))));

    }
}
