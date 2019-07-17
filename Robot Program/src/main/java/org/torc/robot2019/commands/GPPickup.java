package org.torc.robot2019.commands;

import org.torc.robot2019.hardware.Elevator;
import org.torc.robot2019.hardware.EndEffector;
import org.torc.robot2019.hardware.PivotArm;
import org.torc.robot2019.hardware.EndEffector.SolenoidStates;
import org.torc.robot2019.hardware.gamepositionmanager.GamePositionManager;
import org.torc.robot2019.hardware.gamepositionmanager.GamePositionManager.GPeiceTarget;
import org.torc.robot2019.hardware.gamepositionmanager.GamePositionManager.GamePositions;
import org.torc.robot2019.hardware.gamepositionmanager.GamePositionManager.RobotSides;

import org.torc.robot2019.tools.CLCommand;

public class GPPickup extends CLCommand {

  public static enum PickupStates {
    MovingIntoPosition, WaitingForGP, MovingToFinalPosition
  }

  private GamePositionManager gPosManager;

  private PivotArm pivotArm;

  private Elevator elevator;

  private EndEffector endEffector;

  private PickupStates currentState = PickupStates.MovingIntoPosition;

  private int ballSenseCounter = 0;

  private static final int BALL_SENSE_COUNTER_MAX = 500 / 10;

  private boolean directInterrupt = false;

  private RobotSides robotSide;

  public GPPickup(GamePositionManager _gPosManager, PivotArm _pivotArm, Elevator _elevator, 
    EndEffector _endEffector, RobotSides _robotSide) {
    gPosManager = _gPosManager;

    pivotArm = _pivotArm;

    elevator = _elevator;

    endEffector = _endEffector;

    robotSide = _robotSide;
  }

  @Override
  protected void initialize() {
      currentState = PickupStates.MovingIntoPosition;
  }

  @Override
  protected void execute() {
    if (directInterrupt) {
      return;
    }

    switch (currentState) {
      // Initial state
      case MovingIntoPosition:
        System.out.println("GPPPickup: Starting to move into position...");

        gPosManager.setPosition(GamePositions.CargoFloorPickup, robotSide, GPeiceTarget.kCargo);

        currentState = PickupStates.WaitingForGP;

        break;
      case WaitingForGP:

        System.out.println("GPPPickup: Waiting for GamePeice...");

        // Start Rollers intake
        endEffector.setRollerPercSpeed(-1);
        // Retract Hatch Intake
        endEffector.setSolenoid(SolenoidStates.Open);
        
        if (endEffector.getBallSensor()) {
          // Keep ball in w/ Rollers
          endEffector.setRollerPercSpeed(-0.2);
          // Close solenoid
          // endEffector.setSolenoid(SolenoidStates.Closed);
          currentState = PickupStates.MovingToFinalPosition;
        }

        break;
      case MovingToFinalPosition:

        System.out.println("GPPPickup: Moving to Final Position...");

        RobotSides targetSide = robotSide;

        // Invert target side from pickup side
        switch (robotSide) {
          case kFront:
            targetSide = RobotSides.kRear;
            break;
          case kRear:
            targetSide = RobotSides.kFront;
            break;
        }

        gPosManager.setPosition(GamePositions.CargoShuttle, targetSide, GPeiceTarget.kCargo);

        this.cancel();
        setDirectInterrupt(true);

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
