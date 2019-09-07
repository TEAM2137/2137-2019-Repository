package org.torc.robot2019.subsystems.gamepositionmanager;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.gamepositionmanager.GPSingle;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // elevator, pivot, wrist
    CargoFloorPickup(new GPSingle(0, 1024, 1603), // Front Cargo 2580
                     new GPSingle(0, 1024, 1603), // Front Cargo 2580
                     new GPSingle(767, 3117, 2510), // Rear Cargo 1538
                     new GPSingle(767, 3117, 2510)),// Rear Cargo 1538

        CargoShuttle(new GPSingle(3563, 1965, 886), // Front Cargo 3210
                     new GPSingle(3563, 1965, 886), // Front Cargo 3210
                     new GPSingle(1639, 2041, 2937), // Rear Cargo 1159
                     new GPSingle(1639, 2041, 2937)),// Rear Cargo 1159

    CargoHumanPlayer(new GPSingle(0, 0, 0), // Front Cargo
                     new GPSingle(0, 0, 0), // Front Cargo
                     new GPSingle(0, 0, 0), // Rear Cargo
                     new GPSingle(0, 0, 0)),// Rear Cargo

RL1AndHatchPanelPickup(
                     new GPSingle(0, 823, 2267), // Front HatchPanel 1905
                     new GPSingle(0, 1369, 1796), // Front Cargo 2300
                     new GPSingle(0, 2812, 2199), // Rear HatchPanel 1897
                     new GPSingle(0, 2829, 2194)),// Rear Cargo 1902

        RocketLevel2(new GPSingle(921, 1916, 1143), // Front HatchPanel 2953
                     new GPSingle(7885, 1998, 1105), // Front Cargo 2991
                     new GPSingle(6804, 2100, 2869), // Rear HatchPanel 1227
                     new GPSingle(6637, 2084, 2917)),// Rear Cargo 1179

        RocketLevel3(new GPSingle(12381, 2041, 1159), // Front HatchPanel 3075
                     new GPSingle(18666, 2027, 1295), // Front Cargo 2905
                     new GPSingle(18666, 2094, 2770), // Rear HatchPanel 1326
                     new GPSingle(18666, 2074, 2865)),// Rear Cargo 1231
                     
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