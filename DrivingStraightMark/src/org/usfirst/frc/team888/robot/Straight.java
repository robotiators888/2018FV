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
		leftEncoderValue = rearLeftMotor.getEncVelocity();						//Gets encoders speed and direction
		rightEncoderValue = rearRightMotor.getEncVelocity();

		while (leftEncoderValue != rightEncoderValue) { 			
			while (leftEncoderValue < rightEncoderValue){
				leftEncoderValue++;												//This will change the encoders value to INCREASE it 
			} 
			while (leftEncoderValue > rightEncoderValue){
				leftEncoderValue--;												////This will change the encoders value to DECREASE it 
			}

		}

	}
}
