package org.usfirst.frc.team888.robot.workers;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation {

	private static Navigation nav;
	
	// Instantiates the objects of the other classes controlled by navigation
	protected Climber climber;
	protected DriveTrain drive;
	protected DeadReckon location;
	protected OI oi;
	protected Pincer pincer;
	protected Vision vision;
	protected WaypointTravel guidence;

	// Instantiates a chooser for the dashboard to select where the robot is at the start of the match.
	public SendableChooser<String> startPosition;
	public SendableChooser<String> strategy;

	// Instantiates a string to store the randomizer pattern from the FMS
	protected String gameData;

	// Instantiates adjustments variables  
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected double[] cubeLocation = new double[2];

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

	// Camera stuff
	UDPReceiver receiver;
	byte[] byteRelativeLocation = null;
	int relativeX;

	public Navigation() {
		// Sets objects of necessary classes to be the objects passed in by Robot
		climber = Climber.getInstance();
		drive = DriveTrain.getInstance();
		location = DeadReckon.getInstance();
		pincer = Pincer.getInstance();
		oi = OI.getInstance();
		vision = Vision.getInstance();
		guidence = WaypointTravel.getInstance();

		// Declares the start position and the start location options
		startPosition = new SendableChooser<String>();
		startPosition.addDefault("Middle Start Position", "Middle");
		startPosition.addObject("Left Start Position", "Left");
		startPosition.addObject("Right Start Position", "Right");
		startPosition.addObject("Only go straight", "Straight");

		strategy = new SendableChooser<String>();
		strategy.addDefault("Cube on Switch", "Drop");
		strategy.addObject("No cube on far side", "Cancel");

		receiver = new UDPReceiver();
		receiver.start();
	}
	
	/**
	 * Accessor method for the Navigation Singleton.
	 * @return The object of Navigation
	 */
	public static Navigation getInstance() {
		if (nav != null) {
			synchronized(Navigation.class) {
				if (nav != null) {
					nav = new Navigation();
				}
			}
		}

		return nav;	
	}

	/**
	 * Initializes objects in or called by navigation 
	 */
	public void navigationInit() {
		//...then initialize.
		schedulerOffset = 0;
		location.reset();


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
		location.updateLog(state);
		location.updateDashboard();

		// Send the nav data to the dashboard once per second on the second 
		if (schedulerOffset == 0) {
			updateDashboard();
			location.updateDashboard();
			vision.sendMessage(location.cycle);
		}

		// Send the location data to the dashboard once per second on the half second
		if (schedulerOffset == 25) {
			location.updateDashboard();
		}

		// Increment the offset by one. If it has reached 50 (one second), set it back to zero.
		schedulerOffset = (schedulerOffset + 1) % 50;
	}

	/**
	 * Gets the desired location
	 */
	public void updateGuidenceControl() {
		double[] tmp = CommunicationsBuffer.getHighestCycle();
		if (tmp != null) cubeLocation = location.cubeLocation((int) tmp[0],
				new double[] {tmp[1], tmp[2]});
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
				if (guidence.goToWaypoint(79, 52, location.getHeading(), RobotMap.DEFAULT_AUTO_SPEED)) state = 10;
				/*pincer.setPincerPosition(1700, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (gps.goToWaypoint(-75, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(72, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}*/
				break;
			case 1:
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (guidence.goToWaypoint(-75, 96, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				else {
					if (guidence.goToWaypoint(72, 96, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1100, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 10; //3;
				break;
			case 3:
				// Lower the pincer
				pincer.setPincerPosition(1000, true, 0.0);
				if (guidence.goToWaypoint(-5.5, 6, 0, -RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				vision.sendMessage(location.getCycle());
				pincer.setPincerPosition(700, true, 0.0);
				if (cubeLocation == null) {
					if (guidence.goToWaypoint(-5.5, 54, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 5;
					}
				}
				else {
					if (pincer.getProzimity()) {
						guidence.goToWaypoint(cubeLocation[0], cubeLocation[1], 
								0, RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 5;
					}
				}
				break;
			case 5:
				// Close the pincer and go to the next step
				pincer.setPincerPosition(700, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kReverse);
				state = 6;
				break;
			case 6:
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (guidence.goToWaypoint(-30, 30, ((11 * Math.PI) / 6), -RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 7;
					}
				}
				else {
					if (guidence.goToWaypoint(30, 30, (Math.PI / 6), -RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 7;
					}
				}
				break;
			case 7:
				// Lower the pincer
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (guidence.goToWaypoint(-75, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				else {
					if (guidence.goToWaypoint(72, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				break;
			case 8:
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (guidence.goToWaypoint(-75, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 9;
					}
				}
				else {
					if (guidence.goToWaypoint(72, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 9;
					}
				}
				break;
			case 9:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1100, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 10;
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
					if (guidence.goToWaypoint(0, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (guidence.goToWaypoint(0, 210, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						if (strategy.getSelected().equals("Cancel")) {
							state = 6;
						}
						else {
							state = 1;
						}
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (guidence.goToWaypoint(20, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 4;
					}
				}
				else {
					if (guidence.goToWaypoint(226, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1500, true, 0.0);
				if (guidence.goToWaypoint(226, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1500, true, 0.0);
				if (guidence.goToWaypoint(206, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				if (pincer.setPincerPosition(1500, true, 0.0)) {
					pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
					state = 5;
				}
				break;
			case 5:
				pincer.setPincerPosition(1500, false, 0.0);
				state = 6;
			default:
			}
			break;

		case "Right":
			// The case that robot starts in the right position
			switch (state) {
			case 0: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (guidence.goToWaypoint(0, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (guidence.goToWaypoint(0, 210, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						if (strategy.getSelected().equals("Cancel")) {
							state = 6;
						}
						else {
							state = 1;
						}
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (guidence.goToWaypoint(-20, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 4;
					}
				}
				else {
					if (guidence.goToWaypoint(-226, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1500, true, 0.0);
				if (guidence.goToWaypoint(-226, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1500, true, 0.0);
				if (guidence.goToWaypoint(-206, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				if (pincer.setPincerPosition(1500, true, 0.0)) {
					pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
					state = 5;
				}
				break;
			case 5:
				pincer.setPincerPosition(1500, false, 0.0);
				state = 6;
			default:
			}
			break;

		case "Straight":
			guidence.goToWaypoint(0, 100, 0, RobotMap.DEFAULT_AUTO_SPEED);
			pincer.setPincerPosition(2115, true, 0.0);
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
}