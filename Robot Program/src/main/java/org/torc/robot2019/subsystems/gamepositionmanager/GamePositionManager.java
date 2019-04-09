package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // elevator, pivot, wrist
    CargoFloorPickup(new GPSingle(0, 929, 1711), // Cargo Only
                     new GPSingle(0, 929, 1711),
                     new GPSingle(0, 3034, 2443),
                     new GPSingle(0, 3034, 2443)),

        CargoShuttle(new GPSingle(506, 1965, 1095), // Cargo Only
                     new GPSingle(506, 1965, 1095),
                     new GPSingle(217, 1978, 2993),
                     new GPSingle(217, 1978, 2993)),

   /*                 
   HumanPlayerPickup(new GPSingle(0, 1079, 2668), // Front only
                     new GPSingle(0, 1541, 3000),
                     new GPSingle(0, 1079, 2668),
                     new GPSingle(0, 1541, 3000)),
   */

    CargoHumanPlayer(new GPSingle(0, 1031, 1420), // Cargo Only
                     new GPSingle(0, 1031, 1420),
                     new GPSingle(0, 2928, 2790),
                     new GPSingle(0, 2928, 2790)),

RL1AndHatchPanelPickup(new GPSingle(0, 801, 2200),//new GPSingle(0, 1061, 2314), // Front HatchPanel
                     new GPSingle(0, 1276, 1831),    // Front Cargo
                     new GPSingle(9, 2817, 2087), // Rear HatchPanel
                     new GPSingle(0, 2743, 2236)),     // Rear Cargo

        RocketLevel2(new GPSingle(539, 1916, 1184), // Front HatchPanel
                     new GPSingle(6284, 1947, 1299), // Front Cargo
                     new GPSingle(6732, 2100, 2799), // Rear HatchPanel
                     new GPSingle(7791, 2008, 3084)), // Rear Cargo

        RocketLevel3(new GPSingle(13393, 1953, 1192), // Front HatchPanel
                     new GPSingle(17915, 1953, 1271), // Front Cargo
                     new GPSingle(18666, 1985, 2930), // Rear HatchPanel
                     new GPSingle(18666, 2008, 2924)), // Rear Cargo
                     
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