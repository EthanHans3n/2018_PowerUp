package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Shifter extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public void shift() {
		RobotMap.shifter.set(true);
	}
	
	public void unShift() {
		RobotMap.shifter.set(false);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

