package org.torc.robot2019.program;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * A Class which is meant to find and store if the robot is to use a Practice, 
 * or Competition configuration.
 */
public class RobotInfo {
	public static enum RobotType {Practice, Competition};

	private RobotType robotType;

	DigitalInput jumper;
	
	/**
	 * Contructor for RobotInfo.
	 * @param jumperPort Digital Input port which is going to be searched for a jumper.
	 */
	public RobotInfo(int _jumperPort) {
		jumper = new DigitalInput(_jumperPort);
		
		robotType = jumper.get() ? RobotType.Competition : RobotType.Practice;
	}
	
	public RobotType getRobotType() {
		return robotType;
	}
}
