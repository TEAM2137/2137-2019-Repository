package org.torc.robot2019.program;

import org.torc.robot2019.tools.CommandList;
import org.torc.robot2019.program.disabledmodecontrols.DisabledModeControl;

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

		disabledModeCommand = new DisabledModeControl();

		disabledModeCommand.start();
	}
	
	public static void Periodic() {

	}
}