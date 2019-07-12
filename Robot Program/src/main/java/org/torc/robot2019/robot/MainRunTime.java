package org.torc.robot2019.robot;

import java.util.ArrayList;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;
import org.torc.robot2019.program.RobotMode;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

@RunTime(name = "Main Run Time")
public class MainRunTime extends OpMode {

	Command autonomousCommand;
	
	private static ArrayList<InheritedPeriodic> PeriodicList = new ArrayList<InheritedPeriodic>();

	public static void AddToPeriodic(InheritedPeriodic toAdd) {
		PeriodicList.add(toAdd);
    }
    
    @Override
    public void INIT() {
		RobotMode.Init();
    }

    @Override
    public void LOOP() {
		// Manually call the Scheduler "run" command.
		Scheduler.getInstance().run();
		// Call all perodic funtions in PeriodicList
		for(InheritedPeriodic per : PeriodicList) {
			per.Periodic();
		}
		RobotMode.Periodic();
    }
}
