package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;
	protected Pincer pincer;
	protected OI oi;

	public SendableChooser<String> startPosition;
	
	protected Timer FMSTime;

	protected String gameData;

	protected double time;
	protected double maxOutput = 1.0;
	protected double leftSideAdjustment;
	protected double rightSideAdjustment;
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected double[] desiredLocation = RobotMap.DESIRED_LOCATION;

	protected boolean manualControl = false;

	protected int schedulerOffset = 0;

	protected int state = 0;

	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;
	protected boolean init = true;

	protected boolean previousCameraButtonState = false;
	protected byte[] ip = {10, 8, 88, 12};
	protected InetAddress cameraAddress;

	protected DatagramSocket sock;
	protected DatagramPacket message;

	protected String cameraMessage = "frontCamera";
	protected byte[] byteCameraMessage = cameraMessage.getBytes();

	public Navigation(DriveTrain p_drive, DeadReckon p_location, Pincer p_pince, OI p_oi) {
		drive = p_drive;
		location = p_location;
		oi = p_oi;
		pincer = p_pince;

		startPosition = new SendableChooser<String>();
		startPosition.addDefault("You Need to Choose One", "Middle");
		startPosition.addObject("Left Start Position", "Left");
		startPosition.addObject("Right Start Position", "Right");
		
		try {
			cameraAddress = InetAddress.getByAddress(ip);
			sock = new DatagramSocket(RobotMap.RIO_UDP_PORT);
			message = new DatagramPacket(byteCameraMessage, 
					byteCameraMessage.length, cameraAddress, RobotMap.PI_UDP_PORT);

		} catch (Exception e) {

		} 

		FMSTime = new Timer();
	}

	public void navigationInit() {
		if (init) {
			schedulerOffset = 0;
			location.reset();
			init = false;
		}
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}


	public void navigationExecute() throws IOException {
		location.updateTracker();
		updateGuidenceControl();
		updateMotion();


		if(oi.getRightStickButton(5) && !previousCameraButtonState) {
			switchCamera();
			previousCameraButtonState = true;
		} else if (!oi.getRightStickButton(5)) {
			previousCameraButtonState = false;
		}

		if (schedulerOffset == 0) {
			updateDashboard();
		}

		if (schedulerOffset == 25) {
			location.updateDashboard();
		}


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
		manualControl = !DriverStation.getInstance().isAutonomous();
		if (!manualControl) {
			autoRun();


		//TELEOP CODE	
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
				rightDriveOutput = leftBaseDriveOutput;
				leftDriveOutput = rightBaseDriveOutput;
			} else {
				rightDriveOutput = -rightBaseDriveOutput;
				leftDriveOutput = -leftBaseDriveOutput;
			} 

			drive.move(leftDriveOutput, rightDriveOutput);
		}
	}

	public void autoRun() {
		double[] pos = location.getPos();
		pincer.setPincerPosition(1700, true, 0.0);
		if (time > 200) {
			drive.move(0, 0);
			if (gameData.charAt(0) == startPosition.getSelected().charAt(0)) {
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
			}
		} else if ((Math.abs(RobotMap.DESIRED_LOCATION[0] - pos[0]) > 15) && 
				(Math.abs(RobotMap.DESIRED_LOCATION[1] - pos[1]) > 15)) {
			//double[] adjustments = getAdjustments();
			drive.move(RobotMap.LEFT_AUTO_SPEED, RobotMap.RIGHT_AUTO_SPEED);
			//drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
					//RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
		} else {
			drive.move(0.0, 0.0);
		}
		time++;
		/*
			double turnTo = (Math.PI / 2);
			if (location.getHeading() < turnTo)
				drive.move(0.0, 0.3);
			else{
				SmartDashboard.putString("where", "170");
			}
			drive.move(0,0);
		 */
	}

	public double[] getAdjustments() {	
		double[] locationData = location.getNavLocationData();		
		double[] headingData = calculateTurn();

		/**
		 * If the robot is moving in a positive direction...
		 */

		if ((locationData[0] > 0) && (locationData[1] > 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(locationData[2] - headingData[0]) <
					DeadReckon.modAngle(headingData[0] - locationData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + headingData[1])
						<= maxOutput) {
					leftSideAdjustment = headingData[1];
					rightSideAdjustment = 0.0;

				} else {
					rightSideAdjustment = -headingData[1];
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(locationData[2] - headingData[0]) >
			DeadReckon.modAngle(headingData[0] - locationData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((RobotMap.RIGHT_AUTO_SPEED + headingData[1]) <= maxOutput) {			
					rightSideAdjustment = headingData[1];
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -headingData[1];
					rightSideAdjustment = 0.0;
				}
			}

			/**
			 * If the robot is moving in a negative direction...
			 */

		} else if((locationData[0] < 0) && (locationData[1] < 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.modAngle(locationData[2] - headingData[0]) >
			DeadReckon.modAngle(headingData[0] - locationData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((RobotMap.LEFT_AUTO_SPEED - headingData[1]) >= -maxOutput) {
					leftSideAdjustment = -headingData[1];
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = headingData[1];
					leftSideAdjustment = 0.0;
				}

				/**
 			/* If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.modAngle(locationData[2] - headingData[0]) <
					DeadReckon.modAngle(headingData[0] - locationData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((RobotMap.RIGHT_AUTO_SPEED - headingData[1]) >= -maxOutput) {
					rightSideAdjustment = -headingData[1];
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = headingData[1];
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
		SmartDashboard.putString("Game Pattern", gameData);
		SmartDashboard.putNumber("leftOutput", leftDriveOutput);
		SmartDashboard.putNumber("rightOutput", rightDriveOutput);
	}

	public double[] calculateTurn() {
		double[] pos = location.getPos();
		double[] posToDesired = {0,0};

		for (int i = 0; i < pos.length; i++) {
			posToDesired[i] = pos[i] - desiredLocation[i];
		}

		double desiredHeading = 0; //DeadReckon.modAngle(Math.atan2(posToDesired[0], posToDesired[1]));
		double driveAdjustment = 0.15; //(Math.abs(location.getHeading() - desiredHeading) / Math.PI) * 0.3; 

		double[] i = {
				desiredHeading,
				driveAdjustment
		};

		return i;
	}

	/**
	 * @return The array with zeros for both adjustments
	 */

	public double[] reset() {
		double[] j = {0,0};
		return j;
	}


	public void switchCamera() {
		if(cameraMessage.equals("frontCamera")) {
			cameraMessage = "backCamera";
			byteCameraMessage = cameraMessage.getBytes();
		} else {
			cameraMessage = "frontCamera";
			byteCameraMessage = cameraMessage.getBytes();
		}

		try {
			message.setData(byteCameraMessage);
			sock.send(message);
			SmartDashboard.putString("sent camera message", cameraMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}