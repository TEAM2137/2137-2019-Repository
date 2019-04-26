package org.torc.robot2019.program;

import org.torc.robot2019.tools.CommandList;

import edu.wpi.first.wpilibj.command.Scheduler;

public class DisabledMode {
	public static void Init() {
		//Scheduler.getInstance().removeAll(); // Remove all commands
		CommandList.StopAllCommandLists(); // Stop all CommandLists
		
		// Garuntee that arm doesn't have a set target upon next enable
		RobotMap.S_PivotArm.setPercSpeed(0);
		RobotMap.S_Elevator.deHomeElevator(); // Dehome elevator
		//RobotMap.S_EndEffector.deHomeEndEffector(); // Dehome endeffector
	}
	
	public static void Periodic() {

	}
}