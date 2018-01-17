package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Navigation extends Subsystem {

	
	public static double angleConversion(double heading360) {
		heading360 = RobotMap.robotHeading % 360;
		if (heading360 < 0) {
			heading360 += 360;
		}
		
		return heading360;
		
	}
	
	public static void navigation() {
		
		RobotMap.encoderLeftDifference = RobotMap.encoderLeftValue - RobotMap.lastEncoderLeft;
		RobotMap.encoderRightDifference = RobotMap.encoderRightValue - RobotMap.lastEncoderRight;
		RobotMap.positionChange = 
				(RobotMap.encoderLeftDifference + RobotMap.encoderRightDifference)/2;
		RobotMap.changeInX = RobotMap.positionChange *Math.cos(RobotMap.heading360 
				+ (RobotMap.encoderLeftDifference/(2*RobotMap.botWidth)));
		RobotMap.changeInY = RobotMap.positionChange * Math.sin(RobotMap.heading360 
				+ (RobotMap.encoderRightDifference/(2*RobotMap.botWidth)));
		RobotMap.changeInAngle = (RobotMap.encoderLeftDifference 
				- RobotMap.encoderRightDifference)/RobotMap.botWidth;
		RobotMap.distanceTraveled = 
				Math.sqrt(Math.pow(RobotMap.changeInX, 2) + Math.pow(RobotMap.changeInY, 2));
		RobotMap.robotHeading = angleConversion(RobotMap.changeInAngle);
		RobotMap.positionX += RobotMap.changeInX;
		RobotMap.positionY += RobotMap.changeInY;
		
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}
