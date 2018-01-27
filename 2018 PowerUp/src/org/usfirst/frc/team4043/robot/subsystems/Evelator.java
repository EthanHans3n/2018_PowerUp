package org.usfirst.frc.team4043.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Evelator extends Subsystem {
  
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void elevatorUP() {
      RobotMap.evelator.set(0.3);   
    }
  
    public void elevatorDOWN() {
      RobotMap.evelator.set(-0.25);
    }
    
    public void initDefaultCommand() {
      
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

