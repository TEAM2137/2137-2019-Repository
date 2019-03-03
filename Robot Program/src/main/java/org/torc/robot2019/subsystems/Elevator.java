package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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

public class Elevator extends Subsystem implements InheritedPeriodic {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	
	//public enum ElevatorPositions { Zero,  }
	
	//public ElevatorPositions elevatorPosition = ElevatorPositions.floor;

	public static enum ElevatorPositions { 
		/** 
		 * Used for when robot should be driving around,
		 * with no need to use the pivot arm.
		 */
		Retracted(0),
		Level1(0),
		Level2(5000),
		Level3(14900),
		;
	
		private int positionValue;
	
		ElevatorPositions(int _positionValue) {
		  positionValue = _positionValue;
		}
	  }
	
	private DigitalInput elevatorEndstop;
	
	private TalonSRX elevatorM;

	private PivotArm pivotArm;
	
	public final static int ELEVATOR_MAX_POSITION = 
		(int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_MAX_POSITION);

	public final static int JOG_ERROR_CUTOFF = 
	  	(int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_JOG_ERROR_CUTOFF);
	
	private boolean maxLimitTripped = false;
	private boolean minLimitTripped = false;
	
	private int elevatorTargetPosition = 0;
	
	private boolean hasBeenHomed = false;
	
	Elevator_Home elevatorHomer;
	
	public Elevator(int _elevatorMID, int _endstopID, PivotArm _pivotArm) {
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

		pivotArm = _pivotArm;
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

	public void setPosition(ElevatorPositions _position) {
		setPosition(_position.positionValue);
	}

	public void setPosition(int _position) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		elevatorTargetPosition = MathExtra.clamp(_position, 0, ELEVATOR_MAX_POSITION);
		elevatorM.set(ControlMode.Position, elevatorTargetPosition);
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
	
	public void jogPosition(int positionInc) {
		if (!hasBeenHomed) {
			hasNotHomedAlert();
			return;
		}
		/* 
		* If error is too big, set elevatorTargetPosition to encoder count so jogging is 
		* instantanious
		*/
		int jogErrorThing = Math.abs(elevatorTargetPosition - getEncoder());
		System.out.println("jogErrorThing: " + jogErrorThing);
		if (jogErrorThing > JOG_ERROR_CUTOFF) {
			elevatorTargetPosition = getEncoder();
		}
		setPosition(elevatorTargetPosition += positionInc);
	}
	
	private static void hasNotHomedAlert() {
		System.out.println("Cannot move Elevator; has not homed!!");
	}

	private double getMaxArmExtension() {
		int inchConversion = (int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_TICKS_PER_INCH);

		double distanceAllowedInches = 
		KMap.GetKNumeric(KNumeric.DBL_ROBOT_MAX_EXTEND_OUTSIDE_OF_FRAME_INCHES) - 
		(
			KMap.GetKNumeric(KNumeric.DBL_ELEVATOR_MINIMUM_DISTANCE_FROM_FRAME_EDGE_INCHES)
			+ 
			KMap.GetKNumeric(KNumeric.DBL_GRABBER_LENGTH_INCHES));
		
		int distanceAllowed = (int)(distanceAllowedInches * inchConversion);

		SmartDashboard.putNumber("ElevatorDistanceAllowedInches", distanceAllowedInches);
		SmartDashboard.putNumber("ElevatorDistanceAllowed", distanceAllowed);

		double otherAngle = 90 - PivotArm.PositionToAngle(pivotArm.getEncoder());

		SmartDashboard.putNumber("otherAngle", otherAngle);

		double angleTan = Math.tan(Math.toRadians(otherAngle));
		double verticalDistance = angleTan * distanceAllowed;
		double maxExtension = Math.sqrt(Math.pow(distanceAllowed, 2) + Math.pow(verticalDistance, 2));
		return maxExtension;
	}

	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void Periodic() {
		// Check if homer has homed
		if (!hasBeenHomed && elevatorHomer != null && elevatorHomer.isFinished()) {
			System.out.println("Elevator Homed!!");
			elevatorHomer.free();
			elevatorHomer = null;
			hasBeenHomed = true;
			setPosition(0);
		}

		// Check to maintain elevator is within bounds
		/*
		if (hasBeenHomed &&
			(elevatorM.getControlMode() == ControlMode.Position ||
			elevatorM.getControlMode() == ControlMode.MotionMagic)) {

			SmartDashboard.putBoolean("CorrectingElevatorOnAngle", true);

			int elevatorTarget = elevatorTargetPosition;
			int elevatorMax = (int)getMaxArmExtension();

			SmartDashboard.putNumber("CalculatedElevatorMax", elevatorMax);

			if (elevatorTarget > elevatorMax) {
				SmartDashboard.putBoolean("LimitingElevator", true);
				//setPosition(elevatorMax);
			}
			else {
				SmartDashboard.putBoolean("LimitingElevator", false);
				//setPosition(elevatorTarget);
			}
		}
		else {
			SmartDashboard.putBoolean("CorrectingElevatorOnAngle", false);
		}
		*/

		// Print Encoders
		printEncoder();
		
		SmartDashboard.putNumber("ElevatorError", elevatorTargetPosition - getEncoder());
		SmartDashboard.putNumber("ElevatorTargetPos", elevatorTargetPosition);
		SmartDashboard.putNumber("ElevatorEncoder", getEncoder());
		SmartDashboard.putBoolean("ElevatorEndstop", getEndstop());
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
