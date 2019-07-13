/*
package org.torc.robot2019.commands;

import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.ControllerExtra;

import edu.wpi.first.wpilibj.GenericHID;

public class ControllerRumble extends CLCommand {

	private GenericHID controller;
	
	private double rumbleTime;
	private double rumbleVal;
	
	private int timeCount = 0;
	
	/**
	 * @param cont
	 * @param time
	 * Amount of time (in seconds) for the controller to rumble.
	 *//*
	public ControllerRumble(GenericHID _controller, double _value, double _time) {
		controller = _controller;
		rumbleTime = _time;
		rumbleVal = _value;
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		ControllerExtra.SetDualRumble(controller, rumbleVal);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		if ( (timeCount / 50) >= rumbleTime ) {
			ControllerExtra.SetDualRumble(controller, 0);
			CLCommandDone = true; // Mark Command as done.
		}
		timeCount++;
	}
    
}
*/