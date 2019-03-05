package org.torc.robot2019.subsystems;

import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.PivotArm;

import edu.wpi.first.wpilibj.command.Subsystem;

public class GamePositionManager extends Subsystem {

    public static enum GamePositions {
        // Values are in CARGO position
        RocketLevel1(new GPInternal(3072, 0, 0), // Front HatchPanel
                     new GPInternal(0, 0, 0),    // Front Cargo
                     new GPInternal(1024, 0, 0), // Rear HatchPanel
                     new GPInternal(0, 0, 0)),     // Rear Cargo

        RocketLevel2(new GPInternal(2872, 5000, 0),
                     new GPInternal(0, 0, 0), 
                     new GPInternal(1224, 5000, 0),
                     new GPInternal(0, 0, 0)),

        RocketLevel3(new GPInternal(2672, 14900, 0),
                     new GPInternal(0, 0, 0), 
                     new GPInternal(1424, 14900, 0),
                     new GPInternal(0, 0, 0)),
        ;

        private GPInternal GP_F_HP, GP_F_CG, GP_R_HP, GP_R_CG;

        GamePositions(GPInternal _GP_F_HP, GPInternal _GP_F_CG, 
            GPInternal _GP_R_HP, GPInternal _GP_R_CG) {

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

    public static final GPInternal GP_ZERO = new GPInternal(0, 0, 0);

    private PivotArm pivotArm;
    private Elevator elevator;
    private EndEffector endEffector;

    public GamePositionManager(PivotArm _pivotArm, Elevator _elevator, EndEffector _endEffector) {
        pivotArm = _pivotArm;
        elevator = _elevator;
        endEffector = _endEffector;
    }

    public void setPosition(GamePositions _gamePosition, RobotSides _robotSide, GPeiceTarget _GPeiceTarget) {

        GPInternal targetGPInternal = GP_ZERO;

        switch (_robotSide) {
            case kFront:
                switch (_GPeiceTarget) {
                    case kHatch:
                        targetGPInternal = _gamePosition.GP_F_HP;
                        break;
                    case kCargo:
                        targetGPInternal = _gamePosition.GP_F_CG;
                        break;
                }
                break;
            case kRear:
                switch (_GPeiceTarget) {
                    case kHatch:
                        targetGPInternal = _gamePosition.GP_R_HP;
                        break;
                    case kCargo:
                        targetGPInternal = _gamePosition.GP_R_CG;
                        break;
                }
                break;
            default:
                System.out.println("GamePositionManager.setPosition(): Robot Side not yet implemented!");
                return;
        }

        // Finally, set target positions on all 3 subsystems
        pivotArm.setPosition(targetGPInternal.getPivotArm());
        elevator.setPosition(targetGPInternal.getElevator());
        endEffector.setPosition(targetGPInternal.getWrist());
    }

    @Override
    protected void initDefaultCommand() {
    }
}
/** Defines a singular game position, 
 * used internally by the GamePositionManager class. */
class GPInternal {

    private int pivotArmPosition, elevatorPosition, wristPosition;

    /** GPInternal Constructor.
     * 
     * @param _pivotArmPosition
     * @param _elevatorPosition
     * @param _wristPosition
     */
    public GPInternal(int _pivotArmPosition, int _elevatorPosition, int _wristPosition) {
        pivotArmPosition = _pivotArmPosition;
        elevatorPosition = _elevatorPosition;
        wristPosition = _wristPosition;
    }

    public int getPivotArm() {
        return pivotArmPosition;
    }

    public int getElevator() {
        return elevatorPosition;
    }

    public int getWrist() {
        return wristPosition;
    }
}