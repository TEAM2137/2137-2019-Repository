package org.torc.robot2019.program.teleopcontrols;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.MathExtra;

public class TeleopDrive extends CLCommand {

    final double QUICK_TURN_CONSTANT = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN);
	final double QUICK_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN_SENSITIVITY);
	final double SPEED_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_SPEED_TURN_SENSITIVITY);

    BasicDriveTrain driveTrain;

    public TeleopDrive(BasicDriveTrain _driveTrain) {
        driveTrain = _driveTrain;

        requires(driveTrain);
    }
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        haloDrive(RobotMap.Controls.getInput(ControllerInput.A_DriveLeft), 
            -RobotMap.Controls.getInput(ControllerInput.A_DriveRight), false);
        
        /*
        if (RobotMap.Controls.getInput(ControllerInput.A_DriveLeft) < -0.5) {
            System.out.println("Is less than!!");
            RobotMap.S_DriveTrain.setVelTarget(400, 400);
        }
        */
        

        /*

        double driveLeft = RobotMap.Controls.getInput(ControllerInput.A_DriveLeft);
        double driveRight = RobotMap.Controls.getInput(ControllerInput.A_DriveRight);

        RobotMap.S_DriveTrain.setPercSpeed(driveLeft, driveRight);
        */
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
    
    public void haloDrive(double _wheel, double _throttle, boolean _squared) {
		
		double driverThrottle = MathExtra.clamp(MathExtra.applyDeadband(_throttle, 0.15), -1, 1);
		double driverWheel = MathExtra.clamp(MathExtra.applyDeadband(_wheel, 0.15), -1, 1);
		
		if (_squared) {
			driverThrottle = (Math.pow(driverThrottle, 2) * (driverThrottle<0?-1:1));
			driverWheel = (Math.pow(driverWheel, 2) * (driverWheel<0?-1:1));
		}
		
		double rightMotorOutput = 0;
		double leftMotorOutput = 0;

		// Halo Driver Control Algorithm
		if (Math.abs(driverThrottle) < QUICK_TURN_CONSTANT) {
			rightMotorOutput = driverThrottle - driverWheel * QUICK_TURN_SENSITIVITY;
			leftMotorOutput = driverThrottle + driverWheel * QUICK_TURN_SENSITIVITY;
		} else {
			rightMotorOutput = driverThrottle - Math.abs(driverThrottle) * driverWheel * SPEED_TURN_SENSITIVITY;
			leftMotorOutput = driverThrottle + Math.abs(driverThrottle) * driverWheel * SPEED_TURN_SENSITIVITY;
		}
        
		// If low gear, drive percVBus
		//if (driveTrain.getGearShifters() == ShifterState.Low) {
			//driveTrain.setPercSpeed(-leftMotorOutput, rightMotorOutput);
		//}
        // High gear: drive velocity
        
		//else {
			driveTrain.setVelSpeed(leftMotorOutput, rightMotorOutput);
        //}
        //*/
		
	}
}