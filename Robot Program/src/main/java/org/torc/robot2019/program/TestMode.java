package org.torc.robot2019.program;

import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.tools.MathExtra;

public class TestMode {
	public static void Init() {
		System.out.println("Robot Testing. Random number here: " + Math.random());
	}
	public static void Periodic() {
		// Might need to alter these by 0.5
		double mantisLeft = -RobotMap.Controls.getInput(ControllerInput.A_MantisLeft);
		double mantisRight = RobotMap.Controls.getInput(ControllerInput.A_MantisRight);
		RobotMap.S_Climber.setMantisSpeed(mantisLeft, mantisRight);

		double mantisPivot = MathExtra.applyDeadband(
			RobotMap.Controls.getInput(ControllerInput.A_MantisArm), 0.2);
		RobotMap.S_Climber.setMantisPivotSpeed(mantisPivot);

		double pogoStick = MathExtra.applyDeadband(
			RobotMap.Controls.getInput(ControllerInput.A_PogoControl), 0.2); /*RobotMap.Controls.getInput(ControllerInput.B_PogoExtend) + 
		-RobotMap.Controls.getInput(ControllerInput.B_PogoContract);
		*/
		RobotMap.S_Climber.setPogoStickSpeed(pogoStick);
		
	}
}
