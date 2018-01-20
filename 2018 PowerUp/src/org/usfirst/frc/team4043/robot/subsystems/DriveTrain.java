package org.usfirst.frc.team4043.robot.subsystems;

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
	
	public DriveTrain() {
		super();
		drive = new DifferentialDrive(RobotMap.motorFL, RobotMap.motorFR);
    	
    	RobotMap.motorBL.follow(RobotMap.motorFL);
    	RobotMap.motorBR.follow(RobotMap.motorFR);
	}
	
	public void drive(double throttle, double turn) {
		drive.arcadeDrive(throttle, turn);
	}
	
	public void drive(Joystick joy) {
		inputSpeed = -joy.getRawAxis(1);
		inputTurn = -joy.getRawAxis(4);
		
		//inputTurn = -joy.getRawAxis(5);	//For tank drive
		
		drive(inputSpeed, -inputTurn);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new Drive());
    }
}

