package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.Robot;
import org.usfirst.frc.team4043.robot.RobotMap;
import org.usfirst.frc.team4043.robot.commands.Drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 *
 */
public class DriveTrain extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public DifferentialDrive drive;
	double inputSpeed;
	double inputTurn;
	boolean flase = false;
	
	public DriveTrain() {
		super();
		drive = new DifferentialDrive(RobotMap.motorFL, RobotMap.motorFR);
    	
    	RobotMap.motorBL.follow(RobotMap.motorFL);
    	RobotMap.motorBR.follow(RobotMap.motorFR);
    	
    	RobotMap.motorFR.setSafetyEnabled(false);
    	RobotMap.motorBR.setSafetyEnabled(false);
    	RobotMap.motorFL.setSafetyEnabled(false);
    	RobotMap.motorBL.setSafetyEnabled(false);
    	drive.setSafetyEnabled(flase);
	}
	
	public void drive(double left, double right) {
		if (Robot.driveType) {
			drive.arcadeDrive(left, right);
		} else {
			drive.tankDrive(left, right);
		}
	}
	
	public void drive(Joystick joy) {
		if (Robot.driveType) {				//for arcade
			inputSpeed = -joy.getRawAxis(1);
			inputTurn = -joy.getRawAxis(4);
		} else {							//for tank
			inputSpeed = -joy.getRawAxis(1);
			inputTurn = joy.getRawAxis(5);
		}
		drive(inputSpeed, -inputTurn);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new Drive());
    }
}

