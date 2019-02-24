package org.torc.robot2019.program;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * A singleton class which manages and maintains robot-wide constant values
 * which are dependent on different robot builds (Practice and Competition).
 * (KMap stands for "ConstantMap")
 */
public class KMap {

    public static enum KNumeric {
        // TYPE_NAME(practiceVal, competitionVal)

        /* Initilization IDs */
        // Pnumatics
        INT_PNEUMATICS_PSI_SENSOR_ID(0, 0),
        // Drivetrain
        INT_DRIVETRAIN_LEFT_MASTER_ID(10, 10),
        INT_DRIVETRAIN_RIGHT_MASTER_ID(11, 11),
        INT_DRIVETRAIN_LEFT_SLAVE_0_ID(12, 12),
        INT_DRIVETRAIN_RIGHT_SLAVE_0_ID(13, 13),
        INT_DRIVETRAIN_LEFT_SLAVE_1_ID(14, 14),
        INT_DRIVETRAIN_RIGHT_SLAVE_1_ID(15, 15),
        INT_DRIVETRAIN_PIGEON_ID(4, 4),
        // Climber
        INT_CLIMBER_MANTIS_LEFT_MOTOR_ID(41, 41),
        INT_CLIMBER_MANTIS_RIGHT_MOTOR_ID(42, 42),
        INT_CLIMBER_MANTIS_PIVOT_MOTOR_ID(40, 40),
        INT_CLIMBER_POGO_MOTOR_ID(43, 43),
        // Pivot Arm
        INT_PIVOT_ARM_MASTER_ID(20, 20),
        // Elevator
        INT_ELEVATOR_MOTOR_ID(21, 21),
        INT_ELEVATOR_ENDSTOP_ID(0, 0),

        // Teleop Drive
        DBL_QUICK_TURN(0.3, 0.3),
        DBL_QUICK_TURN_SENSITIVITY(0.7, 0.7),
        DBL_SPEED_TURN_SENSITIVITY(0.7, 0.7),
        DBL_MANTIS_ARM_MAX_PERCENT_OUT(0.5, 0.5),
        // Elevator
        INT_ELEVATOR_MAX_POSITION(19100, 19100),
        // PivotArm
        DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_FORWARD(0.5, 0.5),
        DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_REVERSE(-0.5, -0.5),
        INT_PIVOT_ARM_ENCODER_OFFSET(1077, 1077),
        INT_PIVOT_ARM_ALLOWABLE_ERROR(60, 60),
        INT_PIVOT_ARM_360_DEGREE_RESOLUTION(4096, 4096),
        ;

        double practiceValue;
        double competitionValue;

        KNumeric(double _practice, double _competition) {
            practiceValue = _practice;
            competitionValue = _competition;
        }
    }

    public static enum KString {
        // TYPE_NAME(practiceVal, competitionVal)
        ;

        String practiceValue;
        String competitionValue;

        KString(String _practice, String _competition) {
            practiceValue = _practice;
            competitionValue = _competition;
        }
    }

    private static final int DIGITAL_PORT_NUM = 9;

    private static KMap single_instance = null; // Singleton variable

    public static enum RobotType {Practice, Competition};

	private RobotType CurrentRobotType;

	private DigitalInput jumper;
	
	private KMap(int _jumperPort) {
		jumper = new DigitalInput(_jumperPort);
		
		CurrentRobotType = jumper.get() ? RobotType.Competition : RobotType.Practice;
	}
    
    public static KMap GetInstance() {
        if (single_instance == null) {
            single_instance = new KMap(DIGITAL_PORT_NUM);
        }
        return single_instance;
    }

	public static RobotType GetRobotType() {
		return GetInstance().CurrentRobotType;
    }
    
    // The following is dirty, dirty code. Right now I can't figure out how to make it
    // cleaner, so this is what you get right now.
    public static double GetKNumeric(KNumeric _constant) {
        RobotType CurrentRobotType = GetRobotType();

        if (CurrentRobotType == RobotType.Competition) {
            return _constant.competitionValue;
        }
        else if (CurrentRobotType == RobotType.Practice) {
            return _constant.practiceValue;
        }
        else {
            System.out.printf("GetKNumeric: Warning! RobotType \"%s\" not an " +
            "implemented RobotType!!. Returning comp value...", CurrentRobotType.name());
            return _constant.competitionValue;
        }
    }
    public static String GetKString(KString _constant) {
        RobotType CurrentRobotType = GetRobotType();
        
        if (CurrentRobotType == RobotType.Competition) {
            return _constant.competitionValue;
        }
        else if (CurrentRobotType == RobotType.Practice) {
            return _constant.practiceValue;
        }
        else {
            System.out.printf("GetKString: Warning! RobotType \"%s\" not an " +
            "implemented RobotType!!. Returning comp value...", CurrentRobotType.name());
            return _constant.competitionValue;
        }
    }
}