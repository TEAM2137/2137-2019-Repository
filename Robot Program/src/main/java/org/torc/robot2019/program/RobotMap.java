package org.torc.robot2019.program;

import com.ctre.phoenix.CANifier;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.Pneumatics;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager;
import org.torc.robot2019.vision.VisionManager;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
	public static Pneumatics S_Pneumatics;

	public static BasicDriveTrain S_DriveTrain;

	public static Climber S_Climber;

	public static PivotArm S_PivotArm;

	public static Elevator S_Elevator;

	public static VisionManager VManager;

	public static CANifier Canifier;

	public static EndEffector S_EndEffector;

	public static GamePositionManager S_GPManager;

	public static ElevatorArmManager S_ElevatorArmManager;
	
}
