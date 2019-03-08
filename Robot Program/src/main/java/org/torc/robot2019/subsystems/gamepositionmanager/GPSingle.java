package org.torc.robot2019.subsystems.gamepositionmanager;

/** Defines a singular game position, 
 * used mostly by the GamePositionManager class. */
public class GPSingle {

    private int pivotArmPosition, elevatorPosition, wristPosition;

    /** GPSingle Constructor.
     * 
     * @param _pivotArmPosition
     * @param _elevatorPosition
     * @param _wristPosition
     */
    public GPSingle(int _pivotArmPosition, int _elevatorPosition, int _wristPosition) {
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