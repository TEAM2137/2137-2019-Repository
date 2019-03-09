package org.torc.robot2019.commands;

import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.EndEffector.SolenoidStates;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.GPeiceTarget;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.GamePositions;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.RobotSides;

import org.torc.robot2019.tools.CLCommand;

public class GPPickup extends CLCommand {

  public static enum PickupStates {
    MovingIntoPosition, WaitingForMove, WaitingForGP, MovingToFinalPosition
  }

  private GamePositionManager gPosManager;

  private PivotArm pivotArm;

  private Elevator elevator;

  private EndEffector endEffector;

  private PickupStates currentState = PickupStates.MovingIntoPosition;

  private int ballSenseCounter = 0;

  private static final int BALL_SENSE_COUNTER_MAX = 500 / 20;

  private boolean directInterrupt = false;

  public GPPickup(GamePositionManager _gPosManager, PivotArm _pivotArm, Elevator _elevator, 
    EndEffector _endEffector) {
    gPosManager = _gPosManager;

    pivotArm = _pivotArm;

    elevator = _elevator;

    endEffector = _endEffector;
  }

  @Override
  protected void initialize() {

  }

  @Override
  protected void execute() {
    if (directInterrupt) {
      return;
    }

    switch (currentState) {
      // Initial state
      case MovingIntoPosition:

        gPosManager.setPosition(GamePositions.CargoFloorPickup, RobotSides.kFront, GPeiceTarget.kCargo);
        currentState = PickupStates.WaitingForMove;

        break;
      case WaitingForMove:

        if (elevator.isAtTarget() && pivotArm.isAtTarget()) {
          currentState = PickupStates.WaitingForGP;
        }

        break;
      case WaitingForGP:

        // Start Rollers intake
        endEffector.setRollerPercSpeed(1);
        // Open End Effector
        endEffector.setSolenoid(SolenoidStates.Open);

        if (endEffector.getWristEndstop()) {
          ballSenseCounter++;
        }
        else {
          ballSenseCounter = 0;
        }

        if (ballSenseCounter > BALL_SENSE_COUNTER_MAX) {
          // Stop rollers
          endEffector.setRollerPercSpeed(0);
          // Close solenoid
          endEffector.setSolenoid(SolenoidStates.Closed);
          currentState = PickupStates.MovingToFinalPosition;
        }

        break;
      case MovingToFinalPosition:

        gPosManager.setPosition(GamePositions.CargoShuttle, RobotSides.kFront, GPeiceTarget.kCargo);

        break;
    }

  }

  @Override
  protected void end() {

  }

  @Override
  protected void interrupted() {

  }

  public void setDirectInterrupt(boolean _value) {
    directInterrupt = _value;
  }
}
