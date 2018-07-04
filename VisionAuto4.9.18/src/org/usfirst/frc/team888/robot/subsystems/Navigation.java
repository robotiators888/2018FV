package org.usfirst.frc.team888.robot.subsystems;

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

	// Instantiates initialized boolean
	protected boolean init = true;

	// Camera stuff
	UDPReceiver receiver;
	byte[] byteRelativeLocation = null;
	int relativeX;

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
		strategy.addDefault("Cube on Switch", "Drop");
		strategy.addObject("No cube on far side", "Cancel");

		receiver = new UDPReceiver();
		receiver.start();
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
		updateGuidenceControl();

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
<<<<<<< HEAD
	public int updateGuidenceControl() {

		if (serverSocket != null && receivePacket != null) {
			try {
				serverSocket.receive(receivePacket);
				byteRelativeLocation = receivePacket.getData();
				relativeX = ByteBuffer.wrap(byteRelativeLocation).getInt();
			} catch (IOException e) {
				byteRelativeLocation = null;
				relativeX = 0;
			}
		}
		return relativeX;
=======
	public void updateGuidenceControl() {
		Number[] tmp = CommunicationsBuffer.getHighestCycle();
		if (tmp != null) cubeLocation = location.cubeLocation((int) tmp[0],
					new double[] {(double) tmp[1], (double) tmp[2]});
>>>>>>> branch 'Charlie' of https://github.com/robotiators888/2018FV.git
	}


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
					if (gps.goToWaypoint(-75, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(72, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				break;
			case 1:
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (gps.goToWaypoint(-75, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				else {
					if (gps.goToWaypoint(72, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 2;
					}
				}
				break;
			case 2:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1100, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 3;
				break;
			case 3:
				// Lower the pincer
				pincer.setPincerPosition(700, true, 0.0);
				if (gps.goToWaypoint(-5.5, 0, 0, -RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 4;
				}
				break;
			case 4:
				vision.sendMessage(location.getCycle());
				pincer.setPincerPosition(700, true, 0.0);
<<<<<<< HEAD
				if (gps.goToWaypoint(updateGuidenceControl(), 54, 0, RobotMap.DEFAULT_AUTO_SPEED) || !pincer.proximity.get()) {
					state = 5;
=======
				if (cubeLocation == null) {
					if (gps.goToWaypoint(-5.5, 54, 0, RobotMap.DEFAULT_AUTO_SPEED)) state = 5;
>>>>>>> branch 'Charlie' of https://github.com/robotiators888/2018FV.git
				}
				else {
					if (pincer.getProzimity()) {
						gps.goToWaypoint(cubeLocation[0], cubeLocation[1], 
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
					if (gps.goToWaypoint(-30, 30, ((11 * Math.PI) / 6), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				else {
					if (gps.goToWaypoint(30, 30, (Math.PI / 6), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				break;
			case 7:
				// Lower the pincer
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (gps.goToWaypoint(-75, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				else {
					if (gps.goToWaypoint(72, 80, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 8;
					}
				}
				break;
			case 8:
				pincer.setPincerPosition(1100, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (gps.goToWaypoint(-75, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 9;
					}
				}
				else {
					if (gps.goToWaypoint(72, 93, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
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
					if (gps.goToWaypoint(0, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(0, 210, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
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
				pincer.setPincerPosition(1500, true, 0.0);
				if (gps.goToWaypoint(226, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1500, true, 0.0);
				if (gps.goToWaypoint(206, 160, ((Math.PI * 3) / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
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
					if (gps.goToWaypoint(0, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						state = 1;
					}
				}
				else {
					if (gps.goToWaypoint(0, 210, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
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
				pincer.setPincerPosition(1500, true, 0.0);
				if (gps.goToWaypoint(-226, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1500, true, 0.0);
				if (gps.goToWaypoint(-206, 160, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
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
			gps.goToWaypoint(0, 100, 0, RobotMap.DEFAULT_AUTO_SPEED);
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

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}