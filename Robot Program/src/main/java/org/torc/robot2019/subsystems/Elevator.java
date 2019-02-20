package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
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

public class Elevator extends Subsystem implements InheritedPeriodic {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	
	//public enum ElevatorPositions { Zero,  }
	
	//public ElevatorPositions elevatorPosition = ElevatorPositions.floor;
	
	private DigitalInput elevatorEndstop;
	
	private TalonSRX elevatorM;
	
	public final static int ELEVATOR_MAX_POSITION = 
		(int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_MAX_POSITION);
	
	private boolean maxLimitTripped = false;
	private boolean minLimitTripped = false;
	
	private int elevatorTargetPosition = 0;
	
	private boolean hasBeenHomed = false;
	
	Elevator_Home elevatorHomer;
	
	public Elevator(int _elevatorMID, int _endstopID) {
		// Add to periodic list
		Robot.AddToPeriodic(this);
		
		elevatorM = new TalonSRX(_elevatorMID);
		// Invert motor phase
		elevatorM.setSensorPhase(true);

		MotorControllers.TalonSRXConfig(elevatorM);

		elevatorM.configContinuousCurrentLimit(5);

		elevatorM.config_kF(0, 0);
		elevatorM.config_kP(0, 10);
		elevatorM.config_kI(0, 0);
		elevatorM.config_kD(0, 0);
		elevatorM.config_IntegralZone(0, 0);
		
		elevatorEndstop = new DigitalInput(_endstopID);
	}
	
	/*
	public static int GetElevatorPositions(ElevatorPositions position) {
		int toReturn = 0;
		switch (position) {
		}
		return toReturn;
	}
	*/
	
	/**
	 * Initializes the elevator for use. This will home, and arm the elevator for use.
	 * Do not call this from the same elevator subsystem constructor.
	 */	
	public void homeElevator() {
		if (hasBeenHomed) {
			deHomeElevator();
		}
		elevatorHomer = new Elevator_Home(this);
		elevatorHomer.start();
	}
	
	/**
	 * Sets the elevator's state to "unHomed", requiring 
	 * another homing to work again.
	 */
	public void deHomeElevator() {
		if (elevatorHomer != null && elevatorHomer.isRunning()) {
			elevatorHomer.cancel();
			elevatorHomer.free();
			elevatorHomer = null;
		}
		hasBeenHomed = false;
		elevatorTargetPosition = 0;
		System.out.println("Elevator De-Homed!!");
	}
	
	public boolean getHomed() {
		return hasBeenHomed;
	}
	
	protected void setPercSpeedUnchecked(double _speed) {
		elevatorM.set(ControlMode.PercentOutput, MathExtra.clamp(_speed, (minLimitTripped ? 0 : -1), (maxLimitTripped ? 0 : 1)));
	}

	public void setPercSpeed(double _speed) {
		if (elevatorHomer != null && elevatorHomer.isRunning()) {
			System.out.println("Elevator currently homing, cannot move!");
			return;
		}
		setPercSpeedUnchecked(_speed);
	}
	
	/*
	public void positionFind(ElevatorPositions position) {
		System.out.println("Finding position: " + position.name());
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		int targPos = GetElevatorPositions(position);
		elevatorTargetPosition = targPos;
		elevatorPosition = position;
		elevator.set(ControlMode.MotionMagic, MathExtra.clamp(targPos, 0, ELEVATOR_MAX_POSITION));
	}
	*/

	public void setPosition(int _position) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		elevatorTargetPosition = _position;
		elevatorM.set(ControlMode.Position, MathExtra.clamp(_position, 0, ELEVATOR_MAX_POSITION));
	}
	
	protected void zeroEncoder() {
		elevatorM.setSelectedSensorPosition(0);
	}
	
	public void printEncoder() {
		//System.out.println(elevator.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("ElevatorEncoder", elevatorM.getSelectedSensorPosition());
	}
	
	public int getEncoder() {
		return elevatorM.getSelectedSensorPosition();
	}
	
	public boolean getEndstop() {
		return !elevatorEndstop.get();
	}
	
	/*
	public void setPosMagic(int pos) {
		elevatorM.set(ControlMode.MotionMagic, pos);
	}
	*/
	
	public void jogElevatorPos(double positionInc) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		elevatorTargetPosition += positionInc;
		elevatorTargetPosition = MathExtra.clamp(elevatorTargetPosition, 0, ELEVATOR_MAX_POSITION);
		elevatorM.set(ControlMode.MotionMagic, elevatorTargetPosition);
	}
	
	/*
	public void jogElevatorPosInc(int increment) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		elevatorPosition = Elevator.ElevatorPositions.values()[(int) MathExtra.clamp(elevatorPosition.ordinal() + increment, 0, Elevator.ElevatorPositions.values().length-1)];
		positionFind(elevatorPosition);
	}
	*/
	
	static void hasNotHomedAlert() {
		System.out.println("Cannot move Elevator; has not homed!!");
	}
	
	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void Periodic() {
		if (!hasBeenHomed && elevatorHomer != null && elevatorHomer.isFinished()) {
			System.out.println("Elevator Homed!!");
			elevatorHomer.free();
			elevatorHomer = null;
			hasBeenHomed = true;
			setPosition(0);
		}
		// Print Encoders
		printEncoder();
		
		SmartDashboard.putNumber("ElevatorError", elevatorTargetPosition - getEncoder());
		SmartDashboard.putNumber("ElevatorEncoder", getEncoder());
		//System.out.println("ElevatorEncoder " + elevatorM.getSelectedSensorPosition(0));
		SmartDashboard.putBoolean("ElevatorEndstop", getEndstop());
		
		SmartDashboard.putNumber("ElevatorVel", elevatorM.getSelectedSensorVelocity(0));
		//System.out.println("ElevatorVel " + elevatorM.getSelectedSensorVelocity(0));
	}
	
}
class Elevator_Home extends CLCommand {
	
	public static enum HomingStates { firstMoveDown, secondMoveUp }
	
	/**
	 * The calling Subsystem of the command.
	 */
	Elevator elevSubsystem; 
	
	HomingStates homingState = HomingStates.firstMoveDown;
	
	double firstMoveDownPerc = 0.5;
	double secondMoveUpPerc = 0.5;
	
	public Elevator_Home(Elevator elevator) {
		// Use requires() here to declare subsystem dependencies
		elevSubsystem = elevator;
		requires(elevSubsystem);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("Elevator_Home Init");
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		System.out.println("Running homing command!");
		switch (homingState) {
			case firstMoveDown:
				elevSubsystem.setPercSpeedUnchecked(-firstMoveDownPerc);
				if (elevSubsystem.getEndstop()) {
					System.out.println("firstMoveDown Done!");
					homingState = HomingStates.secondMoveUp;
				}
				break;
			case secondMoveUp:
				elevSubsystem.setPercSpeedUnchecked(secondMoveUpPerc);
				if (!elevSubsystem.getEndstop()) {
					System.out.println("secondMoveUp Done!");
					elevSubsystem.zeroEncoder();
					elevSubsystem.setPercSpeedUnchecked(0);
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
