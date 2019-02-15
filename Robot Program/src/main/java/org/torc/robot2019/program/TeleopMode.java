package org.torc.robot2019.program;

import org.torc.robot2019.program.RobotInfo.RobotType;
import org.torc.robot2019.program.teleopcontrols.TeleopDrive;

public class TeleopMode {
	
	private static TeleopDrive driveCommand;

	public static void Init() {
		System.out.println("Teleop Mode Enabled!!");
		driveCommand = new TeleopDrive(RobotMap.DriveTrain);
		driveCommand.start();
		//System.out.println("Is Practice Bot: " + (RobotMap.RobInfo.getRobotType() == RobotType.Practice));
	}
	
	public static void Periodic() {

	}
}

