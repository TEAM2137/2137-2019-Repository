package org.torc.robot2019.program.teleopcontrols;

import com.ctre.phoenix.CANifier;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.PivotArm.PivotArmPositions;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.MathExtra;

import edu.wpi.first.wpilibj.GenericHID;

public class TeleopDrive extends CLCommand {

    public static CANifier canTool;

    boolean oneShot = false;

    final double QUICK_TURN_CONSTANT = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN);
	final double QUICK_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN_SENSITIVITY);
	final double SPEED_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_SPEED_TURN_SENSITIVITY);

    BasicDriveTrain driveTrain;

    private static GenericHID driversController;

    public TeleopDrive(BasicDriveTrain _driveTrain) {
        driveTrain = _driveTrain;

        requires(driveTrain);

        driversController = TORCControls.GetDriverController();
    }
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        canTool = new CANifier(3);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        
        haloDrive(TORCControls.GetInput(ControllerInput.A_DriveLeft), 
            -TORCControls.GetInput(ControllerInput.A_DriveRight), false);
        
        if (driversController.getRawButtonPressed(7)) { // Select Button???
			System.out.println("Attempting to home elevator!");
			RobotMap.S_Elevator.homeElevator();
        }
        
        /*
        // Left Y Axis
        double axisVal = MathExtra.applyDeadband(
            RobotMap.Controls.getDriverController().getRawAxis(1), 0.3);
        RobotMap.S_Elevator.setPercSpeed(axisVal);
        */

		if (driversController.getRawButton(1)) { // A button
            RobotMap.S_Elevator.setPosition(0);
            RobotMap.S_PivotArm.setRawPosition(1000);
		}
		else if (driversController.getRawButton(2)) { // B button
            RobotMap.S_Elevator.setPosition(10000);
            RobotMap.S_PivotArm.setRawPosition(1711);
		}
		else if (driversController.getRawButton(3)) { // X Button
            RobotMap.S_Elevator.setPosition(0);
            RobotMap.S_PivotArm.setPosition(PivotArmPositions.Up);
            
        }
        else if (driversController.getRawButton(4)) { // Y Button
            RobotMap.S_Elevator.setPosition(19000);
            RobotMap.S_PivotArm.setRawPosition(2164);
        }
        
        if (driversController.getRawButton(9) && oneShot == false){
            if (driversController.getRawButtonReleased(9)){
            oneShot = true;
            }
          } else if (driversController.getRawButton(9) && oneShot == true){
            if (driversController.getRawButtonReleased(9)){
            oneShot = false;
            }
          }
        if (oneShot == true){
            canTool.setLEDOutput(1.0, CANifier.LEDChannel.LEDChannelA);
            canTool.setLEDOutput(0.65, CANifier.LEDChannel.LEDChannelB);
            canTool.setLEDOutput(0.0, CANifier.LEDChannel.LEDChannelC);
        } else {
            canTool.setLEDOutput(1.0, CANifier.LEDChannel.LEDChannelA);
            canTool.setLEDOutput(1.0, CANifier.LEDChannel.LEDChannelB);
            canTool.setLEDOutput(1.0, CANifier.LEDChannel.LEDChannelC);
        }
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
        // Set drivetrain speed to MotorOutput values
        driveTrain.setVelSpeed(leftMotorOutput, rightMotorOutput);
	}
}