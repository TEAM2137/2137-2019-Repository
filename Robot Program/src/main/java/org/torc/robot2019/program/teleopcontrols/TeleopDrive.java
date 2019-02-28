package org.torc.robot2019.program.teleopcontrols;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.Elevator.ElevatorPositions;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.PivotArm.PivotArmPositions;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.MathExtra;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopDrive extends CLCommand {

    final double QUICK_TURN_CONSTANT = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN);
	final double QUICK_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN_SENSITIVITY);
	final double SPEED_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_SPEED_TURN_SENSITIVITY);

    BasicDriveTrain driveTrain;

    PivotArm pivotArm;

    Climber climber;

    Elevator elevator;

    public static enum ArmSide { kFront, kBack }

    public ArmSide armSide;

    RobotAutoLevel autoLevelCommand;

    public TeleopDrive(BasicDriveTrain _driveTrain, PivotArm _pivotArm, Climber _climber, Elevator _elevator) {
        driveTrain = _driveTrain;

        pivotArm = _pivotArm;

        climber = _climber;

        elevator = _elevator;

        autoLevelCommand = new RobotAutoLevel(climber, driveTrain);

        requires(driveTrain);
        requires(pivotArm);

        armSide = getPivotArmSide();
    }
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        autoLevelCommand.start();
        //SmartDashboard.putNumber("velDrive", 0);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        double driveLeft = TORCControls.GetInput(ControllerInput.A_DriveLeft);
        double driveRight = TORCControls.GetInput(ControllerInput.A_DriveRight);
        
        double mantisLeft = TORCControls.GetInput(ControllerInput.A_MantisLeft);
        double mantisRight = TORCControls.GetInput(ControllerInput.A_MantisRight);

        RobotMap.S_Climber.setMantisSpeed(mantisLeft, mantisRight);

        // Set drive mode based on if mantis wheels should move or not
        if (mantisLeft > 0.2 || mantisRight > 0.2) {
            RobotMap.S_DriveTrain.setVelSpeed(-mantisLeft, mantisRight);
        }
        else {
            // Drive the robot
            haloDrive(driveLeft, -driveRight, false);
        }
        
        // Mantis Arm Control
        double mantisPivot = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_MantisArm), 0.2) *
            KMap.GetKNumeric(KNumeric.DBL_MANTIS_ARM_MAX_PERCENT_OUT);
        RobotMap.S_Climber.setMantisPivotSpeed(mantisPivot);

        // Arm position control
        if (TORCControls.GetInput(ControllerInput.B_PivotUp, InputState.Pressed) >= 1) {
            pivotArm.setPosition(PivotArmPositions.Up);
            elevator.setPosition(ElevatorPositions.Retracted);
        }

        if (TORCControls.GetInput(ControllerInput.B_PivotFlipSelection) >= 1) {
            if (TORCControls.GetInput(ControllerInput.B_PivotHorizontal, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.HorizontalF);
                elevator.setPosition(ElevatorPositions.Retracted);
            }
            /*
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket1, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level1F);
            }
            */
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket2, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level2F);
                elevator.setPosition(ElevatorPositions.Level2);
            }
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket3, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level3F);
                elevator.setPosition(ElevatorPositions.Level3);
            }
        }
        else {
            if (TORCControls.GetInput(ControllerInput.B_PivotHorizontal, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.HorizontalR);
                elevator.setPosition(ElevatorPositions.Retracted);
            }
            /*
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket1, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level1R);
            }
            */
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket2, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level2R);
                elevator.setPosition(ElevatorPositions.Level2);
            }
            else if (TORCControls.GetInput(ControllerInput.B_PivotRocket3, InputState.Pressed) >= 1) {
                pivotArm.setPosition(PivotArmPositions.Level3R);
                elevator.setPosition(ElevatorPositions.Level3);
            }
        }

        // Pogo sticks auto control
        if (TORCControls.GetInput(ControllerInput.B_PogoAuto) >= 1) {
            autoLevelCommand.setApplyPID(true);
        }
        else {
            autoLevelCommand.setApplyPID(false);
            double pogoSpeed = MathExtra.applyDeadband(
			    TORCControls.GetInput(ControllerInput.A_PogoControl), 0.2);
		    RobotMap.S_Climber.setPogoStickSpeed(pogoSpeed);
        }
        
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        autoLevelCommand.cancel();
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
    
    public ArmSide getPivotArmSide() {
        // Left side
        if (PivotArm.PositionToAngle(pivotArm.getEncoder()) <= 180) {
            return ArmSide.kFront;
        }
        // Right side
        else {
            return ArmSide.kBack;
        }
    }
}