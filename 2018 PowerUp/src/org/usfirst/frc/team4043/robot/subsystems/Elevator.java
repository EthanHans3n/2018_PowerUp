package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.Robot;
import org.usfirst.frc.team4043.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Elevator extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public void elevatorMove(double axis) {
		Robot.elevatorPID.disable();
//		if (axis < -.6) {
//			RobotMap.evelator.set(-.3);
//			RobotMap.evelator2.set(-.3);
//		} else if (axis < .6) {
//			RobotMap.evelator.set(axis);
//			RobotMap.evelator2.set(axis);
//		} else {
//			RobotMap.evelator.set(.3);
//			RobotMap.evelator2.set(.3);
//		}
		
		RobotMap.evelator.set(axis);
		RobotMap.evelator2.set(axis);
		
//		RobotMap.evelator.set(axis * .6);
//		RobotMap.evelator2.set(axis * .6);
		
	}
	
	public void elevatorStop() {
		RobotMap.evelator.set(0);
		RobotMap.evelator2.set(0);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	//setDefaultCommand(new ElevatorUp());
    }
}

