package org.usfirst.frc.team888.robot.subsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//import java.net.InetAddress;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	// Instantiates the objects of the other classes controlled by navigation
	protected DriveTrain drive;
	protected DeadReckon location;
	protected Pincer pincer;
	protected Vision vision;
	protected WaypointTravel gps;
	protected Climber climber;
	protected OI oi;

	// Instantiates a chooser for the dashboard to select where the robot is at the start of the match
	public SendableChooser<String> startPosition;
	public SendableChooser<String> strategy;
	// Instantiates a string to store the randomizer pattern from the FMS
	protected String gameData;

	// Instantiates time variable
	protected int cycle;

	// Instantiates adjustments variables  
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	// Instantiates boolean for if manual controls are enabled. Defaults to disables (auto).
	protected boolean manualControl = false;

	// Instantiates offset for how often some methods are called in navExecute
	protected int schedulerOffset = 0;

	// Instantiates state for auto
	protected int state = 0;

	// Instantiates toggle booleans for drive direction
	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;

	// Instantiates initialized boolean
	protected boolean init = true;

	// Camera stuff
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	byte[] receiveData = new byte[8];
	byte[] byteRelativeLocation = null;

	public Navigation(DriveTrain p_drive, DeadReckon p_location, Pincer p_pince, Vision p_vision, 
			WaypointTravel p_gps, Climber p_climber, OI p_oi) {
		// Sets objects of necessary classes to be the objects passed in by Robot
		drive = p_drive;
		location = p_location;
		pincer = p_pince;
		vision = p_vision;
		gps = p_gps;
		climber = p_climber;
		oi = p_oi;

		// Declares the start position and the start location options
		startPosition = new SendableChooser<String>();
		startPosition.addDefault("Middle Start Position", "Middle");
		startPosition.addObject("Left Start Position", "Left");
		startPosition.addObject("Right Start Position", "Right");
		startPosition.addObject("Only go straight", "Straight");

		strategy = new SendableChooser<String>();
		strategy.addDefault("Cube on Switch", "Switch");
		strategy.addObject("Cube on Scale", "Scale");
		strategy.addObject("Don't Drop Cube", "Straight");

		try {
			serverSocket = new DatagramSocket(RobotMap.RIO_UDP_PORT);
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.setSoTimeout(1);
		} catch (Exception e) {

		} 
	}

	/**
	 * Initializes objects in or called by navigation 
	 */
	public void navigationInit() {
		// If it is time to initialize...
		if (init) {
			//...then initialize.
			schedulerOffset = 0;
			location.reset();
			init = false;
		}

		// Stores the randomizer data from the FMS as a string
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	/**
	 * Calls the methods that need to be run periodically
	 */
	public void navigationExecute() {
		// Methods run at 50Hz
		location.updateTracker();
		updateGuidenceControl();
		updateMotion();

		// Send the nav data to the dashboard once per second on the second 
		if (schedulerOffset == 0) {
			updateDashboard();
			location.updateDashboard();
			vision.sendMessage(cycle);
		}

		// Send the location data to the dashboard once per second on the half second
		if (schedulerOffset == 25) {
			location.updateDashboard();
		}

		// Increment the offset by one. If it has reached 50 (one second), set it back to zero.
		schedulerOffset = (schedulerOffset + 1) % 50;
		cycle++;
	}

	/**
	 * Gets the desired location
	 */
	public void updateGuidenceControl() {

		if (serverSocket != null && receivePacket != null) {
			try {
				serverSocket.receive(receivePacket);
				byteRelativeLocation = receivePacket.getData();
			} catch (IOException e) {
				byteRelativeLocation = null;
			}
		}
	}

	/**
	 * Moves the drive train
	 */
	public void updateMotion() {
		// If the game mode is auto, manual controls are off.
		manualControl = !DriverStation.getInstance().isAutonomous();

		// If manual controls are off...
		if (!manualControl) {
			//...run auto.
			autoRun();
		}

		// Otherwise run teleop controls.	
		else {
			// If both triggers are pressed...
			if (oi.getTriggers()) {
				// ...the motors go at full speed
				leftBaseDriveOutput = oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}
			// Otherwise go at 70% speed
			else {
				leftBaseDriveOutput = 0.7 * oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = 0.7 * oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}
			// If the joystick is less then 20% in either direction then ignore it
			if (Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.2 &&
					Math.abs(oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.2){
				leftBaseDriveOutput = 0.0;
				rightBaseDriveOutput = 0.0;
			}

			// Toggles what the front of the robot will be to the driver
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

			if (output) {
				rightDriveOutput = leftBaseDriveOutput;
				leftDriveOutput = rightBaseDriveOutput;
			} else {
				rightDriveOutput = -rightBaseDriveOutput;
				leftDriveOutput = -leftBaseDriveOutput;
			} 

			// Sends the movement command to the drive train to execute
			drive.move(leftDriveOutput, rightDriveOutput);
		}
	}

	/**
	 * Tells the robot what to do in auto
	 */
	public void autoRun() {
		// Switch statement that takes the start position of the robot
		switch (startPosition.getSelected()) {

		case "Middle":	
			// The case that robot starts in the middle position
			switch (state) {
			case 0:
				pincer.setPincerPosition(1700, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (gps.goToWaypoint(-75, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(72, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				break;
			case 1:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1700, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 2;
				break;
			case 2:
				// Lower the pincer
				pincer.setPincerPosition(1700, true, 0.0);
				if (gps.goToWaypoint(0, 0, 0, -RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				vision.sendMessage(cycle);
				state = 4;
				break;
			case 4:
				pincer.setPincerPosition(700, true, 0.0);
				if (gps.goToWaypoint(-5.5, 54, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 5;
				}
			case 5:
				// Close the pincer and go to the next step
				pincer.setPincerPosition(700, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kReverse);
				state = 6;
				break;
			case 6:
				// Lower the pincer
				pincer.setPincerPosition(1700, true, 0.0);
				if (gps.goToWaypoint(0, 0, 0, -RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 7;
				}
				break;
			case 7:
				// Lower the pincer
				pincer.setPincerPosition(1700, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (gps.goToWaypoint(-72, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				else {
					if (gps.goToWaypoint(72, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				break;
			case 8:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1700, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 9;
				break;
			default:
			}
			break;

		case "Left":
			// The case that robot starts in the right position
			switch (state) {
			case 0: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (gps.goToWaypoint(0, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(0, 210, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (gps.goToWaypoint(20, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 4;
					}
				}
				else {
					if (gps.goToWaypoint(226, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1800, true, 0.0);
				if (gps.goToWaypoint(226, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1800, true, 0.0);
				if (gps.goToWaypoint(206, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				pincer.setPincerPosition(1800, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 5;
				break;
			default:
			}
			break;

		case "Right":
			// The case that robot starts in the right position
			switch (state) {
			case 0: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (gps.goToWaypoint(0, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(0, 210, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (gps.goToWaypoint(-20, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 4;
					}
				}
				else {
					if (gps.goToWaypoint(-226, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1800, true, 0.0);
				if (gps.goToWaypoint(-226, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1800, true, 0.0);
				if (gps.goToWaypoint(-206, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				pincer.setPincerPosition(1800, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 5;
				break;
			default:
			}
			break;
		default:
		}

		SmartDashboard.putNumber("State", state);
	}


	/**
	 * Sends navigation data to the dashboard
	 */
	public void updateDashboard() {
		SmartDashboard.putString("Game Pattern", gameData);
		SmartDashboard.putNumber("leftOutput", leftDriveOutput);
		SmartDashboard.putNumber("rightOutput", rightDriveOutput);
		SmartDashboard.putNumber("State", state);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}