package org.torc.robot2019.program;

import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.tools.MathExtra;

import edu.wpi.first.wpilibj.GenericHID;

public class TestMode {
	private static GenericHID driversController;

	private static double lastAxisVal = 0;
	public static void Init() {
		System.out.println("Robot Testing. Random number here: " + Math.random());

		driversController = TORCControls.GetDriverController();
	}
	public static void Periodic() {
		
		// Might need to alter these by 0.5
		double mantisLeft = -TORCControls.GetInput(ControllerInput.A_MantisLeft);
		double mantisRight = TORCControls.GetInput(ControllerInput.A_MantisRight);
		RobotMap.S_Climber.setMantisSpeed(mantisLeft, mantisRight);

		double mantisPivot = MathExtra.applyDeadband(
			TORCControls.GetInput(ControllerInput.A_MantisArm), 0.2);
		RobotMap.S_Climber.setMantisPivotSpeed(mantisPivot);

		double pogoStick = MathExtra.applyDeadband(
			TORCControls.GetInput(ControllerInput.A_PogoControl), 0.2); /*RobotMap.Controls.getInput(ControllerInput.B_PogoExtend) + 
		-RobotMap.Controls.getInput(ControllerInput.B_PogoContract);
		*/
		//RobotMap.S_Climber.setPogoStickSpeed(pogoStick);
		
		//RobotMap.S_PivotArm.setPercSpeed(RobotMap.Controls.getInput(ControllerInput.A_PivotArmControl));
	}
}
