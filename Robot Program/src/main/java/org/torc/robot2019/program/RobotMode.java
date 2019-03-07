package org.torc.robot2019.program;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Cameras;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.Pneumatics;
import org.torc.robot2019.subsystems.GamePositionManager;
import org.torc.robot2019.vision.VisionManager;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
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
		
		RobotMap.S_Cameras = new Cameras();
		
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
		
		RobotMap.S_EndEffector = new EndEffector(30, 31, 0, 0, 1, RobotMap.Canifier, GeneralPin.LIMR, 
			GeneralPin.QUAD_B);

		RobotMap.S_ElevatorArmManager = new ElevatorArmManager(RobotMap.S_PivotArm, RobotMap.S_Elevator);

		RobotMap.S_GPManager = new GamePositionManager(RobotMap.S_PivotArm, RobotMap.S_Elevator, 
		RobotMap.S_EndEffector, RobotMap.S_ElevatorArmManager);
	}
	
	/**
	 * All periodic robot things. usually calls to 
	 * functions that need to be constantly updated
	 */
	public static void Periodic() {
		
		SmartDashboard.putNumber("PSI", RobotMap.S_Pneumatics.getPSI());

		SmartDashboard.putNumber("GyroFusedHeading", RobotMap.S_DriveTrain.getGyroAngle());

	}
}
