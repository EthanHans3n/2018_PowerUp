package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Intake extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void startSuck(){
    	RobotMap.intakeL.set(-.6);
    	RobotMap.intakeR.set(.6);
    }
    
    public void startYeet(){
    	RobotMap.intakeL.set(1);
    	RobotMap.intakeR.set(-1);
    }
    
    public void keepCube() {
    	RobotMap.intakeL.set(-.5);
        RobotMap.intakeR.set(.5);
    }
    
    public void armsDown() {
    	RobotMap.armVert.set(-1);
    }
    
    public void armsUp() {
    	RobotMap.armVert.set(1);
    }
    
    public void armsStop() {
    	RobotMap.armVert.set(0);;
    }
    
    public void stopAll() {
    	RobotMap.intakeL.set(0);
    	RobotMap.intakeR.set(0);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

