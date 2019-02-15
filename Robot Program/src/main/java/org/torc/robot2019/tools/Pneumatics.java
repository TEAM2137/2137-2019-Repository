package org.torc.robot2019.tools;

import edu.wpi.first.wpilibj.AnalogInput;

public class Pneumatics {

	public static double getPSIFromAnalog (AnalogInput input) {
		
		double voltage = input.getVoltage();
		double PSI = 0;
		
		// Get PSI through calculations
		if (voltage < 4.5 && voltage > 0.5) {
			PSI = (voltage * 37.5) - 18.75;
		}
		
		return PSI;
	}
	
}
