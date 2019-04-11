package org.torc.robot2019.subsystems;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Robot;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;

public class EndEffector extends Subsystem implements InheritedPeriodic {

	public static enum EndEffectorPositions { 
		/** 
		 * Used for when robot should be driving around,
		 * with no need to use the pivot arm.
		 */
		Zero(0),
		Travel(2048),
		Climbing(2993),
		;
	
		private int positionValue;
	
		EndEffectorPositions(int _positionValue) {
		  positionValue = _positionValue;
		}
	}

	public static enum SolenoidStates {
		Open, Closed
	}
	
	private TalonSRX endEffectorM;

	private VictorSPX rollerM;
	
	public final static int END_EFFECTOR_MAX_POSITION = 
		(int)KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_WRIST_MAX_POSITION);

	public final static int END_EFFECTOR_MIN_POSITION = 
		(int)KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_WRIST_MIN_POSITION);
	
	private int targetPosition = 2048;
	
	// private boolean hasBeenHomed = false;
	
	// private EndEffector_Home endEffectorHomer;

	private CANifier canifier;

	private GeneralPin ballSensorPin1;
	private GeneralPin ballSensorPin2;

	private Solenoid pistonOpenS;
	private Solenoid pistonClosedS;

	private SolenoidStates solenoidState = SolenoidStates.Open;
	
	public EndEffector(int _endEffectorMID, int _rollerMID, int _PCMID, int _pistonOpenSID, 
		int _pistonClosedSID, CANifier _canifier, GeneralPin _ballSensorPin1, GeneralPin _ballSensorPin2) {
		// Add to periodic list
		Robot.AddToPeriodic(this);
		
		endEffectorM = new TalonSRX(_endEffectorMID);

		MotorControllers.TalonSRXConfig(endEffectorM);

		endEffectorM.configContinuousCurrentLimit(2);
		
		endEffectorM.configClosedLoopPeakOutput(0, 1);

		endEffectorM.config_kF(0, 5.6);
		endEffectorM.config_kP(0, 8);//KMap.GetKNumeric(KNumeric.DBL_END_EFFECTOR_KP));
		endEffectorM.config_kI(0, 0);//KMap.GetKNumeric(KNumeric.DBL_END_EFFECTOR_KI));
		endEffectorM.config_kD(0, 0);
		endEffectorM.config_IntegralZone(0, 0);//(int)KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_KIZONE));

		endEffectorM.configMotionCruiseVelocity(200);
		endEffectorM.configMotionAcceleration(100);

		rollerM = new VictorSPX(_rollerMID);

		pistonOpenS = new Solenoid(_PCMID, _pistonOpenSID);
		pistonClosedS = new Solenoid(_PCMID, _pistonClosedSID);

		canifier = _canifier;

		// endstopPin = _endstopPin;
		ballSensorPin1 = _ballSensorPin1;
		ballSensorPin2 = _ballSensorPin2;

		int absolutePosition = endEffectorM.getSensorCollection().getPulseWidthPosition();
		absolutePosition += KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_ENCODER_OFFSET);
		absolutePosition &= 0xFFF;// Mask out overflows, keep bottom 12 bits
		endEffectorM.setSelectedSensorPosition(absolutePosition, 0, 10);

		SmartDashboard.putNumber("EEDesiredPos", 2048);
	}
	
	// public void homeEndEffector() {
	// 	if (hasBeenHomed) {
	// 		//deHomeEndEffector();
	// 		System.out.println("End effector already homed; Not re-homing...");
	// 		return;
	// 	}
	// 	// endEffectorHomer = new EndEffector_Home(this);
	// 	// endEffectorHomer.start();
	// }
	
	/**
	 * Sets the end effector's state to "unHomed", requiring 
	 * another homing to work again.
	 */
	/*
	public void deHomeEndEffector() {
		if (endEffectorHomer != null && endEffectorHomer.isRunning()) {
			endEffectorHomer.cancel();
			endEffectorHomer.free();
			endEffectorHomer = null;
		}
		hasBeenHomed = false;
		targetPosition = 0;
		System.out.println("EndEffector De-Homed!!");
	}
	*/
	
	// public boolean getHomed() {
	// 	return hasBeenHomed;
	// }
	
	protected void setWristPercSpeedUnchecked(double _speed) {
		endEffectorM.set(ControlMode.PercentOutput, _speed);
	}

	public void setWristPercSpeed(double _speed) {
		// if (endEffectorHomer != null && endEffectorHomer.isRunning()) {
		// 	System.out.println("EndEffector currently homing, cannot move!");
		// 	return;
		// }
		setWristPercSpeedUnchecked(_speed);
	}

	public void setRollerPercSpeed(double _speed) {
		rollerM.set(ControlMode.PercentOutput, _speed);
	}

	public void setPosition(EndEffectorPositions _position) {
		setPosition(_position.positionValue);
	}

	public void setPosition(int _position) {
		targetPosition = MathExtra.clamp(_position, END_EFFECTOR_MIN_POSITION, END_EFFECTOR_MAX_POSITION);
		endEffectorM.set(ControlMode.MotionMagic, targetPosition);
	}

	public void jogPosition(int positionInc) {
		setPosition(targetPosition += positionInc);
	}

	public void printEncoder() {
		SmartDashboard.putNumber("EndEffectorEncoder", endEffectorM.getSelectedSensorPosition());
	}
	
	public int getEncoder() {
		return endEffectorM.getSelectedSensorPosition();
	}

	public boolean getBallSensor() {
		return !canifier.getGeneralInput(ballSensorPin1) || !canifier.getGeneralInput(ballSensorPin2);
	}

	public void setSolenoid(SolenoidStates _state) {
		solenoidState = _state; // Update the classes' current solenoid state
		switch (_state) {
			case Open:
				pistonOpenS.set(true);
				pistonClosedS.set(false);
				break;
			case Closed:
				pistonOpenS.set(false);
				pistonClosedS.set(true);
				break;
		}
		System.out.println("Solenoids are being set!");
	}

	/**
	 * Inverts the current solenoid state.
	 * If it is currently open, this will close it, etc.
	 */
	public void invertSolenoid() {
		switch(solenoidState) {
			case Open:
				setSolenoid(SolenoidStates.Closed);
				break;
			case Closed:
				setSolenoid(SolenoidStates.Open);
				break;
		}
	}

	public SolenoidStates getSolenoid() {
		return solenoidState;
	}
	
	// private static void hasNotHomedAlert() {
	// 	System.out.println("Cannot move EndEffector; has not homed!!");
	// }

	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void Periodic() {

		// Print Encoders
		printEncoder();

		SmartDashboard.putNumber("EndEffectorError", targetPosition - getEncoder());
		SmartDashboard.putNumber("EndEffectorEncoder", getEncoder());
		SmartDashboard.putNumber("EndEffectorRawEncoder", endEffectorM.getSensorCollection().getPulseWidthPosition());

		SmartDashboard.putNumber("EndEffectorTarget", targetPosition);

		SmartDashboard.putBoolean("BallSensor", getBallSensor());
		SmartDashboard.putBoolean("BallSensor1", !canifier.getGeneralInput(ballSensorPin1));
		SmartDashboard.putBoolean("BallSensor2", !canifier.getGeneralInput(ballSensorPin2));

		SmartDashboard.putNumber("EndEffectorVel", endEffectorM.getSelectedSensorVelocity(0));

		
	}
	
}