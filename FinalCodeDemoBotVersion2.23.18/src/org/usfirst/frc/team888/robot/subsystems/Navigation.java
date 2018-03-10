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

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	protected String gameData;

	protected DriveTrain drive;
	protected DeadReckon location;
	protected OI oi;

	protected double maxOutput = 1.0;
	protected double desiredHeading;
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected int state;

	protected double[] desiredLocation = new double[2];

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

		state = 0;
	}

	public void navigationInit() {
		if (init) {
			schedulerOffset = 0;
			location.reset();
			init = false;
		}

		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	//send first message to pi to start camera feed
	public void navigationExecute() {
		updateCamera();

		location.updateTracker();
		updateGuidenceControl();
		updateMotion();
		location.updateDashboard();



		if(oi.getRightStickButton(5) && !previousCameraButtonState) {
			if (schedulerOffset == 0) {
				updateCamera();
			}
			previousCameraButtonState = true;
		} else if (!oi.getRightStickButton(5)) {
			previousCameraButtonState = false;
		}

		updateDashboard();

		schedulerOffset = (schedulerOffset + 1) % 50;
	}

	public void updateGuidenceControl() {
	}



	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */

	public void updateMotion() {
		manualControl = !DriverStation.getInstance().isAutonomous();

		if (!manualControl) {
			runAuto();

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

			drive.move(-leftDriveOutput, -rightDriveOutput);
		}
	}


	public void runAuto() {
		double[] pos = location.getPos();
		switch (state) {
		case 0:
			desiredLocation[0] = 0;
			desiredLocation[1] = 72;
			if ((Math.abs(desiredLocation[0] - pos[0]) < 3) && 
					(Math.abs(desiredLocation[1] - pos[1]) < 3)) {
				drive.move(0.0, 0.0);
				state = 1;
			} else {
				double[] adjustments = getToWaypoint();
				drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
						RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
			}
			break;
		case 1:
			if (gameData.charAt(0) == 'L') {
				if (location.getHeading() > ((Math.PI * 14) / 9)) {
					drive.move(0.2, -0.2);
				}
				else if (location.getHeading() < ((Math.PI * 13) / 9)){
					drive.move(-0.2, 0.2);
				}
				else {
					drive.move(0, 0);
					state = 2;
				}
			}
			else {
				if (location.getHeading() < ((Math.PI * 4) / 9)) {
					drive.move(-0.4, 0.4);
					SmartDashboard.putString("boi", "CW");
				}
				else if (location.getHeading() > ((Math.PI * 5) / 9)){
					drive.move(0.6, -0.6);
					SmartDashboard.putString("boi", "CCW");
				}
				else {
					drive.move(0, 0);
					state = 2;
				}
			}
			break;
		case 2:
			if (gameData.charAt(0) == 'L') {
				desiredLocation[0] = -72;
				desiredLocation[1] = 72;		
			}
			else {
				desiredLocation[0] = 72;
				desiredLocation[1] = 72;
			}
			
			if ((Math.abs(desiredLocation[0] - pos[0]) < 3) && 
					(Math.abs(desiredLocation[1] - pos[1]) < 3)) {
				drive.move(0.0, 0.0);
				state = 3;
			} else {
				double[] adjustments = getToWaypoint();
				drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
						RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
			}
			break;
		case 3:
			if (gameData.charAt(0) == 'L' && (location.getHeading() > (Math.PI / 12))) {
				drive.move(-0.2, 0.2);
			}
			else if (gameData.charAt(0) == 'R' && (location.getHeading() < ((Math.PI * 23)/ 12))) {
				drive.move(-0.6, 0.6);
			}
			else {
				drive.move(0.0, 0.0);
				state = 4;
			}
			break;
		case 4:
			if (gameData.charAt(0) == 'L') {
				desiredLocation = new double[] {72, 140};
			}
			else {
				desiredLocation[0] = 72;
				desiredLocation[1] = 144;
			}
			if ((Math.abs(desiredLocation[0] - pos[0]) < 3) && 
					(Math.abs(desiredLocation[1] - pos[1]) < 3)) {
				drive.move(0.0, 0.0);
				state = 5;
			} else {
				double[] adjustments = getToWaypoint();
				drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
						RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
			}
			break;
		case 5:
			if (location.getHeading() < ((Math.PI * 23)/ 12) && location.getHeading() > (Math.PI / 12)) {
				drive.move(-0.6, 0.6);
			}
			else {
				drive.move(0.0, 0.0);
				state = 6;
			}
			break;
		default:
			drive.move(0.0, 0.0);
		}
		
		SmartDashboard.putNumber("state", state);
	}

	public double[] getToWaypoint() {
		String direction = location.getDirection();
		double heading = location.getHeading();
		double rightSideAdjustment = 0;
		double leftSideAdjustment = 0;
		double[] targetData = calculateTurn();


		/**
		 * If the robot is moving in a positive direction...
		 */

		if (direction.equals("forward")) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(heading - targetData[0]) <
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

				} else {
					rightSideAdjustment = -targetData[1];
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(heading - targetData[0]) >
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

		return adjustments;		

	}
	
	public double[] getOriented(double desiredHeading) {
		double[] targetData = calculateTurn();
		double heading = location.getHeading();
		double leftTurnSpeed = 0;
		double rightTurnSpeed = 0;

			if (DeadReckon.modAngle(heading - targetData[0]) <
					DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + targetData[1])
						<= maxOutput) {
					leftTurnSpeed = targetData[1];
					rightTurnSpeed = 0.0;

				} else {
					rightTurnSpeed = -targetData[1];
					leftTurnSpeed = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(heading - targetData[0]) >
			DeadReckon.modAngle(targetData[0] - heading)) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 *INVERT ONE TURN SPEED/

				if 	((RobotMap.RIGHT_AUTO_SPEED + targetData[1]) <= maxOutput) {			
					rightTurnSpeed = targetData[1];
					leftTurnSpeed = 0.0;
				} else {
					leftTurnSpeed = -targetData[1];
					rightTurnSpeed = 0.0;
				}
			}		
				
		double[] turnSpeeds = {
				leftTurnSpeed,
				rightTurnSpeed
		};

		return turnSpeeds;	
	}

	//Sends navigation data to the dashboard
	public void updateDashboard() {
		//SmartDashboard.putNumber("Left Adjustments", leftSideAdjustment);
		//SmartDashboard.putNumber("Right Adjustments", rightSideAdjustment);
		SmartDashboard.putString("Game Pattern", gameData);
		SmartDashboard.putNumber("leftOutput", leftDriveOutput);
		SmartDashboard.putNumber("rightOutput", rightDriveOutput);
	}

	/**Finds the heading and adjustments to add for waypoint movement
	 * @return An array with the heading the robot should travel and the adjustment to add to the motor output
	 */
	public double[] calculateTurn() {

		//Calculates the direction the robot should travel in to get to the next waypoint
		double[] pos = location.getPos();

		double desiredHeading = DeadReckon.modAngle(Math.atan2(desiredLocation[0] - pos[0],
				desiredLocation[1] - pos[1]));

		SmartDashboard.putNumber("desired x", desiredLocation[0]);
		SmartDashboard.putNumber("desired y", desiredLocation[1]);
		SmartDashboard.putNumber("desired heading", Math.toDegrees(desiredHeading));
		
		//Calculates the adjustment based on how much the robot needs to turn
		double driveAdjustment = 0.15; //(Math.abs(location.getHeading() - desiredHeading) / Math.PI) * 0.3; 

		double[] i = {
				desiredHeading,
				driveAdjustment
		};

		return i;
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