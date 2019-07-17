package org.torc.robot2019.functions;

//This Holds all of the objects
public class RobotConfig {

    public enum RobotBases {
        DEEP_SPACE_2019, POWER_UP_2018, STEAM_WORKS_2017
    }

    public enum AllianceColors {
        RED, BLUE, TEST
    }

    public enum StartPosition {
        LEFT, RIGHT, CENTER, TEST
    }

    public enum StepState {
        STATE_INIT, STATE_RUNNING, STATE_FINISHED, STATE_COMPLETED, STATE_INTERUPIED, STATE_TIMEOUT, STATE_ERROR
    }

    public double DRIVE_GEAR_REDUCTION      = 0;
    public double WHEEL_DIAMETER_INCHES     = 0;
    public double WHEEL_ACTUAL_ERROR        = 0;
    public double REV_PER_INCH              = 0;
    public double ROBOT_TRACK               = 0;
    public double WHEEL_TURN_ERROR          = 0;
    public double REV_PER_DEGREE            = 0;

    public AllianceColors allianceColor     = AllianceColors.TEST;
    public StartPosition startPosition      = StartPosition.TEST;

    public RobotConfig() {
        // Put your code here
    }

    public void loadConfig(RobotBases base) {
        switch (base) {
            case DEEP_SPACE_2019:
                DRIVE_GEAR_REDUCTION = 0.7;
                WHEEL_DIAMETER_INCHES = 4.0;
                WHEEL_ACTUAL_ERROR = 1;
                REV_PER_INCH = (DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)) * WHEEL_ACTUAL_ERROR;
                ROBOT_TRACK = 16.5;
                WHEEL_TURN_ERROR = 1.0;
                REV_PER_DEGREE = (((2 * 3.1415 * ROBOT_TRACK) * REV_PER_INCH) / 360) * WHEEL_TURN_ERROR;
                break;

            case POWER_UP_2018:
                DRIVE_GEAR_REDUCTION = 0.7;
                WHEEL_DIAMETER_INCHES = 4.0;
                WHEEL_ACTUAL_ERROR = 1;
                REV_PER_INCH = (DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)) * WHEEL_ACTUAL_ERROR;
                ROBOT_TRACK = 16.5;
                WHEEL_TURN_ERROR = 1.0;
                REV_PER_DEGREE = (((2 * 3.1415 * ROBOT_TRACK) * REV_PER_INCH) / 360) * WHEEL_TURN_ERROR;
                break;

            case STEAM_WORKS_2017:
                DRIVE_GEAR_REDUCTION = 0.7;
                WHEEL_DIAMETER_INCHES = 4.0;
                WHEEL_ACTUAL_ERROR = 1;
                REV_PER_INCH = (DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)) * WHEEL_ACTUAL_ERROR;
                ROBOT_TRACK = 16.5;
                WHEEL_TURN_ERROR = 1.0;
                REV_PER_DEGREE = (((2 * 3.1415 * ROBOT_TRACK) * REV_PER_INCH) / 360) * WHEEL_TURN_ERROR;
                break;
        }
    }
}