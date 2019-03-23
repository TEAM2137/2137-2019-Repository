/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.tools;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class SmartDashboardExtra {

    public static void EnsureBoolean(String _key, boolean _valueToSet) {
        while (!SmartDashboard.getBoolean(_key, false)) {
            SmartDashboard.putBoolean(_key, true);
        }
        
        SmartDashboard.putBoolean(_key, _valueToSet);
    }
}
