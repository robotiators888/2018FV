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

	// Instantiates a chooser for the dashboard to select where the robot is at the start of the match
	public SendableChooser<String> startPosition;
	public SendableChooser<String> strategy;
	// Instantiates a string to store the randomizer pattern from the FMS
	protected String gameData;

	// Instantiates time variable
	protected double time;

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

	protected boolean previousCameraButtonState = false;

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

		strategy = new SendableChooser<String>();
		strategy.addDefault("Cube on Switch", "Switch");
		strategy.addObject("Cube on Scale", "Scale");
		strategy.addObject("Don't Drop Cube", "Straight");
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

		// If the camera switch button was pressed and the the wrong camera is displaying...
		if(oi.getRightStickButton(5) && !previousCameraButtonState) {
			//...switch the cameras
			vision.switchCamera();
			previousCameraButtonState = true;
		}
		// Otherwise the camera state is correct. 
		else if (!oi.getRightStickButton(5)) {
			previousCameraButtonState = false;
		}

		// Send the nav data to the dashboard once per second on the second 
		if (schedulerOffset == 0) {
			updateDashboard();
			location.updateDashboard();
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
		//desiredLocation = RobotMap.DESIRED_LOCATION;
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
			if(oi.getTriggers()) {
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
			if(Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.2 &&
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

			if(output) {
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
					if (!gps.goToWaypoint(0, 36, ((Math.PI * 7) / 4), RobotMap.DEFAULT_AUTO_SPEED)) {
						// ...go there.
						gps.goToWaypoint(0, 36, ((Math.PI * 7) / 4), RobotMap.DEFAULT_AUTO_SPEED);
					}
					// Otherwise...
					else {
						// Otherwise...
						state = 1;
					}
				} else {
					// If the robot has not arrived at the switch...
					if (!gps.goToWaypoint(0, 36, ((Math.PI) / 4), RobotMap.DEFAULT_AUTO_SPEED)) {
						// ...go there.
						gps.goToWaypoint(0, 36, ((Math.PI) / 4), RobotMap.DEFAULT_AUTO_SPEED);
					}
					// Otherwise...
					else {
						// Otherwise...
						state = 1;
					}
				}
				break;
			case 1:
				// Lower the pincer
				pincer.setPincerPosition(1700, true, 0.0);

				// If the our alliance has the left side of the switch
				if (gameData.charAt(0) == 'L') {
					// If the robot has not arrived at the switch...
					if (!gps.goToWaypoint(-75, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						// ...go there.
						gps.goToWaypoint(-75, 89, 0, RobotMap.DEFAULT_AUTO_SPEED);
					}
					// Otherwise...
					else {
						// Otherwise...
						state = 2;
					}
				}

				// If the our alliance has the right side of the switch
				else {
					// If the robot has not arrived at the switch...
					if (!gps.goToWaypoint(72, 89, 0, RobotMap.DEFAULT_AUTO_SPEED)) {
						// ...go there.
						gps.goToWaypoint(72, 89, 0, RobotMap.DEFAULT_AUTO_SPEED);
					}
					// Otherwise...
					else {
						// Otherwise...
						state = 2;
					}
				}
				break;
			case 2:
				// Open the pincer and go to the next step
				pincer.setPincerPosition(1700, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 3;
			default:
			}
			break;

		case "Left":
			// The case that robot starts in the left position
			switch (state) {
			case 0: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (!gps.goToWaypoint(0, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						gps.goToWaypoint(0, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 1;
					}
				}
				else {
					if (!gps.goToWaypoint(0, 210, (Math.PI /2), RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
						gps.goToWaypoint(0, 210, (Math.PI /2), RobotMap.DEFAULT_AUTO_SPEED * 1.5);
					}
					else {
						state = 1;
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (!gps.goToWaypoint(20, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED)) {
						gps.goToWaypoint(20, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 3;
					}
				}
				else {
					if (!gps.goToWaypoint(154, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
						gps.goToWaypoint(154, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5);
					}
					else {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1800, true, 0.0);
				if (!gps.goToWaypoint(154, 185, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
					gps.goToWaypoint(154, 185, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5);
				}
				else {
					state = 3;
				}
				break;
			case 3:
				pincer.setPincerPosition(1800, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 4;
			default:
			}
			break;

		case "Right":
			// The case that robot starts in the right position
			switch (state) {
			case 0: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (!gps.goToWaypoint(0, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						gps.goToWaypoint(0, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 1;
					}
				}
				else {
					if (!gps.goToWaypoint(0, 210, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
						gps.goToWaypoint(0, 210, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED * 1.5);
					}
					else {
						state = 1;
					}
				}
				break;
			case 1: 
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'R') {
					if (!gps.goToWaypoint(-16, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED)) {
						gps.goToWaypoint(-16, 148, ((Math.PI * 3) /2), RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 4;
					}
				}
				else {
					if (!gps.goToWaypoint(-220, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
						gps.goToWaypoint(-220, 200, Math.PI, RobotMap.DEFAULT_AUTO_SPEED * 1.5);
					}
					else {
						state = 2;
					}
				}
				break;
			case 2:
				pincer.setPincerPosition(1800, true, 0.0);
				if (gameData.charAt(0) == 'L') {
					if (!gps.goToWaypoint(-220, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
						gps.goToWaypoint(-220, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED * 1.5);
					}
					else {
						state = 3;
					}
				}
				break;
			case 3:
				pincer.setPincerPosition(1800, true, 0.0);
				if (!gps.goToWaypoint(-200, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED * 1.5)) {
					gps.goToWaypoint(-200, 148, (Math.PI / 2), RobotMap.DEFAULT_AUTO_SPEED * 1.5);
				}
				else {
					state = 4;
				}
				break;
			case 4:
				pincer.setPincerPosition(1800, true, 0.0);
				pincer.pincerPiston.set(DoubleSolenoid.Value.kForward);
				state = 5;
			/*case 5:
				if (gameData.charAt(0) == 'R') {
					if (!gps.goToWaypoint(0, 148, 0, -RobotMap.DEFAULT_AUTO_SPEED)) {
						gps.goToWaypoint(0, 148, 0, -RobotMap.DEFAULT_AUTO_SPEED);
					}
					else {
						state = 6;
					}
				} else {
					state = 6;
				}
				break; */
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
	}

	/**
	 * @return The array with zeros for both adjustments
	 */
	public double[] reset() {
		double[] j = {0,0};
		return j;
	}


	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}