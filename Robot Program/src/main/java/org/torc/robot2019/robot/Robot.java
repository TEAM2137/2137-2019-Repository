
package org.torc.robot2019.robot;

import java.util.ArrayList;

import org.torc.robot2019.program.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	Command autonomousCommand;
	
	private static ArrayList<InheritedPeriodic> PeriodicList = new ArrayList<InheritedPeriodic>();

	public static void AddToPeriodic(InheritedPeriodic toAdd) {
		PeriodicList.add(toAdd);
	}
	
	/*
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		RobotMode.Init();
	}
	
	@Override
	public void robotPeriodic() {
		// Manually call the Scheduler "run" command.
		Scheduler.getInstance().run();
		// Call all perodic funtions in PeriodicList
		for(InheritedPeriodic per : PeriodicList) {
			per.Periodic();
		}
		RobotMode.Periodic();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		DisabledMode.Init();
	}

	@Override
	public void disabledPeriodic() {
		DisabledMode.Periodic();
	}

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
	public void autonomousInit() {
		AutonMode.Init();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		/* Must be called often to have commands function properly in a non-
		 * command based robot.
		 */
		//Scheduler.getInstance().run();
		
		AutonMode.Periodic();
	}

	@Override
	public void teleopInit() {
		TeleopMode.Init();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		/* Must be called often to have commands function properly in a non-
		 * command based robot.
		 */
		//Scheduler.getInstance().run();
		
		TeleopMode.Periodic();
	}

	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		/* Must be called often to have commands function properly in a non-
		 * command based robot.
		 */
		//Scheduler.getInstance().run();
		
		TestMode.Periodic();
	}
	
	@Override
	public void testInit() {
		TestMode.Init();
	}
	
}
