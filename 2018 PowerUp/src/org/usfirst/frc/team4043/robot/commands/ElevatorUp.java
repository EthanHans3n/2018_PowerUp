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
    	requires(Robot.elevator);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
//    	float axisUp = (float)OI.getCoStick().getRawAxis(3);
//    	float axisDown = (float)OI.getCoStick().getRawAxis(2);
//    	System.out.println(axisUp);
//    	if(axisUp > axisDown) {
//    		if (axisUp > 0.1f){
//        		Robot.elevator.elevatorMove(axisUp);
//        		Robot.elevatorPID.disable();
//        	}
//        	else if (axisUp < -0.3f) {
//        		Robot.elevator.elevatorMove(axisUp);
//        		Robot.elevatorPID.disable();
//        	}
////        	else {
////        		Robot.elevatorPID.enable();
////        		Robot.elevatorPID.setSetpoint(RobotMap.evelator.getSelectedSensorPosition(0));
////        	}
//    	} else {
//    		if (axisDown > 0.1f){
//        		Robot.elevator.elevatorMove(-axisDown);
//        		Robot.elevatorPID.disable();
//        	}
//        	else if (axisDown < -0.3f) {
//        		Robot.elevator.elevatorMove(-axisDown);
//        		Robot.elevatorPID.disable();
//        	}
////        	else {
////        		Robot.elevatorPID.enable();
////        		Robot.elevatorPID.setSetpoint(RobotMap.evelator.getSelectedSensorPosition(0));
////        	}
//    	}
    	Robot.elevator.elevatorMove(-.7f);
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
