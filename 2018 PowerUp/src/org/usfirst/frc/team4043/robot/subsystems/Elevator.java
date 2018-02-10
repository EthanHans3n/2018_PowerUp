package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.OI;
import org.usfirst.frc.team4043.robot.Robot;
import org.usfirst.frc.team4043.robot.RobotMap;
import org.usfirst.frc.team4043.robot.commands.ElevatorUp;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Elevator extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public void elevatorUp(float axis) {
		RobotMap.evelator.set(axis);
	}
	
	public void elevatorStop() {
		RobotMap.evelator.set(0);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new ElevatorUp());
    }
}

