/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4043.robot;

import org.usfirst.frc.team4043.robot.subsystems.DriveTrain;
import org.usfirst.frc.team4043.robot.subsystems.Evelator;
import org.usfirst.frc.team4043.robot.subsystems.Intake;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.kauailabs.navx.frc.AHRS;

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
	public static OI m_oi;
	public static DriveTrain driveTrain;
	public static Intake intake;
	public static Evelator evelator;
	AHRS ahrs;

	int state = 1;
	String gameData;
	String autoChoice;

	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		driveTrain = new DriveTrain();
		ahrs = new AHRS(SPI.Port.kMXP);
		intake = new Intake();
		evelator = new Evelator();
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
		RobotMap.motorFR.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		//Sets the feedback device as a quad encoder, which is what the cimcoder is
		RobotMap.motorFR.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		boolean cross = SmartDashboard.getBoolean("DB/Button 0", false);
		boolean ds1 = SmartDashboard.getBoolean("DB/Button 1", false);
		boolean ds2 = SmartDashboard.getBoolean("DB/Button 2", false);
		boolean ds3 = SmartDashboard.getBoolean("DB/Button 3", false);
		
		if (cross) {
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
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		
		System.out.println(RobotMap.motorFR.getSelectedSensorPosition(0));
		
		switch (autoChoice) {
		case "ds1L": ds1L();
//		case "ds1R": ds1R();
		case "ds2L": ds2L();
//		case "ds2R": ds2R();
//		case "ds3L": ds3L();
		case "ds3R": ds3R();
		case "ds1cross": ds1cross();
		case "ds3cross": ds3cross();
		case "ds2cross" : ds2cross();
		}
	}
	
	
	public double turnToAngle(double wantedAngle){ //Takes in a wanted angle and returns the turnSpeed to get there
		double currentAngle = ahrs.getAngle(); //In order to determine where we are, take in the current gyro value from the navx
		double rotateSpeed;
		
		if (currentAngle > wantedAngle + 2) { 					//If we are too far to the right of where we want to be...
			rotateSpeed = (wantedAngle - currentAngle) / 20;	//turn left (negative number)
		} else if (currentAngle < wantedAngle - 2) {			//Otherwise, if we are too far left ...
			rotateSpeed = (wantedAngle - currentAngle) / 20;	//turn right (positive number)
		} else {												//If we are right on track ...
			rotateSpeed = 0d;									//don't rotate
		}
		
		//Just sanity checks for our output. Turning for arcade drive has to be between -1 and 1
		if (rotateSpeed > 1) {												
			rotateSpeed = 1;
		} else if (rotateSpeed < -1) {
			rotateSpeed = -1;
		} else if (rotateSpeed < .1 && rotateSpeed > 0) {		//Checks for a value small enough that it won't turn the robot
			rotateSpeed = .1;
		} else if (rotateSpeed > -.1 && rotateSpeed < 0) {		//Checks for a value small enough that it won't turn the robot
			rotateSpeed = -.1;
		}
		
		return rotateSpeed;
	}
	
	public double driveToFeet(double wantedDistance) { 
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance < wantedDistance) {
			driveSpeed = (wantedDistance - currentDistance) / 50;
		} else {
			driveSpeed = 0d;
		}
		
		if (driveSpeed > 1) {
			driveSpeed = 1;
		} else if (driveSpeed < .1 && driveSpeed > 0) {
			driveSpeed = .1d;
		} else if (driveSpeed < -1) {
			driveSpeed = -1;
		} else if (driveSpeed > -.1 && driveSpeed < 0) {
			driveSpeed = -.1d;
		}
		
		return driveSpeed;
	}
	
	public double backToFeet(double wantedDistance) {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance > wantedDistance) {
			driveSpeed = (wantedDistance - currentDistance) / 50;
		} else {
			driveSpeed = 0d;
		}
		
		if (driveSpeed > 1) {
			driveSpeed = 1;
		} else if (driveSpeed < .1 && driveSpeed > 0) {
			driveSpeed = .1d;
		} else if (driveSpeed < -1) {
			driveSpeed = -1;
		} else if (driveSpeed > -.1 && driveSpeed < 0) {
			driveSpeed = -.1d;
		}
		
		return driveSpeed;
	}
	
	public void ds1L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 60/12) {
				driveTrain.drive.arcadeDrive(driveToFeet (60/12), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 45-2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(45));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < (Math.sqrt(2)*40)/12);
				driveTrain.drive.arcadeDrive(driveToFeet((Math.sqrt(2)*40)/12), turnToAngle(45));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle > 2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 40/12) {
				driveTrain.drive.arcadeDrive (driveToFeet (40/12), turnToAngle (0));
				evelator.elevatorUP();
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
			if (currentDistance > 96 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(96 / 12), turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 9;
		}
	}
	public void ds3R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 60/12) {
				driveTrain.drive.arcadeDrive(driveToFeet (60/12), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 45-2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(45));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < (Math.sqrt(2)*40)/12);
				driveTrain.drive.arcadeDrive(driveToFeet((Math.sqrt(2)*40)/12), turnToAngle(45));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle < -2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 40/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(40/12), turnToAngle(0));
				evelator.elevatorUP();
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
			if (currentDistance > 96 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(96/ 12), turnToAngle(0));
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
		double time = 0;
	
	//stage one we move 70/12 forward from DS2
		if (state == 1) {
			if (currentDistance < 70 /12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
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
			if (currentDistance >  78/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(72/12), turnToAngle(-90));
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
			if (currentDistance < 70/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
				evelator.elevatorUP();
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		}
		 
		else if (state == 6) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 7;
			}
		}
		else if (state == 7) { 
			if (currentDistance > 60/12) {
				driveTrain.drive.arcadeDrive(backToFeet(60/12), turnToAngle(0));
			} else {
				state = 8;
			}			
		}
		else if (state == 8) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 9;
			}
		}
	}
	
	public void ds1cross() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();

		if (state == 1) {
			if (currentDistance < 24 / 12) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(24/12), turnToAngle(0)); //drive 2 feet forward
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
				driveTrain.drive.arcadeDrive(driveToFeet(60/12), turnToAngle(-30)); // move 3 feet
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
			if (currentDistance < 180/12) { //if the robot isn't in the null territory
				driveTrain.drive.arcadeDrive(driveToFeet(180/12), turnToAngle(0)); //drive 20 feet forward
			} else {
				state = 6;
			}
		}
	}
	
	public void ds3cross() {
 	   double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
	   double currentAngle = ahrs.getAngle();
        
 	   if (state == 1) {
 		   if (currentDistance < 100 /12) {
 			   driveTrain.drive.arcadeDrive(driveToFeet(100/12), turnToAngle(0));
 		   }
 		   else{
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
 		   if (currentDistance < 62.5/12) {
 			   driveTrain.drive.arcadeDrive(driveToFeet(62.5/12) , turnToAngle(51.3));
 		   } else {
 			   state = 4;
 		   }
 	   } else if ( state == 4 ) {
 		   if ( currentAngle > 0 ) {
 			   driveTrain.drive.arcadeDrive(0, turnToAngle(0));
 		   } else {
			   RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
 			   state = 5;
 		   }
 	   } else if (state == 5) {
 		   if (currentDistance < 132/12) {
 			   driveTrain.drive.arcadeDrive(driveToFeet(132/12), turnToAngle(0));
 		   } else {
 			   state = 6;
 		   }
 	   }
	}

	public void ds2cross() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
	        
	 	if (state == 1) {
	 		if (currentDistance < 24 / 12) {
	 			driveTrain.drive.arcadeDrive(driveToFeet(24 / 12), turnToAngle(0));
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
			if (currentDistance < 180/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(180/12), turnToAngle(30));
			} else {
				state = 4;
			
			}
	 	} else if (state == 5) {
	 		if (currentDistance > 144 / 12) {
	 			driveTrain.drive.arcadeDrive(backToFeet(144/12), turnToAngle(0));
	 		} else {
	 			state = 6;
	 		}
	 	}
	}
	
	public void ds2R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
	
	//stage one we move 70/12 degrees forward from DS2
		if (state == 1) {
			if (currentDistance < 70 /12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
			} else {
				state = 2;
			}
		}
			//second stage begins, we are turning 90 degrees 
		else if (state == 2) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90)); 
			} else {
				state = 3;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
		}
		
		else if (state == 3) {
			if (currentDistance >  78/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(78/12), turnToAngle(90));
				//zoom zoom
			} else {
				state = 4;
			}					
		}
			
		else if (state==4) {
			if (currentAngle > 2) {
			driveTrain.drive.arcadeDrive(0,turnToAngle(0));
			} else {
				state = 5;
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
			}
		}
			
		else if (state == 5) {
			if (currentDistance < 70/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
				evelator.elevatorUP();
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
			}
		}
		 
		else if (state == 6) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 7;
			}
		}
		else if (state == 7) { 
			if (currentDistance > 60/12) {
				driveTrain.drive.arcadeDrive(backToFeet(60/12), turnToAngle(0));
			} else {
				state = 8;
			}			
		}
		else if (state == 8) {
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
		}
	}
	
	public void ds1cL() { //driver station 1 scale left
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 1) {
			if (currentDistance < 240 / 12) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(240/12), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle < 68-2) { //if the angle more than
				driveTrain.drive.arcadeDrive(0, turnToAngle(68)); //turn to  degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 64/12) { //if the robot has moved less than  feet
				driveTrain.drive.arcadeDrive(driveToFeet(64/12), turnToAngle(68));// move  feet
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if (currentAngle > 2) { //if the angle is  than 0
				driveTrain.drive.arcadeDrive(0, turnToAngle(0)); //turn to 0 degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		
	public void ds3cR() { //driver station 3 scale right
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 1) {
			if (currentDistance < 240 / 12) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(24/12), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle > -68 + 2){ //if the angle  than 
				driveTrain.drive.arcadeDrive(0, turnToAngle(68)); //turn to  degrees
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 64/12){ //if the robot has moved less than  feet
				driveTrain.drive.arcadeDrive(driveToFeet(64/12), turnToAngle(-68)); // move  feet
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
	
	public void ds1R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 24/12) {
				driveTrain.drive.arcadeDrive(driveToFeet (24/12), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 70 - 2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(70));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 198/12);
				driveTrain.drive.arcadeDrive(driveToFeet(198/12), turnToAngle(70));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle > 2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 48/12) {
				driveTrain.drive.arcadeDrive (driveToFeet (48/12), turnToAngle (0));
				evelator.elevatorUP();
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
			if (currentDistance > 120 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(120 / 12), turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentAngle < 90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
			
	public void ds3L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 24/12) {
				driveTrain.drive.arcadeDrive(driveToFeet (24/12), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle > -70 + 2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(-70));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 198/12);
				driveTrain.drive.arcadeDrive(driveToFeet(198/12), turnToAngle(-70));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if(currentAngle < -2);
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				RobotMap.motorFR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 48/12) {
				driveTrain.drive.arcadeDrive (driveToFeet (48/12), turnToAngle (0));
				evelator.elevatorUP();
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
			if (currentDistance > 120 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(120 / 12), turnToAngle(0));
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 9;
			}
	
	public void ds1cR() { //driver station 1 scale right
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		doulbe currentAngle = ahrs.getAngle();
				
		if(state == 1) {
			if (currentDistance < 228 /12) //if the robot hasn't moved forward
			driveTrain.drive.arcadeDrive(driveToFeet(288 / 12), turnToAngle(0)); //drive 2 feet forward
		} else {
			state = 2;
			}
		} else if (state == 2) { // if the angle less than 90
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90)); //turn to 90 degrees
			} else {
				RobotMap.motorFR.setSelectedPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 248/12);
				driveTrian.drive.arcadeDrive(driveToFeet(248/12)); turnToAngle(90));
			} else {
				state = 4	
			}
		} else if (state == 4) {
			if (currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				robotMap.motorFR.setSelectedPostion(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 72/12);
				driveTrain.drive.arcadeDrive(driveToFeet(72/12)); turnToAngle(0));
			} else {
				state = 6;
				}
		} else if (state ==6) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				robotMap.motorFR.setSelectedPostion(0, 0, 10);
				state = 7;

		public void ds3cL() { //driver station 1 scale right
			double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0)
			doulbe currentAngle = ahrs.getAngle();

		if(state == 1) {
			if (currentDistance < 228 /12 ) //if the robot hasn't moved forward
			driveTrain.drive.arcadeDrive(driveToFeet(288 / 12), turnToAngle(0)); //drive 2 feet forward
		} else {
			state = 2;
			}
		} else if (state == 2) { // if the angle less than 90
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90)); //turn to 90 degrees
			} else {
				RobotMap.motorFR.setSelectedPosition(0, 0, 10);
				state = 3;
		}
		} else if (state == 3) {
			if (currentDistance < 248/12);
				driveTrian.drive.arcadeDrive(driveToFeet(248/12)); turnToAngle(-90));
			} else {
				state = 4	
			}
		} else if (state == 4) {
			if (currentAngle < -2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				robotMap.motorFR.setSelectedPostion(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 72/12);
				driveTrain.drive.arcadeDrive(driveToFeet(72/12)); turnToAngle(0));
			} else {
				state = 6;
				}
		} else if (state ==6) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				robotMap.motorFR.setSelectedPostion(0, 0, 10);
				state = 7;
						
							
				
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
