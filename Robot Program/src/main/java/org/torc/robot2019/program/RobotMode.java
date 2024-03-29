package org.torc.robot2019.program;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.commands.VisionCorrector;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.Pneumatics;
import org.torc.robot2019.subsystems.RPiCameras;
import org.torc.robot2019.subsystems.RioCameras;
import org.torc.robot2019.subsystems.RPiCameras.CameraSelect;
//import org.torc.robot2019.subsystems.RioCameras.CameraSelect;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager;
import org.torc.robot2019.vision.VisionManager;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import org.torc.robot2019.robot.subsystems.DriveTrain;

public class RobotMode {
	/*
	 * This Function is called when the robot first boots up.
	 * 
	 * Any initialization code necessary for Robot-level access (most/any state),
	 * should go here.
	 */
	public static void Init() {
		// Init Cameras
		//RioCameras.GetInstance();

		// Init vision correction command
		RobotMap.S_VisionCorrector = new VisionCorrector();
		
		RobotMap.S_Pneumatics = new Pneumatics(
			(int)KMap.GetKNumeric(KNumeric.INT_PNEUMATICS_PSI_SENSOR_ID));
		
		RobotMap.S_DriveTrain = new BasicDriveTrain(
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_LEFT_MASTER_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_RIGHT_MASTER_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_LEFT_SLAVE_0_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_RIGHT_SLAVE_0_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_LEFT_SLAVE_1_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_RIGHT_SLAVE_1_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_DRIVETRAIN_PIGEON_ID));
		
		RobotMap.S_Climber = new Climber(
			(int)KMap.GetKNumeric(KNumeric.INT_CLIMBER_MANTIS_LEFT_MOTOR_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_CLIMBER_MANTIS_RIGHT_MOTOR_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_CLIMBER_MANTIS_PIVOT_MOTOR_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_CLIMBER_POGO_MOTOR_ID));

		RobotMap.S_PivotArm = new PivotArm(
			(int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_MASTER_ID));

		RobotMap.S_Elevator = new Elevator(
			(int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_MOTOR_ID),
			(int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_ENDSTOP_ID),
			RobotMap.S_PivotArm);

		//RobotMap.VManager = new VisionManager(NetworkTableInstance.getDefault());

		RobotMap.Canifier = new CANifier((int)KMap.GetKNumeric(KNumeric.INT_CANIFIER_ID));
		
		RobotMap.S_EndEffector = new EndEffector(30, 31, 2, 0, 1, RobotMap.Canifier, GeneralPin.LIMF, 
			GeneralPin.LIMR, GeneralPin.QUAD_A);

		RobotMap.S_ElevatorArmManager = new ElevatorArmManager(RobotMap.S_PivotArm, RobotMap.S_Elevator);

		RobotMap.S_GPManager = new GamePositionManager(
			RobotMap.S_EndEffector, RobotMap.S_ElevatorArmManager);
			
	}
	
	/**
	 * All periodic robot things. usually calls to 
	 * functions that need to be constantly updated
	 */
	public static void Periodic() {

		// Control Camera Selection (w/ Driver controller)
		if (TORCControls.GetInput(ControllerInput.B_SelectCameraFront, InputState.Pressed) >= 1) {
			RPiCameras.GetInstance().setSelectedCamera(CameraSelect.kFront);
		}
		else if (TORCControls.GetInput(ControllerInput.B_SelectCameraRear, InputState.Pressed) >= 1) {
			RPiCameras.GetInstance().setSelectedCamera(CameraSelect.kRear);
		}

		// Operator Reset EndEffector Sensor Offset 
		if (TORCControls.GetInput(ControllerInput.B_ReinitWristEncoder, InputState.Pressed) >= 1) {
			RobotMap.S_EndEffector.resetSensorOffset();
		}

		// Variables
		SmartDashboard.putNumber("PSI", RobotMap.S_Pneumatics.getPSI());

		SmartDashboard.putNumber("GyroFusedHeading", RobotMap.S_DriveTrain.getGyroAngle());

	}
}
