package org.usfirst.frc.team888.robot;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpifirst
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;




public class Straight {

	public static CANTalon rearLeftMotor;
	public static CANTalon rearRightMotor;
	public static CANTalon frontLeftMotor;
	public static CANTalon frontRightMotor;
	public static double leftEncoderValue = 0;
	public static double rightEncoderValue = 0;

	public static void straightStart(){

		frontRightMotor = new CANTalon(RobotMap.rearLeftSRX);
		frontLeftMotor = new CANTalon(RobotMap.frontLeftSRX);
		rearRightMotor = new CANTalon(RobotMap.rearRightSRX);
		rearLeftMotor = new CANTalon(RobotMap.rearLeftSRX);

		rearLeftMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rearRightMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rearLeftMotor.changeControlMode(TalonControlMode.PercentVbus);
		rearRightMotor.changeControlMode(TalonControlMode.PercentVbus);

	}


	public static void getEncoderValue(){										//Get encoder values
		leftEncoderValue = rearLeftMotor.getEncVelocity();						//Gets encoders speed and direction
		rightEncoderValue = rearRightMotor.getEncVelocity();
		SmartDashboard.putNumber("Left Encoder", leftEncoderValue);
		SmartDashboard.putNumber("Right Encoder", rightEncoderValue);

	}


	public static void goStraight(){											//Does all calculations in order to go straight
		// Factor in how close the motors are to the power limit	
		// change one side to match the power of the other side by increasing until it reaches the power limit
		// This will change the encoders value to DECREASE it 

		public final double DISTANCEPERONECLICK = 0.75; //inches (not the acutal value)
		public final double INCHESPERREV = 7; //(not actual value)
		public final double RIGHTCLICKPERREV = 200; //(not actual value)
		public final double LEFTCLICKPERREV = 250; //(not actual value)
		

		public double powerLimit = 1;
		public double totalDistance = 0;
		public double encoderClicks = 0;
		
		//public double rightAdjustmentPower = 0, leftAdjustmentPower = 0;
		//public double rightMotorActualPower = 0, leftMotorActualPower = 0; 

		totalDistance = encoderClicks * DISTANCEPERONECLICK;	//finds the total distance the robot has traveled
		
		if (encoderClicks < )
		
	}
}

