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

        DBL_QUICK_TURN(0.3, 0.3),
        DBL_QUICK_TURN_SENSITIVITY(0.7, 0.7),
        DBL_SPEED_TURN_SENSITIVITY(0.7, 0.7),
        DBL_MANTIS_ARM_MAX_PERCENT_OUT(0.5, 0.5),
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

	public RobotType getRobotType() {
		return CurrentRobotType;
    }
    
    // The following is dirty, dirty code. Right now I can't figure out how to make it
    // cleaner, so this is what you get right now.
    public static double GetKNumeric(KNumeric _constant) {
        RobotType CurrentRobotType = GetInstance().getRobotType();

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
        RobotType CurrentRobotType = GetInstance().getRobotType();
        
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