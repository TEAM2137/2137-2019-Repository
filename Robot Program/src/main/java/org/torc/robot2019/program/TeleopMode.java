package org.torc.robot2019.program;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.teleopcontrols.TeleopDrive;
import org.torc.robot2019.subsystems.EndEffector.SolenoidStates;
import org.torc.robot2019.tools.LimelightControl;

public class TeleopMode {
	
	private static TeleopDrive driveCommand;

	public static void Init() {
		System.out.println("Teleop Mode Enabled!!");
		System.out.println("RobotType: " + KMap.GetRobotType().toString());

		// Reset limelight NT instance
		//LimelightControl.resetTableInstance();

		// Home elevator
		RobotMap.S_Elevator.homeElevator();
		
		// Keep Pivot arm at init position
		RobotMap.S_PivotArm.setPosition(RobotMap.S_PivotArm.getEncoder());
		// Keep End Effector at init position
		RobotMap.S_EndEffector.setPosition(RobotMap.S_EndEffector.getEncoder());
		// Default solenoid to open
		RobotMap.S_EndEffector.setSolenoid(SolenoidStates.Open);

		// Initialize Teleop Drive Command
		driveCommand = new TeleopDrive(RobotMap.S_DriveTrain, RobotMap.S_GPManager, 
			RobotMap.S_PivotArm, RobotMap.S_Climber, RobotMap.S_Elevator, RobotMap.S_EndEffector,
			RobotMap.S_ElevatorArmManager);
		driveCommand.start();
	}
	
	public static void Periodic() {

	}
}

