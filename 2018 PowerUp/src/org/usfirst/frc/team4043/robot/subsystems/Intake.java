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
     RobotMap.intakeFL.set(.5);
     RobotMap.intakeFR.set(-.5);
     RobotMap.intakeBL.set(.5);
     RobotMap.intakeBR.set(-.5);
    }
    public void startYeet(){
     RobotMap.intakeFL.set(-.5);
     RobotMap.intakeFR.set(.5);
     RobotMap.intakeBL.set(-.5);
     RobotMap.intakeBR.set(.5);
    }
    


    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

