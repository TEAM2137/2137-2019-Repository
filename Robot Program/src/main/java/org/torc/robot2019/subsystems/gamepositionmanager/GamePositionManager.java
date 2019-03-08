package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // Pivot, elevator, wrist
            CGPickup(new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0)),
                     
            HPPickup(new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0)),

             Shuttle(new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0),
                     new GPSingle(0, 0, 0)),

        RocketLevel1(new GPSingle(1024, 0, 2050), // Front HatchPanel
                     new GPSingle(0, 0, 0),    // Front Cargo
                     new GPSingle(3072, 0, 0), // Rear HatchPanel
                     new GPSingle(0, 0, 0)),     // Rear Cargo

        RocketLevel2(new GPSingle(2002, 3971, 5435),
                     new GPSingle(0, 0, 0), 
                     new GPSingle(2872, 5000, 0),
                     new GPSingle(0, 0, 0)),

        RocketLevel3(new GPSingle(1424, 14900, 0),
                     new GPSingle(0, 0, 0), 
                     new GPSingle(2672, 14900, 0),
                     new GPSingle(0, 0, 0)),
        ;

        private GPSingle GP_F_HP, GP_F_CG, GP_R_HP, GP_R_CG;

        GamePositions(GPSingle _GP_F_HP, GPSingle _GP_F_CG, 
            GPSingle _GP_R_HP, GPSingle _GP_R_CG) {

                GP_F_HP = _GP_F_HP;
                GP_F_CG = _GP_F_CG;
                GP_R_HP = _GP_R_HP;
                GP_R_CG = _GP_R_CG;
        }
    }

    public static enum RobotSides {
        kFront, kRear;
    }

    public static enum GPeiceTarget {
        kHatch, kCargo
    }

    public static final GPSingle GP_ZERO = new GPSingle(0, 0, 0);

    private EndEffector endEffector;
    private ElevatorArmManager elevArmManager;

    public GamePositionManager(EndEffector _endEffector, ElevatorArmManager _elevArmManager) {
        endEffector = _endEffector;
        elevArmManager = _elevArmManager;
    }

    public GPSingle setPosition(GamePositions _gamePosition, RobotSides _robotSide, GPeiceTarget _GPeiceTarget) {

        GPSingle targetGPSingle = GP_ZERO;

        switch (_robotSide) {
            case kFront:
                switch (_GPeiceTarget) {
                    case kHatch:
                        targetGPSingle = _gamePosition.GP_F_HP;
                        break;
                    case kCargo:
                        targetGPSingle = _gamePosition.GP_F_CG;
                        break;
                }
                break;
            case kRear:
                switch (_GPeiceTarget) {
                    case kHatch:
                        targetGPSingle = _gamePosition.GP_R_HP;
                        break;
                    case kCargo:
                        targetGPSingle = _gamePosition.GP_R_CG;
                        break;
                }
                break;
            default:
                System.out.println("GamePositionManager.setPosition(): Robot Side not yet implemented!");
                return GP_ZERO;
        }

        // Finally, set target positions on all 3 subsystems
        /*
        pivotArm.setPosition(targetGPSingle.getPivotArm());
        elevator.setPosition(targetGPSingle.getElevator());
        */
        elevArmManager.setPosition(targetGPSingle.getPivotArm(), targetGPSingle.getElevator());
        endEffector.setPosition(targetGPSingle.getWrist());

        return targetGPSingle;
    }

    @Override
    protected void initDefaultCommand() {
    }
}