package org.torc.robot2019.subsystems;

import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // Values are in CARGO position
        RocketLevel1(3072, 0, 0,
                     1024, 0, 0),

        RocketLevel2(2872, 5000, 0,
                     1224, 5000, 0),

        RocketLevel3(2672, 14900, 0,
                     1424, 14900, 0),
        ;

        private int pivotArmPositionF, elevatorPositionF, wristPositionF,
                    pivotArmPositionR, elevatorPositionR, wristPositionR;
        ;
        GamePositions(int _pivotArmPositionF, int _elevatorPositionF, int _wristPositionF,
                    int _pivotArmPositionR, int _elevatorPositionR, int _wristPositionR) {

            pivotArmPositionF = _pivotArmPositionF;
            elevatorPositionF = _elevatorPositionF;
            wristPositionF = _wristPositionF;
            pivotArmPositionR = _pivotArmPositionR;
            elevatorPositionR = _elevatorPositionR;
            wristPositionR = _wristPositionR;
        }
    }

    public static enum RobotSides {
        kFront, kRear;
    }

    public static enum GPeiceTarget {
        kHatch, kCargo
    }

    private static final int HatchPanelOffset = 3; // TODO: Get this value, and implement it correctly

    private PivotArm pivotArm;
    private Elevator elevator;
    private EndEffector endEffector;

    public GamePositionManager(PivotArm _pivotArm, Elevator _elevator, EndEffector _endEffector) {
        pivotArm = _pivotArm;
        elevator = _elevator;
        endEffector = _endEffector;
    }

    public void setPosition(GamePositions _gamePosition, RobotSides _robotSide, GPeiceTarget _GPeiceTarget) {
        int pivotArmTarget = 0;
        int elevatorTarget = 0;
        int wristTarget = 0;

        switch (_robotSide) {
            case kFront:
                pivotArmTarget = _gamePosition.pivotArmPositionF;
                elevatorTarget = _gamePosition.elevatorPositionF;
                wristTarget = _gamePosition.wristPositionF;
                break;
            case kRear:
                pivotArmTarget = _gamePosition.pivotArmPositionR;
                elevatorTarget = _gamePosition.elevatorPositionR;
                wristTarget = _gamePosition.wristPositionR;
                break;
            default:
                System.out.println("GamePositionManager.setPosition(): Robot Side not yet implemented!");
                return;
        }

        // Finally, set target positions on all 3 subsystems
        pivotArm.setRawPosition(pivotArmTarget);
        elevator.setPosition(elevatorTarget);
        endEffector.setPosition(wristTarget);
    }

    @Override
    protected void initDefaultCommand() {
    }
}