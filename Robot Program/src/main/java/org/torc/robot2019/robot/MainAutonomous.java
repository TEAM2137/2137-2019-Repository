
package org.torc.robot2019.robot;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;
import org.torc.robot2019.program.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
@Autonomous(name = "Main Autonomous")
public class MainAutonomous extends OpMode {
    
    /**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
    @Override
    public void INIT() {
		AutonMode.Init();
    }
    
    /**
	 * This function is called periodically during autonomous
	 */
    @Override
    public void LOOP() {
        AutonMode.Periodic();
    }
}
