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
	public static SendableChooser<Boolean> driveTypeChooser = new SendableChooser<Boolean>();
	public static SendableChooser<Boolean> luccaModeChooser = new SendableChooser<Boolean>();
	
	public static double initTime = 0;
	int state = 1;
	String gameData;
	String autoChoice = "";
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
		
		SmartDashboard.putData("Auto mode", m_chooser);				// Old dashboard attempt? Can this be disabled?
		SmartDashboard.putBoolean("Lucca Mode", luccaMode);			// adding all the fancy dashboard stuff
		SmartDashboard.putNumber("Lucca Speed Value", luccaSpeed);
		SmartDashboard.putString("Drive Type", driveTypeString);
		SmartDashboard.putData("Lucca Mode Chooser", luccaModeChooser);
		SmartDashboard.putData("Drive Type Selector", driveTypeChooser);
		luccaModeChooser.addDefault("Lucca Mode: Off", false);
		luccaModeChooser.addObject("Lucca Mode: On", true);
		driveTypeChooser.addDefault("Drive Type: Arcade Drive", true);
		driveTypeChooser.addObject("Drive Type: Tank Drive", false);
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	
	@Override
	public void disabledInit() {
		updateDriveType();
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		
		updateDriveType();
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
	
	public double turnToAngle(double wantedAngle){ //Takes in a wanted angle and returns the turnSpeed to get there
		double currentAngle = ahrs.getAngle(); //In order to determine where we are, take in the current gyro value from the navx
		double rotateSpeed;
		
		if (currentAngle > wantedAngle + 2) { 					//If we are too far to the right of where we want to be...
			rotateSpeed = -.7d;	//turn left (negative number)
		} else if (currentAngle < wantedAngle - 2) {			//Otherwise, if we are too far left ...
			rotateSpeed = .7d;	//turn right (positive number)
		} else {												//If we are right on track ...
			rotateSpeed = 0d;									//don't rotate
		}
		
		return rotateSpeed;
	}
	
	public double driveToFeet(double wantedDistance) { 
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance < wantedDistance - 300) {
			driveSpeed = 0.7d;
		} else {
			driveSpeed = 0d;
		}
		
		return driveSpeed;
	}
	
	public double backToFeet(double wantedDistance) {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance > wantedDistance) {
			driveSpeed = -0.7d;
		} else {
			driveSpeed = 0d;
		}
		
		return driveSpeed;
	}
	
	boolean driveTypeNew;
	void updateDriveType() {
		luccaMode = luccaModeChooser.getSelected();
		SmartDashboard.putBoolean("Lucca Mode", luccaMode);
		luccaSpeed = SmartDashboard.getNumber("Lucca Speed Value", 100);
		driveTypeNew = driveTypeChooser.getSelected();
		if (driveType != driveTypeNew) {
			driveType = driveTypeNew;
			if (driveType) {
				driveTypeString = "Arcade Drive";
			} else {
				driveTypeString = "Tank Drive";
			}
			SmartDashboard.putString("Drive Type", driveTypeString);
			SmartDashboard.updateValues();
		}	
	}
	
	public void autoTest() {
		//double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (currentAngle < 30) {
			Robot.driveTrain.drive.arcadeDrive(0, turnToAngle(30));
			System.out.println(ahrs.getAngle());
		}
		
//		if (currentAngle < 90) {
//			System.out.println("turning");
//			Robot.driveTrain.drive.arcadeDrive(0, 1);
//		}
		
		//System.out.println(ahrs.getAngle());
	}
	
	public void ds2L() {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
	
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 6000) {
				driveTrain.drive.arcadeDrive(driveToFeet(6000), turnToAngle(0));
				System.out.println("state 1");
			} else {
				state = 2;
				System.out.println("Start state 2");
			}
		}
			//second stage begins, we are turning -90 degrees 
		else if (state == 2) {
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90)); 
				System.out.println("state 2");
			} else {
				state = 3;
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 0);
				
			}
		}
		
		else if (state == 3) {
			if (currentDistance < 7000) {
				driveTrain.drive.arcadeDrive(driveToFeet(7000), turnToAngle(-90));
			} else {
				state = 4;
			}					
		}
			
		else if (state==4) {
			if (currentAngle < -2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(0));
			} else {
				state = 5;
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 0);
			}
		}
			
		else if (state == 5) {
			if (currentDistance < 6000) {
				driveTrain.drive.arcadeDrive(driveToFeet(6000), turnToAngle(0));
				elevator.elevatorMove(-.6f);
			} else {
				state = 7;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + 2) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 8;
			}
		}
	}
	
	public void ds2R() {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
	
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 5000) {
				driveTrain.drive.arcadeDrive(driveToFeet(5000), turnToAngle(0));
			} else {
				state = 2;
			}
		
		} else if (state == 2) {
			if (currentAngle < 90-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90)); 
			} else {
				state = 3;
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
			}
			
		} else if (state == 3) {
			if (currentDistance > 5000) {
				driveTrain.drive.arcadeDrive(driveToFeet(5000), turnToAngle(90));
				//zoom zoom
			} else {
				state = 4;
			}
			
		} else if (state == 4) {
			if (currentAngle > 2) {
				driveTrain.drive.arcadeDrive(0,turnToAngle(0));
			} else {
				state = 5;
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
			}	
			
		} else if (state == 5) {
			if (currentDistance < 5000) {
				driveTrain.drive.arcadeDrive(driveToFeet(5000), turnToAngle(0));
				//elevatorPID.setSetpoint(300);
				elevator.elevatorMove(.6f);
			} else {
				state = 7;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 7) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 8;
			}
		
		} else if (state == 8) { 
			if (currentDistance > 5000) {
				driveTrain.drive.arcadeDrive(backToFeet(5000), turnToAngle(0));
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
	
	//boolean cross;
	//boolean ds1;
	//boolean ds2;
	//boolean ds3;
	
	public static boolean driveType;
	public static double luccaSpeed = 100;
	public static boolean luccaMode;
	
	String driveTypeString = "Not Set";
	
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
		RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
		
		RobotMap.evelator.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		RobotMap.evelator.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		RobotMap.evelator.setSelectedSensorPosition(0, 0, 0);
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		//cross = SmartDashboard.getBoolean("DB/Button 0", false);
		//ds1 = SmartDashboard.getBoolean("DB/Button 1", false);
		//ds2 = SmartDashboard.getBoolean("DB/Button 2", false);
		//ds3 = SmartDashboard.getBoolean("DB/Button 3", false);
		//double dashData = SmartDashboard.getNumber("DB/Slider 0", 0.0);
		
		initTime = Timer.getFPGATimestamp();
		
		time = Timer.getFPGATimestamp();
		ahrs.reset();
		
		state = 1;
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	
	@Override
	public void autonomousPeriodic() {		
		Scheduler.getInstance().run();
		
		updateDriveType();
		
		if (Timer.getFPGATimestamp() < initTime + 11 && Timer.getFPGATimestamp() > initTime + 4){
			RobotMap.armVert.set(1);
		} else {
			RobotMap.armVert.stopMotor();
		}
		
		cross();
		
		System.out.println(ahrs.getAngle());
		System.out.println(RobotMap.motorBR.getSelectedSensorPosition(0));
		
//		if (cross) {
//			System.out.println("cross");
//			cross();
//		} else if (ds2) {
//			System.out.println("ds2L");
//			ds2L();
//		} else if (ds1) {
//			System.out.println("ds1L");
//			ds1L();
//		} else if (ds3) {
//			System.out.println("ds3R");
//			ds3R();
//		}
	}
	
	public void ds1L() {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 10000) {
				driveTrain.drive.arcadeDrive(driveToFeet (10000), turnToAngle (0));
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if(currentAngle < 45-2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(45));
			} else {
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
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
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 8405) {
				driveTrain.drive.arcadeDrive(driveToFeet(8405), turnToAngle(0));
				elevatorPID.setSetpoint(300);
			} else {
				state = 7;
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
			if (currentAngle > -90+2) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 10;
			}
		}
	}
	
	public void ds3R() {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
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
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
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
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
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
	
	public void cross() {
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);

		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 25043) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(25043), turnToAngle(0)); //drive 12ish feet forward
			} else {
				state = 2;
			}
		}
	}
	
	public void ds1cL() { //driver station 1 scale left
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 21428) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(21428), turnToAngle(0)); //drive 25 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) {
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 0);
			} else {
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance > -852) {
				driveTrain.drive.arcadeDrive(backToFeet(-852), turnToAngle(90));
			} else {
				state = 4;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 4) {
			if (Timer.getFPGATimestamp() < time + .5) {
				elevator.elevatorMove(-.7f);
			} else {
				state = 5;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 5) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 6;
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
			}
		} else if (state == 6) {
			if (currentDistance > -852) {
				driveTrain.drive.arcadeDrive(backToFeet(-852), turnToAngle(90));
				elevator.elevatorMove(.4f);
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (currentAngle > 0) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(180));
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
			} else {
				state = 8;
			}
		} else if (state == 8) {
			if (currentDistance < 3195) {
				driveTrain.drive.arcadeDrive(driveToFeet(3195), turnToAngle(180));
			} else {
				state = 9;
			}
		}
	}
		
	public void ds3cR() { //driver station 3 scale right
		double currentDistance = RobotMap.motorBR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		
		if (state == 0) {
			if (Timer.getFPGATimestamp() < time + 3) {
			} else {
				state = 1;
			}
		} else if (state == 1) {
			if (currentDistance < 21428) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(21428), turnToAngle(0)); //drive 25 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle > -68 + 2){ //if the angle  than 
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90)); //turn to  degrees
			} else {
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance > -852){ //if the robot has moved less than  feet
				driveTrain.drive.arcadeDrive(backToFeet(-852), turnToAngle(-90)); // move  feet
			} else {
				state = 4;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 4) {
			if (Timer.getFPGATimestamp() < time + .5) {
				elevator.elevatorMove(-.7f);
			} else {
				state = 5;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 5) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 6;
				time = Timer.getFPGATimestamp();
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
			}
		} else if (state == 6) {
			if (currentDistance > -852) {
				driveTrain.drive.arcadeDrive(backToFeet(-852), turnToAngle(-90));
			} else {
				state = 7;
			}
		} else if (state == 7) {
			if (currentAngle < -2){ //if the angle is  than 0
				driveTrain.drive.arcadeDrive(0, turnToAngle(180)); //turn to 0 degrees
			} else {
				RobotMap.motorBR.setSelectedSensorPosition(0, 0, 10);
				state = 8;
			}
		} else if (state == 8) {
			if (currentDistance < 3195) {
				Robot.driveTrain.drive.arcadeDrive(driveToFeet(3195), turnToAngle(180));
			} else {
				state = 8;
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
		
		RobotMap.armVert.set(0);
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		
		updateDriveType();
		
		if (Robot.m_oi.coStick.getRawAxis(3) > .1) {
    		//Move elevator up at speed of right trigger
    		Robot.elevator.elevatorMove(-Robot.m_oi.coStick.getRawAxis(3));
    	} else if (Robot.m_oi.coStick.getRawAxis(2) > .1) {
    		//if the left trigger is above deadband, run the elevator down at left trigger speed
    		Robot.elevator.elevatorMove(Robot.m_oi.coStick.getRawAxis(2));
    	} else {
    		//otherwise, stop elevator
    		Robot.elevator.elevatorMove(0);
    	}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
