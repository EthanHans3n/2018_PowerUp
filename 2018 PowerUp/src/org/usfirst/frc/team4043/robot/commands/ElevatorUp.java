package org.usfirst.frc.team4043.robot.commands;

import org.usfirst.frc.team4043.robot.OI;
import org.usfirst.frc.team4043.robot.Robot;
import org.usfirst.frc.team4043.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ElevatorUp extends Command {

    public ElevatorUp() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	float axis = (float)OI.getCoStick().getRawAxis(5);
    	if (axis > 0.1f){
    		Robot.elevator.elevatorUp(axis);
    		Robot.elevatorPID.disable();
    	}
    	else if (axis < -0.3f) {
    		Robot.elevator.elevatorUp(axis);
    		Robot.elevatorPID.disable();
    	}
    	else {
    		Robot.elevatorPID.enable();
    		Robot.elevatorPID.setSetpoint(RobotMap.evelator.getSelectedSensorPosition(0));
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
