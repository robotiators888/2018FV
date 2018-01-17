package org.usfirst.frc.team888.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {

	public static int rearLeftSRX = 1;
	public static int frontLeftSRX = 2;
	public static int rearRightSRX = 3;
	public static int frontRightSRX = 4;
	
	public static double lastEncoderLeft = 0;
	public static double lastEncoderRight = 0;
	public static double encoderLeftValue = 0;
	public static double encoderRightValue = 0;
	public static double robotHeading = 0;
	public static double lastXPosition = 0;
	public static double lastYPosition = 0;
	public static double positionX = 3;
	public static double positionY = 4;
	public static double heading360 = 0;
	public static double botWidth = 33;
	
	public static double encoderLeftDifference = 0;
	public static double encoderRightDifference = 0;
	public static double positionChange = 0;
	public static double changeInX = 0;
	public static double changeInY = 0;
	public static double changeInAngle = 0;
	public static double distanceTraveled = 0;
}
