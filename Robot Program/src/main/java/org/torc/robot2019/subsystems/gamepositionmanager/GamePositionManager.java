package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // elevator, pivot, wrist
    CargoFloorPickup(new GPSingle(0, 987, 1676), // Cargo Only
                     new GPSingle(0, 987, 1676),
                     new GPSingle(387, 3022, 2546),
                     new GPSingle(387, 3022, 2546)),

        CargoShuttle(new GPSingle(5334, 1694, 993), // Cargo Only
                     new GPSingle(5334, 1694, 993),
                     new GPSingle(4315, 2224, 3125),
                     new GPSingle(4315, 2224, 3125)),
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

RL1AndHatchPanelPickup(new GPSingle(0, 776, 2274),//new GPSingle(0, 1061, 2314), // Front HatchPanel
                     new GPSingle(0, 1266, 1910),    // Front Cargo
                     new GPSingle(9, 2887, 2000), // Rear HatchPanel
                     new GPSingle(0, 2788, 2179)),     // Rear Cargo

        RocketLevel2(new GPSingle(539, 1916, 1237), // Front HatchPanel
                     new GPSingle(7267, 1942, 1232), // Front Cargo
                     new GPSingle(6732, 2100, 2872), // Rear HatchPanel
                     new GPSingle(7791, 2008, 3084)), // Rear Cargo

        RocketLevel3(new GPSingle(13393, 1953, 1192), // Front HatchPanel
                     new GPSingle(17915, 1953, 1271), // Front Cargo
                     new GPSingle(18666, 1985, 2930), // Rear HatchPanel
                     new GPSingle(18666, 2008, 2963)), // Rear Cargo
                     
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