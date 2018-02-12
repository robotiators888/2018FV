package org.usfirst.frc.team888.robot.subsystems.nav;

public class AngleMath {

	/**
     * Converts the angle measurement to a range of 0 - 2Pi.
     * @param angle Angle measurement in radians.
     * @return Angle measurement in radians from 0 - 2Pi.
     */
    public static double absAngle(double angle) {
    	angle %= 2 * Math.PI;
    	if (angle < 0){
    		angle += 2 * Math.PI;
    		}
    	return angle;
    }
	
}
