/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4043.robot;

import edu.wpi.first.wpilibj.Joystick;

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
	public Joystick driveStick = new Joystick(0);
	public Button yeetStart = new JoyStickButton(0, 1);
	public Button intakeStart  = new JoyStickButton(0, 2);
	public Button evelatorUp = new JoystickButton(0, 3);
	public Button evelatorDown = new JoystickButton(0, 4);
	
	public OI() {
		intakeStart.whenPressed(new SuckIn());
		yeetStart.whenPressed(new SpitOut());
	
		evelatorUp.whenPressed(new EvelatorUp());
		evelatorDown.whenPressed(new EvelatorDown());
		
	}
	
	public Joystick getDriveStick() {
		return driveStick;
	}
}
