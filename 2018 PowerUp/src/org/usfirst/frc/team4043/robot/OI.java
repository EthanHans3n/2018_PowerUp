/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4043.robot;

import org.usfirst.frc.team4043.robot.commands.ElevatorDown;
import org.usfirst.frc.team4043.robot.commands.ElevatorStop;
import org.usfirst.frc.team4043.robot.commands.ElevatorUp;
import org.usfirst.frc.team4043.robot.commands.OperationKeapDaKewb;
import org.usfirst.frc.team4043.robot.commands.Shift;
import org.usfirst.frc.team4043.robot.commands.SpitOut;
import org.usfirst.frc.team4043.robot.commands.StopIntake;
import org.usfirst.frc.team4043.robot.commands.SuckIn;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	//Zoe was here 
	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	//Zoe was here
	//El bandito was also here as too
	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	public Joystick driveStick = new Joystick(5);
	public static Joystick coStick = new Joystick(1);
	public Button yeetStart = new JoystickButton(driveStick, 6);
	public Button intakeStart  = new JoystickButton(driveStick, 5);
	public Button evelatorUp = new JoystickButton(coStick, 4);
	public Button evelatorDown = new JoystickButton(coStick, 1);
	public Button elevatorSwitch = new JoystickButton(coStick, 2);
	public Button keepCube = new JoystickButton(driveStick, 1);
	public Button shifter = new JoystickButton(driveStick, 2);
	
	//public Trigger elevatorTrigUp = new JoystickButton(driveStick, 3);
	//public Trigger elevatorTrigDown = new JoystickButton(driveStick, 2);
	
	public OI() {
		intakeStart.whileHeld(new SuckIn());
		yeetStart.whileHeld(new SpitOut());
		
		intakeStart.whenReleased(new StopIntake());
		yeetStart.whenReleased(new StopIntake());
		
		if (driveStick.getRawAxis(3) > .1) {
			new ElevatorUp(driveStick.getRawAxis(3));
		} else if (driveStick.getRawAxis(2) > .1) {
			new ElevatorDown(driveStick.getRawAxis(2));
		}
		
		evelatorUp.whenReleased(new ElevatorStop());
		evelatorDown.whenReleased(new ElevatorStop());
		
		keepCube.whenPressed(new OperationKeapDaKewb());
		shifter.toggleWhenPressed(new Shift());
	}
	
	public Joystick getDriveStick() {
		return driveStick;
	}
	
	public static Joystick getCoStick() {
		return coStick;
	}
}
