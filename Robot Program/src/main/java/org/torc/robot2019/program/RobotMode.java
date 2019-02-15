package org.torc.robot2019.program;

import org.torc.robot2019.subsystems.BasicDriveTrain;
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
		// Keep this at top of other constructor calls
		RobotMap.RobInfo = new RobotInfo(9);
		
		RobotMap.PNUPressure = new AnalogInput(0);

		RobotMap.Controls = new TORCControls(new XboxController(0));

		RobotMap.DriveTrain = new BasicDriveTrain(22, 10, 23, 11, 0, 1);

		RobotMap.VManager = new VisionManager(NetworkTableInstance.getDefault());
	}
	
	/**
	 * All periodic robot things. usually calls to 
	 * functions that need to be constantly updated
	 */
	public static void Periodic() {
		
		SmartDashboard.putNumber("PSI", Pneumatics.getPSIFromAnalog(RobotMap.PNUPressure));
		
	}
}
