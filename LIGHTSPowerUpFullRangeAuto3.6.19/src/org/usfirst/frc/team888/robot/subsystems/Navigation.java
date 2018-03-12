package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	//Instantiates the objects of the other classes controlled by navigation
	protected DriveTrain drive;
	protected DeadReckon location;
	protected Pincer pincer;
	protected OI oi;

	//Instantiates a chooser for the dashboard to select where the robot is at the start of the match
	public SendableChooser<String> startPosition;

	//Instantiates a string to store the randomizer pattern from the FMS
	protected String gameData;

	//Instantiates time variable
	protected double time;

	//Instantiates adjustments variables  
	protected double maxOutput = 1.0;
	protected double leftSideAdjustment;
	protected double rightSideAdjustment;
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected double[] desiredLocation = {0, 0};

	//Instantiates boolean for if manual controls are enabled. Defaults to disables (auto).
	protected boolean manualControl = false;

	//Instantiates offset for how often some methods are called in navExecute
	protected int schedulerOffset = 0;

	//Instantiates state for auto
	protected int state = 0;

	//Instantiates toggle booleans for drive direction
	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;

	//Instantiates initialized boolean
	protected boolean init = true;

	//Instantiates camera objects
	protected boolean previousCameraButtonState = false;
	protected InetAddress cameraAddress;

	protected DatagramSocket sock;
	protected DatagramPacket message;

	protected String cameraMessage = "frontCamera";
	protected byte[] byteCameraMessage = cameraMessage.getBytes();

	public Navigation(DriveTrain p_drive, DeadReckon p_location, Pincer p_pince, OI p_oi) {
		//Sets objects of drive, location, pincer, and OI to be the objects passed in by Robot
		drive = p_drive;
		location = p_location;
		oi = p_oi;
		pincer = p_pince;

		//Declares the start position and the start location options
		startPosition = new SendableChooser<String>();
		startPosition.addDefault("You Need to Choose One", "Middle");
		startPosition.addObject("Left Start Position", "Left");
		startPosition.addObject("Right Start Position", "Right");

		//Declares objects for RIO-PI communication
		try {
			cameraAddress = InetAddress.getByAddress(RobotMap.IP_ADDRESS);
			sock = new DatagramSocket(RobotMap.RIO_UDP_PORT);
			message = new DatagramPacket(byteCameraMessage, 
					byteCameraMessage.length, cameraAddress, RobotMap.PI_UDP_PORT);
		} catch (Exception e) {}
		
		desiredLocation = new double[] {0,0};
	}

	//Initializes objects in or called by navigation 
	public void navigationInit() {
		//If it is time to initialize...
		if (init) {
			//...then initialize.
			schedulerOffset = 0;
			location.reset();
			init = false;
		}

		//Stores the randomizer data from the FMS as a string
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	//Calls the methods that need to be run periodically
	public void navigationExecute() throws IOException {
		//Methods run at 50Hz
		location.updateTracker();
		updateGuidenceControl();
		updateMotion();
		location.updateDashboard();

		//If the camera switch button was pressed and the the wrong camera is displaying...
		if(oi.getRightStickButton(5) && !previousCameraButtonState) {
			//...switch the cameras
			switchCamera();
			previousCameraButtonState = true;
		}
		//Otherwise the camera state is correct. 
		else if (!oi.getRightStickButton(5)) {
			previousCameraButtonState = false;
		}

		//Send the nav data to the dashboard once per second on the second 
		if (schedulerOffset == 0) {
			updateDashboard();
		}

		//Send the location data to the dashboard once per second on the half second
		if (schedulerOffset == 25) {
		}

		//Increment the offset by one. If it has reached 50 (one second), set it back to zero.
		schedulerOffset = (schedulerOffset + 1) % 50;
	}

	//Gets the desired location
	public void updateGuidenceControl() {
		//desiredLocation = RobotMap.DESIRED_LOCATION;
	}


	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */

	//Moves the drive train
	public void updateMotion() {
		//If the game mode is auto, manual controls are off
		manualControl = !DriverStation.getInstance().isAutonomous();

		//If manual controls are off...
		if (!manualControl) {
			//...run auto.
			autoRun();
		}

		//Otherwise run teleop controls.	
		else {
			//If both triggers are pressed...
			if(oi.getTriggers()) {
				//...the motors go at full speed
				leftBaseDriveOutput = oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}
			//Otherwise go at 70% speed
			else {
				leftBaseDriveOutput = 0.7 * oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = 0.7 * oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}

			//If the joystick is less then 20% in either direction then ignore it
			if(Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.2 &&
					Math.abs(oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.2){
				leftBaseDriveOutput = 0.0;
				rightBaseDriveOutput = 0.0;
			}

			//Toggles what the front of the robot will be to the driver
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

			//Sends the movement command to the drive train to execute
			drive.move(leftDriveOutput, rightDriveOutput);
		}
	}

	//Tells the robot what to go in auto
	public void autoRun() {
		double[] pos = location.getPos();

		if (startPosition.getSelected().equals("Middle")) {
			switch (state) {
			case 0:
				desiredLocation = new double[] {0. -72};
				pincer.setPincerPosition(2115, true, 0.0);
				if ((Math.abs(desiredLocation[0] - pos[0]) < 15) && 
						(Math.abs(desiredLocation[1] - pos[1]) < 15)) {
					drive.move(0.0, 0.0);
					state = 1;
				} else {
					double[] adjustments = getAdjustments();
					drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
							RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
				}
				break;
			case 1:
				pincer.setPincerPosition(2115, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (location.getHeading() > ((Math.PI * 14) / 9)) {
						drive.move(-0.2, 0.2);
					}
					else if (location.getHeading() < ((Math.PI * 13) / 9)){
						drive.move(0.2, -0.2);
					}
					else {
						drive.move(0, 0);
						state = 2;
					}
				}
				else {
					if (location.getHeading() > ((Math.PI * 4) / 9)) {
						drive.move(0.2, -0.2);
					}
					else if (location.getHeading() < ((Math.PI * 5) / 9)){
						drive.move(-0.2, 0.2);
					}
					else {
						drive.move(0, 0);
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(2115, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					desiredLocation = new double[] {-72, -72};
				}
				else {
					desiredLocation = new double[] {72, -72};
				}
				if ((Math.abs(desiredLocation[0] - pos[0]) < 15) && 
						(Math.abs(desiredLocation[1] - pos[1]) < 15)) {
					drive.move(0.0, 0.0);
					state = 3;
				} else {
					double[] adjustments = getAdjustments();
					drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
							RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
				}
				break;
			case 3:
				pincer.setPincerPosition(2115, true, 0.0);
				if (gameData.charAt(0) == 'L' && (location.getHeading() > (Math.PI / 12))) {
					drive.move(-0.2, 0.2);
				}
				else if (gameData.charAt(0) == 'R' && (location.getHeading() < ((Math.PI * 11)/ 12))) {
					drive.move(-0.2, 0.2);
				}
				else {
					drive.move(0.0, 0.0);
					state = 4;
				}
				break;
			case 4:
				pincer.setPincerPosition(1700, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					desiredLocation = new double[] {-72, -140};
				}
				else {
					desiredLocation = new double[] {72, -140};
				}
				if ((Math.abs(desiredLocation[0] - pos[0]) < 15) && 
						(Math.abs(desiredLocation[1] - pos[1]) < 15)) {
					drive.move(0.0, 0.0);
					state = 5;
				} else {
					double[] adjustments = getAdjustments();
					drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
							RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
				}
				break;
			case 5:
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 6;
			default:
			}
		}

		else {
			switch (state) {
			case 0: 
				desiredLocation = new double[] {0, -140};
				pincer.setPincerPosition(1700, true, 0.0);
				if ((Math.abs(desiredLocation[0] - pos[0]) < 15) && 
						(Math.abs(desiredLocation[1] - pos[1]) < 15)) {
					drive.move(0.0, 0.0);
					state = 1;
				} else {
					double[] adjustments = getAdjustments();
					drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], 
							RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
				}
			case 1: 
				if (gameData.charAt(0) == startPosition.getSelected().charAt(0)) {
					pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				}
				state = 2;
				break;
			default:
			}
		}
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

	//Sends navigation data to the dashboard
	public void updateDashboard() {
		SmartDashboard.putNumber("Left Adjustments", leftSideAdjustment);
		SmartDashboard.putNumber("Right Adjustments", rightSideAdjustment);
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
		double[] posToDesired = {0,0};

		for (int i = 0; i < desiredLocation.length; i++) {
			posToDesired[i] = pos[i] - desiredLocation[i];
		}

		double desiredHeading = DeadReckon.modAngle(Math.atan2(posToDesired[0], posToDesired[1]));

		//Calculates the adjustment based on how much the robot needs to turn
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

	//Switches which camera is being sent to the display
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