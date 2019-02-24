package org.torc.robot2019.program;

import org.torc.robot2019.program.teleopcontrols.TeleopDrive;

public class TeleopMode {
	
	private static TeleopDrive driveCommand;

	public static void Init() {
		System.out.println("Teleop Mode Enabled!!");
		RobotMap.S_Elevator.homeElevator();
		// Initialize Teleop Drive Command
		driveCommand = new TeleopDrive(RobotMap.S_DriveTrain, RobotMap.S_PivotArm, RobotMap.S_Climber,
			RobotMap.S_Elevator);
		driveCommand.start();
	}
	
	public static void Periodic() {

	}
}
