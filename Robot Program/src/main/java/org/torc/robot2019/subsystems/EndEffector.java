package org.torc.robot2019.subsystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
		Travel(2019),
		Climbing(2917),
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

	private GeneralPin hatchPanelSensorPin;

	private Solenoid pistonOpenS;
	private Solenoid pistonClosedS;

	private SolenoidStates solenoidState = SolenoidStates.Open;
	
	public EndEffector(int _endEffectorMID, int _rollerMID, int _PCMID, int _pistonOpenSID, 
		int _pistonClosedSID, CANifier _canifier, GeneralPin _ballSensorPin1, GeneralPin _ballSensorPin2,
		GeneralPin _hatchPanelSensorPin) {
		// Add to periodic list
		Robot.AddToPeriodic(this);
		
		endEffectorM = new TalonSRX(_endEffectorMID);

		MotorControllers.TalonSRXConfig(endEffectorM);

		endEffectorM.configContinuousCurrentLimit(2);
		
		endEffectorM.configClosedLoopPeakOutput(0, 1);

		endEffectorM.config_kF(0, 5.6);
		endEffectorM.config_kP(0, 6.5);//KMap.GetKNumeric(KNumeric.DBL_END_EFFECTOR_KP));
		endEffectorM.config_kI(0, 0);//KMap.GetKNumeric(KNumeric.DBL_END_EFFECTOR_KI));
		endEffectorM.config_kD(0, 0);
		endEffectorM.config_IntegralZone(0, 0);//(int)KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_KIZONE));

		endEffectorM.configMotionCruiseVelocity(200);
		endEffectorM.configMotionAcceleration(200);

		rollerM = new VictorSPX(_rollerMID);

		pistonOpenS = new Solenoid(_PCMID, _pistonOpenSID);
		pistonClosedS = new Solenoid(_PCMID, _pistonClosedSID);

		canifier = _canifier;

		// endstopPin = _endstopPin;
		ballSensorPin1 = _ballSensorPin1;
		ballSensorPin2 = _ballSensorPin2;

		hatchPanelSensorPin = _hatchPanelSensorPin;

		resetSensorOffset();

		SmartDashboard.putNumber("EEDesiredPos", 2048);

	}

	public void resetSensorOffset() {

		int samples = (int)KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_NUMBER_OF_PULSE_SAMPLES);

		List<Integer> collectedSamples = new ArrayList<Integer>();

		for (int i = 0; i < samples; i++) {
			collectedSamples.add(endEffectorM.getSensorCollection().getPulseWidthPosition());
		}

		Collections.sort(collectedSamples);

		//System.out.println("collectedSamples Pre-Trim: ");
		//System.out.println(collectedSamples.toString());

		// remove upper and lower bounds
		collectedSamples.remove(0);
		collectedSamples.remove(collectedSamples.size() - 1);

		//System.out.println("collectedSamples Post-Trim: ");
		//System.out.println(collectedSamples.toString());

		// Determine average
		int sumValue = 0;

		for (Integer i : collectedSamples) {
			sumValue += i;
		}

		int absolutePosition = sumValue / collectedSamples.size();

		//System.out.println("absolutePosition: " + absolutePosition);

		absolutePosition += KMap.GetKNumeric(KNumeric.INT_END_EFFECTOR_ENCODER_OFFSET);
		absolutePosition &= 0xFFF;// Mask out overflows, keep bottom 12 bits
		endEffectorM.setSelectedSensorPosition(absolutePosition, 0, 10);

		System.out.println("EndEffector Sensor Position Reset!");
	}
	
	protected void setWristPercSpeedUnchecked(double _speed) {
		endEffectorM.set(ControlMode.PercentOutput, _speed);
	}

	public void setWristPercSpeed(double _speed) {
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

	public boolean getHatchPanelSensor() {
		return !canifier.getGeneralInput(hatchPanelSensorPin);
	}

	public void setSolenoid(SolenoidStates _state) {
		solenoidState = _state; // Update the classes' current solenoid state
		switch (_state) {
			case Open:
				pistonOpenS.set(false);
				pistonClosedS.set(true);
				break;
			case Closed:
				pistonOpenS.set(true);
				pistonClosedS.set(false);
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

		SmartDashboard.putBoolean("HatchPanelSensor", getHatchPanelSensor());

		SmartDashboard.putString("SolenoidState", getSolenoid().toString());

		SmartDashboard.putNumber("EndEffectorVel", endEffectorM.getSelectedSensorVelocity(0));

		
	}
	
}