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
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class EndEffector extends Subsystem implements InheritedPeriodic {

	public static enum EndEffectorPositions { 
		/** 
		 * Used for when robot should be driving around,
		 * with no need to use the pivot arm.
		 */
		Zero(0),
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
	
	private int targetPosition = 0;
	
	private boolean hasBeenHomed = false;
	
	private EndEffector_Home endEffectorHomer;

	private CANifier canifier;

	private GeneralPin endstopPin;
	private GeneralPin ballSensorPin;

	private Solenoid pistonOpenS;
	private Solenoid pistonClosedS;

	private SolenoidStates solenoidState = SolenoidStates.Open;
	
	public EndEffector(int _endEffectorMID, int _rollerMID, int _PCMID, int _pistonOpenSID, 
		int _pistonClosedSID, CANifier _canifier, GeneralPin _endstopPin, GeneralPin _ballSensorPin) {
		// Add to periodic list
		Robot.AddToPeriodic(this);
		
		endEffectorM = new TalonSRX(_endEffectorMID);

		MotorControllers.TalonSRXConfig(endEffectorM);

		endEffectorM.configContinuousCurrentLimit(2);
		
		endEffectorM.configClosedLoopPeakOutput(0, 0.75);

		endEffectorM.config_kF(0, 0);
		endEffectorM.config_kP(0, 2);
		endEffectorM.config_kI(0, 0.01);
		endEffectorM.config_kD(0, 0);
		endEffectorM.config_IntegralZone(0, 50);

		rollerM = new VictorSPX(_rollerMID);

		pistonOpenS = new Solenoid(_PCMID, _pistonOpenSID);
		pistonClosedS = new Solenoid(_PCMID, _pistonClosedSID);

		canifier = _canifier;

		endstopPin = _endstopPin;
		ballSensorPin = _ballSensorPin;
	}
	
	public void homeEndEffector() {
		if (hasBeenHomed) {
			deHomeEndEffector();
		}
		endEffectorHomer = new EndEffector_Home(this);
		endEffectorHomer.start();
	}
	
	/**
	 * Sets the end effector's state to "unHomed", requiring 
	 * another homing to work again.
	 */
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
	
	public boolean getHomed() {
		return hasBeenHomed;
	}
	
	// TODO: Make this protected again when done
	public void setWristPercSpeedUnchecked(double _speed) {
		endEffectorM.set(ControlMode.PercentOutput, _speed);
	}

	public void setWristPercSpeed(double _speed) {
		if (endEffectorHomer != null && endEffectorHomer.isRunning()) {
			System.out.println("EndEffector currently homing, cannot move!");
			return;
		}
		setWristPercSpeedUnchecked(_speed);
	}

	public void setRollerPercSpeed(double _speed) {
		rollerM.set(ControlMode.PercentOutput, _speed);
	}

	public void setPosition(EndEffectorPositions _position) {
		setPosition(_position.positionValue);
	}

	public void setPosition(int _position) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		targetPosition = MathExtra.clamp(_position, 0, END_EFFECTOR_MAX_POSITION);
		endEffectorM.set(ControlMode.Position, targetPosition);
	}

	public void jogPosition(int positionInc) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		/*
		elevatorTargetPosition += positionInc;
		elevatorTargetPosition = MathExtra.clamp(elevatorTargetPosition, 0, ELEVATOR_MAX_POSITION);
		*/
		setPosition(targetPosition += positionInc);
	}
	
	protected void zeroEncoder() {
		endEffectorM.setSelectedSensorPosition(0);
	}
	
	public void printEncoder() {
		//System.out.println(elevator.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("EndEffectorEncoder", endEffectorM.getSelectedSensorPosition());
	}
	
	public int getEncoder() {
		return endEffectorM.getSelectedSensorPosition();
	}
	
	public boolean getWristEndstop() {
		return !canifier.getGeneralInput(endstopPin);
	}

	public boolean getBallSensor() {
		return !canifier.getGeneralInput(ballSensorPin);
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
	
	private static void hasNotHomedAlert() {
		System.out.println("Cannot move EndEffector; has not homed!!");
	}

	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void Periodic() {
		// Check if homer has homed
		if (!hasBeenHomed && endEffectorHomer != null && endEffectorHomer.isFinished()) {
			System.out.println("EndEffector Homed!!");
			endEffectorHomer.free();
			endEffectorHomer = null;
			hasBeenHomed = true;
			setPosition(0);
		}

		// Print Encoders
		printEncoder();
		
		SmartDashboard.putNumber("EndEffectorError", targetPosition - getEncoder());
		SmartDashboard.putNumber("EndEffectorEncoder", getEncoder());
		//System.out.println("ElevatorEncoder " + endEffectorM.getSelectedSensorPosition(0));
		SmartDashboard.putBoolean("WristEndstop", getWristEndstop());
		SmartDashboard.putBoolean("BallSensor", getBallSensor());
		
		SmartDashboard.putNumber("EndEffectorVel", endEffectorM.getSelectedSensorVelocity(0));
		//System.out.println("ElevatorVel " + endEffectorM.getSelectedSensorVelocity(0));
	}
	
}
class EndEffector_Home extends CLCommand {
	
	public static enum HomingStates { firstMoveDown, secondMoveUp }
	
	/**
	 * The calling Subsystem of the command.
	 */
	EndEffector endEffectorSubsystem; 
	
	HomingStates homingState = HomingStates.firstMoveDown;
	
	double firstMoveDownPerc = 0.5;
	double secondMoveUpPerc = 0.5;
	
	public EndEffector_Home(EndEffector _endEffector) {
		// Use requires() here to declare subsystem dependencies
		endEffectorSubsystem = _endEffector;
		requires(endEffectorSubsystem);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("EndEffector_Home Init");
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		switch (homingState) {
			case firstMoveDown:
				endEffectorSubsystem.setWristPercSpeedUnchecked(-firstMoveDownPerc);
				if (endEffectorSubsystem.getWristEndstop()) {
					System.out.println("firstMoveDown Done!");
					homingState = HomingStates.secondMoveUp;
				}
				break;
			case secondMoveUp:
				endEffectorSubsystem.setWristPercSpeedUnchecked(secondMoveUpPerc);
				if (!endEffectorSubsystem.getWristEndstop()) {
					System.out.println("secondMoveUp Done!");
					endEffectorSubsystem.zeroEncoder();
					endEffectorSubsystem.setWristPercSpeedUnchecked(0);
					CLCommandDone = true;
				}
				break;
		}
		
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}
}
