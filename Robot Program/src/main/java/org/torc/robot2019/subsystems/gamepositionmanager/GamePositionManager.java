package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // elevator, pivot, wrist
    CargoFloorPickup(new GPSingle(0, 974, 2455), // Front Cargo
                     new GPSingle(0, 974, 2455), // Front Cargo
                     new GPSingle(768, 3065, 1577), // Rear Cargo
                     new GPSingle(768, 3065, 1577)),// Rear Cargo

        CargoShuttle(new GPSingle(3563, 1965, 3210), // Front Cargo
                     new GPSingle(2978, 1965, 3210), // Front Cargo
                     new GPSingle(2913, 2123, 1179), // Rear Cargo
                     new GPSingle(2913, 2123, 1179)),// Rear Cargo

    CargoHumanPlayer(new GPSingle(0, 0, 0), // Front Cargo
                     new GPSingle(0, 0, 0), // Front Cargo
                     new GPSingle(0, 0, 0), // Rear Cargo
                     new GPSingle(0, 0, 0)),// Rear Cargo

RL1AndHatchPanelPickup(
                     new GPSingle(0, 781, 1868), // Front HatchPanel
                     new GPSingle(0, 1319, 2300), // Front Cargo
                     new GPSingle(0, 2812, 1897), // Rear HatchPanel
                     new GPSingle(0, 2829, 1902)),// Rear Cargo

        RocketLevel2(new GPSingle(921, 1916, 2953), // Front HatchPanel
                     new GPSingle(7885, 1998, 2991), // Front Cargo
                     new GPSingle(6804, 2100, 1227), // Rear HatchPanel
                     new GPSingle(6637, 2084, 1179)),// Rear Cargo

        RocketLevel3(new GPSingle(13494, 2041, 3075), // Front HatchPanel
                     new GPSingle(18666, 2027, 2905), // Front Cargo
                     new GPSingle(18666, 2094, 1326), // Rear HatchPanel
                     new GPSingle(18666, 2074, 1231)),// Rear Cargo
                     
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