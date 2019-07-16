package org.torc.robot2019.program;

import org.torc.robot2019.tools.CommandList;
import org.torc.robot2019.program.disabledmodecontrols.DisabledModeControl;
import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.Controllers;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.LimelightControl;
import org.torc.robot2019.tools.LimelightControl.LightMode;

import edu.wpi.first.wpilibj.command.Scheduler;

public class DisabledMode {

	private static DisabledModeControl disabledModeCommand; 

	public static void Init() {
		//Scheduler.getInstance().removeAll(); // Remove all commands
		CommandList.StopAllCommandLists(); // Stop all CommandLists
		
		// Garuntee that arm doesn't have a set target upon next enable
		RobotMap.S_PivotArm.setPercSpeed(0);
		RobotMap.S_Elevator.deHomeElevator(); // Dehome elevator
		//RobotMap.S_EndEffector.deHomeEndEffector(); // Dehome endeffector

		// disabledModeCommand = new DisabledModeControl();

		// disabledModeCommand.start();

		//Screw doing it properly, I don't know how disabled works
	}
	
	public static void Periodic() {
		//Here's my bodged code

		if (TORCControls.GetInput(ControllerInput.A_OverrideLEDs, InputState.Pressed) >= 1 ) {
			LimelightControl.setLedMode(LightMode.eOn);
		} else {
			LimelightControl.setLedMode(LightMode.eOff);
		}
	}
}