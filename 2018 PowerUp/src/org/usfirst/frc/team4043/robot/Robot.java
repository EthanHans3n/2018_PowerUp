/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4043.robot;

import org.usfirst.frc.team4043.robot.subsystems.DriveTrain;
import org.usfirst.frc.team4043.robot.subsystems.Elevator;
import org.usfirst.frc.team4043.robot.subsystems.ElevatorPID;
import org.usfirst.frc.team4043.robot.subsystems.Intake;
import org.usfirst.frc.team4043.robot.subsystems.Shifter;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static DriveTrain driveTrain;
	public static Intake intake;
	public static AHRS ahrs;
	public static ElevatorPID elevatorPID;
	public static AnalogInput ai;
	public static Elevator elevator;
	public static Shifter shifter;
	public static OI m_oi;
	
	public static boolean keepState = true;
	int state = 1;
	double currentUltrasonic = 0;
	public static boolean toggleKeep = false;
	String gameData;
	String autoChoice;
	double time = Timer.getFPGATimestamp();

	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		driveTrain = new DriveTrain();
		ahrs = new AHRS(SPI.Port.kMXP);
		intake = new Intake();
		elevatorPID = new ElevatorPID();
		ai = new AnalogInput(0);
		elevator = new Elevator();
		shifter = new Shifter();
		m_oi = new OI();
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
		
		//This should set the feedback from motorFR as 1ms per sample and unlimited bandwidth
		RobotMap.motorBR.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		//Sets the feedback device as a quad encoder, which is what the cimcoder is
		RobotMap.motorBR.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		RobotMap.motorBR.setSelectedSensorPosition(0, 0, 0);
		
		RobotMap.evelator.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		RobotMap.evelator.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		RobotMap.evelator.setSelectedSensorPosition(0, 0, 0);
		//new ArmsDown();
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		boolean cross = SmartDashboard.getBoolean("DB/Button 0", false);
		boolean ds1 = SmartDashboard.getBoolean("DB/Button 1", false);
		boolean ds2 = SmartDashboard.getBoolean("DB/Button 2", false);
		boolean ds3 = SmartDashboard.getBoolean("DB/Button 3", false);
		double dashData = SmartDashboard.getNumber("DB/Slider 0", 0.0);
		double scaleData = SmartDashboard.getNumber("DB/Slider 1", 0.0);
		
		if (scaleData > 2) {
			if (ds1) {
				if (gameData.substring(0, 1) == "L") {
					autoChoice = "ds1cL";
				} else {
					autoChoice = "ds1cR";
				}
			} else if (ds3) {
				if (gameData.substring(0, 1) == "L") {
					autoChoice = "ds3cL";
				} else {
					autoChoice = "ds3cR";
				}
			}
		} else if (cross) {
			if (ds1) {
				autoChoice = "ds1cross";
			} else if (ds2) {
				autoChoice = "ds2cross";
			} else if (ds3) {
				autoChoice = "ds3cross";
			}
		} else if (ds1) {
			if (gameData.substring(0, 1) == "L") {
				autoChoice = "ds1L";
			} else {
				autoChoice = "ds1R";
			}
		} else if (ds2) {
			if (gameData.substring(0, 1) == "L") {
				autoChoice = "ds2L";
			} else {
				autoChoice = "ds2R";
			}
		} else if (ds3) {
			if (gameData.substring(0, 1) == "L") {
				autoChoice = "ds3L";
			} else {
				autoChoice = "ds3R";
			}
		}
		
		if (dashData > 2) {
			state = 0;
		}
		
		time = Timer.getFPGATimestamp();
		ahrs.reset();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		
		//System.out.println(RobotMap.motorBR.getSelectedSensorPosition(0));
		
//		switch (autoChoice) {
//		case "ds1L": ds1L();
//		case "ds1R": ds1R();
//		case "ds2L": ds2L();
//		case "ds2R": ds2R();
//		case "ds3L": ds3L();
//		case "ds3R": ds3R();
//		case "ds1cR" : ds1cR();
//		case "ds1cL" : ds1cL();
//		case "ds3cR" : ds3cR();
//		case "ds3cL" : ds3cL();
//		case "ds1cross": ds1cross();
//		case "ds3cross": ds3cross();
//		case "ds2cross" : ds2cross();
//		}
		
		autoTest();
	}
	
	public double turnToAngle(double wantedAngle){ //Takes in a wanted angle and returns the turnSpeed to get there
		double currentAngle = ahrs.getAngle(); //In order to determine where we are, take in the current gyro value from the navx
		double rotateSpeed;
		
		if (currentAngle > wantedAngle - 2) { 					//If we are too far to the right of where we want to be...
			rotateSpeed = -.7d;	//turn left (negative number)
		} else if (currentAngle < wantedAngle + 2) {			//Otherwise, if we are too far left ...
			rotateSpeed = 0.7d;	//turn right (positive number)
		} else {												//If we are right on track ...
			rotateSpeed = 0d;									//don't rotate
		}
		
		//Just sanity checks for our output. Turning for arcade drive has to be between -1 and 1
//		if (rotateSpeed > 1) {												
//			rotateSpeed = 1;
//		} else if (rotateSpeed < -1) {
//			rotateSpeed = -1;
//		} else if (rotateSpeed < .1 && rotateSpeed > 0) {		//Checks for a value small enough that it won't turn the robot
//			rotateSpeed = .1;
//		} else if (rotateSpeed > -.1 && rotateSpeed < 0) {		//Checks for a value small enough that it won't turn the robot
//			rotateSpeed = -.1;
//		}
		
		return rotateSpeed;
	}
	
	public double driveToFeet(double wantedDistance) { 
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance < wantedDistance - 300) {
			driveSpeed = 0.5d;
		} else {
			driveSpeed = 0d;
		}
		
//		if (driveSpeed > 1) {
//			driveSpeed = 1;
//		} else if (driveSpeed < .1 && driveSpeed > 0) {
//			driveSpeed = .1d;
//		} else if (driveSpeed < -1) {
//			driveSpeed = -1;
//		} else if (driveSpeed > -.1 && driveSpeed < 0) {
//			driveSpeed = -.1d;
//		}
		
		return driveSpeed;
	}
	
	public double backToFeet(double wantedDistance) {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance > wantedDistance) {
			driveSpeed = 0.5d;
		} else {
			driveSpeed = 0d;
		}
		
//		if (driveSpeed > 1) {
//			driveSpeed = 1;
//		} else if (driveSpeed < .1 && driveSpeed > 0) {
//			driveSpeed = .1d;
//		} else if (driveSpeed < -1) {
//			driveSpeed = -1;
//		} else if (driveSpeed > -.1 && driveSpeed < 0) {
//			driveSpeed = -.1d;
//		}
		
		return driveSpeed;
	}
	
	public void autoTest() {
		//double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (currentAngle < 90) {
			Robot.driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			System.out.println(ahrs.getAngle());
		}
		
//		if (currentAngle < 90) {
//			System.out.println("turning");
//			Robot.driveTrain.drive.arcadeDrive(0, 1);
//		}
		
		//System.out.println(ahrs.getAngle());
	}
	
	public void ds1L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 12608) {
				driveTrain.drive.arcadeDrive(driveToFeet (12608), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 45-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(45));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < (11886)) {
				driveTrain.drive.arcadeDrive(driveToFeet(11886), turnToAngle(45));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 8405) {
				driveTrain.drive.arcadeDrive(driveToFeet(8405), turnToAngle(0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 6) {
			currentUltrasonic = ai.getValue();
			if (currentUltrasonic < 30) { //Change this to the actual distance it should be
				driveTrain.drive.arcadeDrive(.25, turnToAngle(0));
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 8;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 8) {
			if (currentDistance > 20172) {
				elevatorPID.setSetpoint(0);
				driveTrain.drive.arcadeDrive(backToFeet(20172), turnToAngle(0));
			} else {
				state = 9;
			}
		} else if (state == 9) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 10;
			}
		}
	}
	public void ds3R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 12608) {
				driveTrain.drive.arcadeDrive(driveToFeet(12608), turnToAngle(0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 45-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(45));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < (11886)) {
				driveTrain.drive.arcadeDrive(driveToFeet(11886), turnToAngle(45));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle < -2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 8405) {
				driveTrain.drive.arcadeDrive(driveToFeet(8405), turnToAngle(0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 8;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 8) {
			if (currentDistance > 20172) {
				elevatorPID.setSetpoint(0);
				driveTrain.drive.arcadeDrive(backToFeet(20172), turnToAngle(0));
			} else {
				state = 9;
			}
		} else if (state == 9) {
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
		}
	}
	
	public void ds2L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
	
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 14709) {
				driveTrain.drive.arcadeDrive(driveToFeet(14709), turnToAngle(0));
			} else {
				state = 2;
			}
		}
			//second stage begins, we are turning -90 degrees 
		else if (state == 2) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0,turnToAngle(-90)); 
			} else {
				state = 3;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
		}
		
		else if (state == 3) {
			if (currentDistance >  16390) {
				driveTrain.drive.arcadeDrive(driveToFeet(16390), turnToAngle(-90));
				//zoom zoom
			} else {
				state = 4;
			}					
		}
			
		else if (state==4) {
			if (currentAngle < -2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				state = 5;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
		}
			
		else if (state == 5) {
			if (currentDistance < 14709) {
				driveTrain.drive.arcadeDrive(driveToFeet(14709), turnToAngle(0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 6) {
			currentUltrasonic = ai.getValue();
			if (currentUltrasonic < 30) { //Change this to the actual distance it should be
				driveTrain.drive.arcadeDrive(driveToFeet(.25), turnToAngle(0));
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 8;
			}
		}
		else if (state == 8) { 
			if (currentDistance > 12608) {
				driveTrain.drive.arcadeDrive(backToFeet(12608), turnToAngle(0));
			} else {
				state = 9;
			}			
		}
		else if (state == 9) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 10;
			}
		}
	}
	
	public void ds1cross() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();

		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 5043) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(5043), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle > -30 +2){ //if the angle more than -30
				driveTrain.drive.arcadeDrive(0, turnToAngle(-30)); //turn to -30 degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 48/12){ //if the robot has moved less than 5 feet
				driveTrain.drive.arcadeDrive(driveToFeet(12608), turnToAngle(-30)); // move 3 feet
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 4;
			}
		} else if (state == 4) {
			if (currentAngle < -2) { //if the angle less than 0
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 37823) { //if the robot isn't in the null territory
				driveTrain.drive.arcadeDrive(driveToFeet(37823), turnToAngle(0)); //drive 20 feet forward
			} else {
				state = 6;
			}
		}
	}
	
	public void ds3cross() {
 		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
        
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
		   if (currentDistance < 21013) {
			   driveTrain.drive.arcadeDrive(driveToFeet(21013), turnToAngle(0));
			} else {
			   state = 2;
			}
		   } else if (state == 2) {
			   if (currentAngle < 51.3) {
				   driveTrain.drive.arcadeDrive(0, turnToAngle(51.3));
			   } else {
				   RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				   state = 3;
			   }
		   } else if (state == 3) {
			   if (currentDistance < 13133) {
				   driveTrain.drive.arcadeDrive(driveToFeet(13133) , turnToAngle(51.3));
			   } else {
				   state = 4;
			   }
		   } else if ( state == 4 ) {
			   if ( currentAngle > 0) {
				   driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			   } else {
				   RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				   state = 5;
			   }
		   } else if (state == 5) {
			   if (currentDistance < 27737) {
				   driveTrain.drive.arcadeDrive(driveToFeet(27737), turnToAngle(0));
			   } else {
				   state = 6;
 		 	  }
 	 	  }
	}
	
	public void ds2cross() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
	
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 5) {
			} else {
				state = 1;
			}	        
		} else if (state == 1) {
	 		if (currentDistance < 5043) {
	 			driveTrain.drive.arcadeDrive(driveToFeet(5043), turnToAngle(0));
	 		} else {
	 			state = 2;
	 		}
		} else if (state == 2) {
			if (currentAngle < 30 - 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(30));
			} else {
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 37823) {
				driveTrain.drive.arcadeDrive(driveToFeet(37823), turnToAngle(30));
			} else {
				state = 4;
			
			}
	 	} else if (state == 5) {
	 		if (currentDistance > 30258) {
	 			driveTrain.drive.arcadeDrive(backToFeet(30258), turnToAngle(0));
	 		} else {
	 			state = 6;
	 		}
	 	}
	}
	
	public void ds2R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle(); 
	
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 14709) {
				driveTrain.drive.arcadeDrive(driveToFeet(14709), turnToAngle(0));
			} else {
				state = 2;
			}
		
		} else if (state == 2) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90)); 
			} else {
				state = 3;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
			
		} else if (state == 3) {
			if (currentDistance >  16390) {
				driveTrain.drive.arcadeDrive(driveToFeet(16390), turnToAngle(90));
				//zoom zoom
			} else {
				state = 4;
			}
			
		} else if (state == 4) {
			if (currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0,turnToAngle(0));
			} else {
				state = 5;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}	
			
		} else if (state == 5) {
			if (currentDistance < 14709) {
				driveTrain.drive.arcadeDrive(driveToFeet(14709), turnToAngle(0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 6) {
			currentUltrasonic = ai.getValue();
			if (currentUltrasonic < 30) { //Change this to the actual distance it should be
				driveTrain.drive.arcadeDrive(driveToFeet(.25), turnToAngle(0));
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 8;
			}
		
		} else if (state == 8) { 
			if (currentDistance > 12608) {
				driveTrain.drive.arcadeDrive(backToFeet(12608), turnToAngle(0));
			} else {
				state = 9;
			}			
		
		} else if (state == 9) {
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 10;
			}
		}
	}
	
	public void ds1cL() { //driver station 1 scale left
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 68081) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(68081), turnToAngle(0)); //drive 20 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance > 64088) {
				driveTrain.drive.arcadeDrive(backToFeet(64088), turnToAngle(90));
			} else {
				state = 4;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 4) {
			elevatorPID.setSetpoint(1000); //Change to what it should actually be
			state = 5;
			time = Timer.getFPGATimestamp();
		} else if (state == 5) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 6;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
		} else if (state == 6) {
			if (currentDistance > -2101) {
				driveTrain.drive.arcadeDrive(backToFeet(-2101), turnToAngle(90));
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (currentAngle > 0) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentDistance > -12608) {
				driveTrain.drive.arcadeDrive(backToFeet(-12608), turnToAngle(0));
			} else {
				state = 9;
			}
		}
	}
		
	public void ds3cR() { //driver station 3 scale right
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 50430) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(50430), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle > -68 + 2){ //if the angle  than 
				driveTrain.drive.arcadeDrive(0, turnToAngle(-68)); //turn to  degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 13448){ //if the robot has moved less than  feet
				driveTrain.drive.arcadeDrive(driveToFeet(13448), turnToAngle(-68)); // move  feet
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if (currentAngle < -2){ //if the angle is  than 0
				driveTrain.drive.arcadeDrive(0, turnToAngle(0)); //turn to 0 degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		}
	}
	
	public void ds1R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 5043) {
				driveTrain.drive.arcadeDrive(driveToFeet (5043), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 70 - 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(70));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 41605) {
				driveTrain.drive.arcadeDrive(driveToFeet(41605), turnToAngle(70));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 10086) {
				driveTrain.drive.arcadeDrive (driveToFeet (10086), turnToAngle (0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 6) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 7;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 7) {
			if (currentDistance > 25215) {
				elevatorPID.setSetpoint(0);
				driveTrain.drive.arcadeDrive(backToFeet(25215), turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
		}
	}
			
	public void ds3L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 5043) {
				driveTrain.drive.arcadeDrive(driveToFeet (5043), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle > -70 + 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-70));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 41605) {
				driveTrain.drive.arcadeDrive(driveToFeet(41605), turnToAngle(-70));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle < -2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 10086) {
				driveTrain.drive.arcadeDrive (driveToFeet (10086), turnToAngle (0));
				elevatorPID.setSetpoint(300); //Change to actual value
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 6) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 7;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 7) {
			if (currentDistance > 25215) {
				elevatorPID.setSetpoint(0); //Maybe need to change this
				driveTrain.drive.arcadeDrive(backToFeet(25215), turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
		}
	}
	
	public void ds1cR() { //driver station 1 scale right
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
				
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 47909) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(47909), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { // if the angle less than 90
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90)); //turn to 90 degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 52111) {
				driveTrain.drive.arcadeDrive(driveToFeet(52111), turnToAngle(90));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if (currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 15129) {
				driveTrain.drive.arcadeDrive(driveToFeet(15129), turnToAngle(0));
				elevatorPID.setSetpoint(1000); //Change this to what it needs to be
			} else {
				state = 6;
			}
		} else if (state == 6) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 7;
				time = Timer.getFPGATimestamp();	//start a timer so the intake and/or yeet thing can work 
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {		
				intake.startYeet();		//shoots the power cube onto the scale
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (Timer.getFPGATimestamp() < time + 1) { // time will most likely need to be changed or even more likely this whole 'if' condition so the program works properly 
				elevatorPID.setSetpoint(0); //Maybe change this
			} else {
				state = 9;
			}
		}
	}
	public void ds3cL() { //driver station 1 scale right
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 47909) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(47909), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { // if the angle less than 90
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90)); //turn to 90 degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
				if (currentDistance < 52111) {
					driveTrain.drive.arcadeDrive(driveToFeet(52111), turnToAngle(-90));
				} else {
					state = 4;	
				}
			} else if (state == 4) {
				if (currentAngle < -2) {
					driveTrain.drive.arcadeDrive(0, turnToAngle(0));
				} else {
					RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
					state = 5;
				}
			} else if (state == 5) {
				if (currentDistance < 15129) {
					driveTrain.drive.arcadeDrive(driveToFeet(15129), turnToAngle(0));
					elevatorPID.setSetpoint(1000); //Change to actual value
				} else {
					state = 6;
				}
			} else if (state == 6) {
				if (currentAngle > 90-2) {
					driveTrain.drive.arcadeDrive(0, turnToAngle(90));
				} else {
					RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
					state = 7;
					time = Timer.getFPGATimestamp();	//start a timer so the intake and/or yeet thing can work 
				}
			} else if (state == 7) {
				if (Timer.getFPGATimestamp() < time + .4) {		
					intake.startYeet();		//shoots the power cube onto the scale
				} else {
					state = 8;
				}
			} else if (state == 8) {
				if (Timer.getFPGATimestamp() < time + 1) { // time will most likely need to be changed or even more likely this whole 'if' condition so the program works properly 
					elevatorPID.setSetpoint(0); //this might need to change
				} else {
					state = 9;
				}
			}
		}
						
							
				
	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
