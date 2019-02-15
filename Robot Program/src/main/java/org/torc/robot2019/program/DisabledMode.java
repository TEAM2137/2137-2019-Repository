package org.torc.robot2019.program;

import org.torc.robot2019.tools.CommandList;

import edu.wpi.first.wpilibj.command.Scheduler;

public class DisabledMode {
	public static void Init() {
		// Remove all commands
		Scheduler.getInstance().removeAll();
		// Stop all CommandLists
		CommandList.StopAllCommandLists();
	}
	
	public static void Periodic() {

	}
}