package org.torc.robot2019.program;

import com.ctre.phoenix.CANifier;

import org.torc.robot2019.hardware.ElevatorArmManager;
import org.torc.robot2019.commands.VisionCorrector;
import org.torc.robot2019.hardware.BasicDriveTrain;
import org.torc.robot2019.hardware.Climber;
import org.torc.robot2019.hardware.Elevator;
import org.torc.robot2019.hardware.EndEffector;
import org.torc.robot2019.hardware.PivotArm;
import org.torc.robot2019.hardware.Pneumatics;
import org.torc.robot2019.hardware.gamepositionmanager.GamePositionManager;


/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
	public static VisionCorrector S_VisionCorrector;

	public static Pneumatics S_Pneumatics;

	public static BasicDriveTrain S_DriveTrain;

	public static Climber S_Climber;

	public static PivotArm S_PivotArm;

	public static Elevator S_Elevator;

	//public static VisionManager VManager;

	public static CANifier Canifier;

	public static EndEffector S_EndEffector;

	public static GamePositionManager S_GPManager;

	public static ElevatorArmManager S_ElevatorArmManager;
	
}
