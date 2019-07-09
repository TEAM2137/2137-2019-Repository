/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.subsystems;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.MainRunTime;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.tools.CLCommand;

import edu.wpi.first.wpilibj.command.Subsystem;

/** 
 * A class used to automatically limit elevator and pivotArm movements to not
 * extend outside of the robot's allowed distance (30").
 */
public class ElevatorArmManager extends Subsystem implements InheritedPeriodic  {

  private PivotArm pivotArm;
  private Elevator elevator;

  private ElevatorArmIndMove indMoveCommand;

  public ElevatorArmManager(PivotArm _pivotArm, Elevator _elevator) {
    // Add this class to periodic update
    MainRunTime.AddToPeriodic(this);

    pivotArm = _pivotArm;
    elevator = _elevator;

  }

  public void setPosition(int _pivotArmPosition, int _elevatorPosition) {
    if (indMoveCommand != null && indMoveCommand.isRunning()) {
      indMoveCommand.cancel();
      indMoveCommand.free();
    }

    int futureMaxElev = GetMaxArmExtension(PivotArm.PositionToAngle(_pivotArmPosition) - 90);

    // If future elevator position is going to be too big at the end of the move,
    // limit it early.
    if (_elevatorPosition > futureMaxElev) {
      System.out.println("EAM: Early limiting elevator");
      _elevatorPosition = futureMaxElev;
    }
    // If current elevator
    if (elevator.getEncoder() > futureMaxElev) {
      System.out.println("EAM: Automagically limiting arm and elevator!");
      // Start independent elevator and pivot moves.
      indMoveCommand = new ElevatorArmIndMove(pivotArm, elevator, _pivotArmPosition, _elevatorPosition);
      indMoveCommand.start();
    }
    else {
      System.out.println("EAM: Normally setting elev and arm positions...");
      pivotArm.setPosition(_pivotArmPosition);
      elevator.setPosition(_elevatorPosition);
    }
  }

  public void setPivotArmPosition(int _pivotArmPosition) {
    setPosition(_pivotArmPosition, elevator.getEncoder());
  }
  public void setElevatorPosition(int _elevatorPosition) {
    setPosition(pivotArm.getEncoder(), _elevatorPosition);
  }

  public void jogPivotArm(int _jogValue) {
    
  }
  public void jogElevator(int _jogValue) {
    
  }

  public static int GetMaxArmExtension(double _angle) {
    double maxInInches =  Math.sqrt(900 + Math.pow(Math.tan(_angle * Math.PI / 180) * 30, 2)) - 
    (KMap.GetKNumeric(KNumeric.DBL_ELEVATOR_MINIMUM_DISTANCE_FROM_FRAME_EDGE_INCHES) +
     KMap.GetKNumeric(KNumeric.DBL_GRABBER_LENGTH_INCHES));

		return (int)(maxInInches * KMap.GetKNumeric(KNumeric.INT_ELEVATOR_TICKS_PER_INCH));
  }
  public int GetMaxArmExtension() {
    return GetMaxArmExtension(PivotArm.PositionToAngle(pivotArm.getEncoder()) - 90);
  }

  public Elevator getElevator() {
    return elevator;
  }

  public PivotArm getPivotArm() {
    return pivotArm;
  }

  @Override
  protected void initDefaultCommand() {
  }

  @Override
  public void Periodic() {
    /*
    if (pivotArmTargetPosition != -1 && elevatorTargetPosition != -1) {
      if (pivotArmTargetPosition != ) {

      }
    }
    */
  }

}
class ElevatorArmIndMove extends CLCommand{

  private static final int ELEVATOR_TARGET_RANGE = 
    (int)KMap.GetKNumeric(KNumeric.INT_ELEVATOR_RANGE_WITHIN_TARGET);
  private static final int PIVOT_ARM_TARGET_RANGE = 
    (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_RANGE_WITHIN_TARGET);;

  private PivotArm pivotArm;
  private Elevator elevator;

  private final int armTarget;
  private final int elevTarget;

  public ElevatorArmIndMove(PivotArm _pivotArm, Elevator _elevator, int _armTarget, int _elevTarget) {
    pivotArm = _pivotArm;
    elevator = _elevator;
    
    armTarget = _armTarget;
    elevTarget = _elevTarget;
  }

	@Override
	protected void initialize() {

	}

	@Override
	protected void execute() {

    boolean pivotArmAtTarget = pivotArmAtTarget();
    boolean elevAtTarget = elevAtTarget();

    // If move finished
    if (pivotArmAtTarget && elevAtTarget) {
      System.out.println("EAIM: Move finished");
      CLCommandDone = true;
      return;
    }
    // Neither moved
    if (!pivotArmAtTarget && !elevAtTarget) {
      //System.out.println("EAIM: Moving elevator...");
      elevator.setPosition(elevTarget);
    }
    // Elevator done moving
    else if (!pivotArmAtTarget && elevAtTarget) {
      //System.out.println("EAIM: Elevator Done. Moving arm...");
      pivotArm.setPosition(armTarget);
    }
  }
  
	@Override
	protected void end() {
    System.out.println("EAIM: Ending command");
    pivotArm.setPosition(pivotArm.getEncoder());
    elevator.setPosition(elevator.getEncoder());
	}

	@Override
	protected void interrupted() {
    end();
  }
  
  private boolean elevAtTarget() {
    int elevEncoder = elevator.getEncoder();

    if (elevEncoder > (elevTarget - ELEVATOR_TARGET_RANGE) && 
        elevEncoder < (elevTarget + ELEVATOR_TARGET_RANGE)) {
        return true;
    }
    else {
        return false;
    }
  }

  private boolean pivotArmAtTarget() {
    int pivotEncoder = pivotArm.getEncoder();

    if (pivotEncoder > (armTarget - PIVOT_ARM_TARGET_RANGE) && 
        pivotEncoder < (armTarget + PIVOT_ARM_TARGET_RANGE)) {
        return true;
    }
    else {
        return false;
    }
  }
}
