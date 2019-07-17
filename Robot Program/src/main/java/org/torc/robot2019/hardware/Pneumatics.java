/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.hardware;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class Pneumatics extends Subsystem {
  
  AnalogInput pressureSensor;

  public Pneumatics(int _pressureSensorID) {
    pressureSensor = new AnalogInput(_pressureSensorID);
  }

  public double getPSI() {
		double voltage = pressureSensor.getVoltage();
		double PSI = 0;
		
		// Get PSI through calculations
		if (voltage < 4.5 && voltage > 0.5) {
			PSI = (voltage * 37.5) - 18.75;
		}
		
		return PSI;
	}

  @Override
  public void initDefaultCommand() {}
  
}