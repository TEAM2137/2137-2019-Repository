package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // elevator, pivot, wrist
         CargoFloorPickup(new GPSingle(0, 982, 3031), // Cargo Only
                     new GPSingle(0, 982, 3031),
                     new GPSingle(0, 3018, 1455),
                     new GPSingle(0, 3018, 1455)),

             CargoShuttle(new GPSingle(2114, 1694, 4771), // Cargo Only
                     new GPSingle(2114, 1694, 4771),
                     new GPSingle(2866, 2382, 0),
                     new GPSingle(2866, 2382, 0)),
   /*                 
   HumanPlayerPickup(new GPSingle(0, 1079, 2668), // Front only
                     new GPSingle(0, 1541, 3000),
                     new GPSingle(0, 1079, 2668),
                     new GPSingle(0, 1541, 3000)),
   */

    CargoHumanPlayer(new GPSingle(0, 1209, 850), // Cargo Only
                     new GPSingle(0, 1209, 850),
                     new GPSingle(0, 2890, 4291),
                     new GPSingle(0, 2890, 4291)),

RL1AndHatchPanelPickup(new GPSingle(0, 805, 2234),//new GPSingle(0, 1061, 2314), // Front HatchPanel
                     new GPSingle(0, 1266, 1768),    // Front Cargo
                     new GPSingle(9, 2778, 2165), // Rear HatchPanel
                     new GPSingle(0, 2788, 2086)),     // Rear Cargo

        RocketLevel2(new GPSingle(25, 1916, 1092), // Front HatchPanel
                     new GPSingle(7267, 1942, 1082), // Front Cargo
                     new GPSingle(7455, 2100, 2837), // Rear HatchPanel
                     new GPSingle(7791, 2008, 3084)), // Rear Cargo

        RocketLevel3(new GPSingle(13393, 1953, 1049), // Front HatchPanel
                     new GPSingle(17915, 1953, 1223), // Front Cargo
                     new GPSingle(18277, 1985, 2890), // Rear HatchPanel
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