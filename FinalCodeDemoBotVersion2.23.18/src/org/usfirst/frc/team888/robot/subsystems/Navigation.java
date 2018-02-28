package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;
	protected OI oi;

	protected double maxOutput = 1.0;
	protected double leftSideAdjustment;
	protected double rightSideAdjustment;
	protected double desiredHeading;
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected double[] desiredLocation;

	protected boolean manualControl = true;

	protected int schedulerOffset = 0;

	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;
	protected boolean init = true;

	protected boolean previousCameraButtonState = false;
	protected byte[] ip = {10, 88, 88, 14};
	protected InetAddress cameraAddress;

	protected DatagramSocket sock;
	protected DatagramPacket message;

	protected String cameraMessage = "frontCamera";
	protected byte[] byteCameraMessage = cameraMessage.getBytes();

	/*
 	protected boolean previousCameraButtonState = false;
 	protected byte[] ip = {10, 8, 88, 12};
 	protected InetAddress cameraAddress;

 	protected DatagramSocket sock;
 	protected DatagramPacket message;
	 */

	public Navigation(DriveTrain p_drive, DeadReckon p_location, OI p_oi) {
		drive = p_drive;
		location = p_location;
		oi = p_oi;

		try {
			cameraAddress = InetAddress.getByAddress(ip);
			sock = new DatagramSocket(7777);
			message = new DatagramPacket(byteCameraMessage, byteCameraMessage.length, cameraAddress, 8888);

		} catch (Exception e) {

		} 
	}

	public void navigationInit() {
		if (init) {
			schedulerOffset = 0;
			location.reset();
			init = false;
		}

		//send first message to pi to start camera feed
		/* try {
		 			sock.send(message);
		 		} catch (IOException e) {
		 			e.printStackTrace();
		 		} */
	}
	//send first message to pi to start camera feed
	public void navigationExecute() throws IOException {
		updateCamera();

		location.updateTracker();
		updateGuidenceControl();
		updateMotion();
		location.updateDashborad();


		if(oi.getRightStickButton(5) && !previousCameraButtonState) {
			if (schedulerOffset == 0) {
				updateCamera();
			}
			previousCameraButtonState = true;
		} else if (!oi.getRightStickButton(5)) {
			previousCameraButtonState = false;
		}

		location.updateDashborad();
		updateDashboard();

		schedulerOffset = (schedulerOffset + 1) % 50;
	}

	public void updateGuidenceControl() {
		desiredLocation = RobotMap.DESIRED_LOCATION;
	}



	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */

	public void updateMotion() {
		if (!manualControl) {
			double[] pos = location.getPos();
			if (pos[1] < 120) {
				double[] adjustments = getAdjustments();
				drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
			} else {
				drive.move(0.0, 0.0);
			}
		} else {
			if(oi.getTriggers()) {
				leftBaseDriveOutput = oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			} else {
				leftBaseDriveOutput = 0.7 * oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = 0.7 * oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}

			if(Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.3 &&
					Math.abs(oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.3){
				leftBaseDriveOutput = 0.0;
				rightBaseDriveOutput = 0.0;
			}

			if (input == true && lastInput == false) {
				press = true;
			} else {
				press = false;
			}

			if (press) {
				output = !output;
			}

			lastInput = input;
			input = oi.getLeftStickButton(2) || oi.getRightStickButton(2);

			if(output) {
				leftDriveOutput = leftBaseDriveOutput;
				rightDriveOutput = rightBaseDriveOutput;
			} else {
				leftDriveOutput = -rightBaseDriveOutput;
				rightDriveOutput = -leftBaseDriveOutput;
			}

			SmartDashboard.putNumber("leftOutput", leftDriveOutput);
			SmartDashboard.putNumber("rightOutput", rightDriveOutput); 

			drive.move(leftDriveOutput, rightDriveOutput);
		}
	}

	public double[] getAdjustments() {	
		double[] navData = location.getNavLocationData();
		desiredHeading = 0; //calculateDesiredHeading();

		/**
		 * If the robot is moving in a positive direction...
		 */

		if ((navData[0] > 0) && (navData[1] > 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(navData[2] - desiredHeading) <
					DeadReckon.modAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT)
						<= maxOutput) {			
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(navData[2] - desiredHeading) >
			DeadReckon.modAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((RobotMap.RIGHT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= maxOutput) {			
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				}

				/**
				 * If the robot is already moving straight add no adjustments
				 */

			} else {
				leftSideAdjustment = 0.0;
				rightSideAdjustment = 0.0;
			}	

			/**
			 * If the robot is moving in a negative direction...
			 */

		} else if((navData[0] < 0) && (navData[1] < 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(navData[2] - desiredHeading) >
			DeadReckon.modAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((RobotMap.LEFT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
 			/* If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(navData[2] - desiredHeading) <
					DeadReckon.modAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((RobotMap.RIGHT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
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

		} else {
			leftSideAdjustment = 0.0;
			rightSideAdjustment = 0.0;
		}

		double[] adjustments = {
				leftSideAdjustment,
				rightSideAdjustment
		};

		return adjustments;		
	}

	public void updateDashboard() {
		SmartDashboard.putNumber("Left Adjustments", leftSideAdjustment);
		SmartDashboard.putNumber("Right Adjustments", rightSideAdjustment);
	}

	public double calculateDesiredHeading() {
		double[] pos = location.getPos();
		double[] posToDesired = {0,0};

		for (int i = 0; i < pos.length; i++) {
			posToDesired[i] = pos[i] - desiredLocation[i];
		}

		desiredHeading = DeadReckon.modAngle(Math.atan2(posToDesired[0], posToDesired[1]));
		return desiredHeading;
	}

	/**
	 * @return The array with zeros for both adjustments
	 */

	public double[] reset() {
		double[] j = {0,0};
		return j;
	}



	public void updateCamera() {
		SmartDashboard.putBoolean("button at beginning", previousCameraButtonState);

		if(cameraMessage.equals("frontCamera")) {
			cameraMessage = "backCamera";
			byteCameraMessage = cameraMessage.getBytes();
			SmartDashboard.putString("changed message", "back");
		} else {
			cameraMessage = "frontCamera";
			byteCameraMessage = cameraMessage.getBytes();
			SmartDashboard.putString("changed message", "front");
		}

		SmartDashboard.putString("camera message after button press", cameraMessage);

		try {
			message.setData(byteCameraMessage);
			sock.send(message);
			SmartDashboard.putString("sent", cameraMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SmartDashboard.putBoolean("button after pressed", previousCameraButtonState);


	}




	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}