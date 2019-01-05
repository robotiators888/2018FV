package algs;
public class RobotMath {

    /**
     * Converts the angle measurement to a range of 0 - 2Pi.
     * 
     * @param angle
     *            Angle measurement in radians.
     * @return Angle measurement in radians from 0 - 2Pi.
     */
    public static double modAngle(double angle) {
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        angle %= 2 * Math.PI;
        return angle;
    }

}
