package org.torc.robot2019.program;

import com.ctre.phoenix.sensors.PigeonIMU;

import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.tools.Pneumatics;
import org.torc.robot2019.vision.VisionManager;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.XboxController;
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
		
		RobotMap.PNUPressure = new AnalogInput(0);

		RobotMap.Controls = new TORCControls(new XboxController(0));
		// _leftMID, _rightMID, _leftS0ID, _rightS0ID, _leftS1ID, _rightS1ID, 
		// _rightShifterID, _leftShifterID
		RobotMap.S_DriveTrain = new BasicDriveTrain(10, 11, 12, 13, 14, 15, 0, 1);
		
		RobotMap.S_Climber = new Climber(41, 42, 40, 43);

		RobotMap.PigeonGyro = new PigeonIMU(4);

		RobotMap.VManager = new VisionManager(NetworkTableInstance.getDefault());
	}
	
	/**
	 * All periodic robot things. usually calls to 
	 * functions that need to be constantly updated
	 */
	public static void Periodic() {
		
		SmartDashboard.putNumber("PSI", Pneumatics.getPSIFromAnalog(RobotMap.PNUPressure));

		SmartDashboard.putNumber("GyroFusedHeading", RobotMap.PigeonGyro.getFusedHeading());
		
	}
}
