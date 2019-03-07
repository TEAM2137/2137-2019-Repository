package org.torc.robot2019.program.teleopcontrols;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.Elevator.ElevatorPositions;
import org.torc.robot2019.subsystems.EndEffector.SolenoidStates;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.Cameras.CameraSelect;
import org.torc.robot2019.subsystems.PivotArm.PivotArmPositions;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.subsystems.GamePositionManager;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.subsystems.GamePositionManager.GPeiceTarget;
import org.torc.robot2019.subsystems.GamePositionManager.GamePositions;
import org.torc.robot2019.subsystems.GamePositionManager.RobotSides;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopDrive extends CLCommand {

    final double QUICK_TURN_CONSTANT = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN);
	final double QUICK_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN_SENSITIVITY);
    final double SPEED_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_SPEED_TURN_SENSITIVITY);
    
    final double ELEVATOR_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_ELEVATOR_JOG_CONTROL_MULTIPLIER);
    final double PIVOTARM_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_PIVOTARM_JOG_CONTROL_MULTIPLIER);
    final double WRIST_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_WRIST_JOG_CONTROL_MULTIPLIER);

    BasicDriveTrain driveTrain;

    PivotArm pivotArm;

    Climber climber;

    Elevator elevator;

    EndEffector endEffector;

    GamePositionManager gpManager;

    ElevatorArmManager elevArmManager;

    public static enum ArmSide { kFront, kBack }

    public ArmSide armSide;

    RobotAutoLevel autoLevelCommand;

    GenericHID operatorController;

    /** Controller inputs for driveline speeds. (0 = left, 1 = right) */
    private double[] driveInput = {0, 0};
    /** Controller inputs for mantis wheel speeds. (0 = left, 1 = right) */
    private double[] mantisWheelInput = {0, 0};

    GamePositions targetedPosition;
    
    RobotSides targetedSide;

    GPeiceTarget targetedGPeice = GPeiceTarget.kHatch; // Default targetedGPeice to Hatch

    public TeleopDrive(BasicDriveTrain _driveTrain, GamePositionManager _gpManager,
         PivotArm _pivotArm, Climber _climber, Elevator _elevator, EndEffector _endEffector, 
         ElevatorArmManager _elevArmManager) {
             ///
             ///
             /// HI GABE, PLEASE INCLUDE THE ELEVARMMANGER IN THE CONSTRUCTOR OF THIS, 
             /// AND KEEP ON WORKING. THANKS. LOVE, ME
             ///
        driveTrain = _driveTrain;

        pivotArm = _pivotArm;

        climber = _climber;

        elevator = _elevator;

        endEffector = _endEffector;

        gpManager = _gpManager;

        elevArmManager = _elevArmManager;

        autoLevelCommand = new RobotAutoLevel(climber, driveTrain);

        requires(driveTrain);
        requires(pivotArm);

        armSide = getPivotArmSide();

        operatorController = TORCControls.GetOperatorController();
    }
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {

        autoLevelCommand.start();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        drivelineMantisControl();
        
        climbControl();

        pivotArmElevatorControl();

        endEffectorControl();
        
    }

    private void drivelineMantisControl() {
        // Robot driving
        driveInput[0] = TORCControls.GetInput(ControllerInput.A_DriveLeft);
        driveInput[1] = TORCControls.GetInput(ControllerInput.A_DriveRight);
        
        mantisWheelInput[0] = TORCControls.GetInput(ControllerInput.A_MantisLeft);
        mantisWheelInput[1] = TORCControls.GetInput(ControllerInput.A_MantisRight);

        RobotMap.S_Climber.setMantisSpeed(mantisWheelInput[0], mantisWheelInput[1]); 

        // Set drive mode based on if mantis wheels should move or not
        if (mantisWheelInput[0] > 0.2 || mantisWheelInput[1] > 0.2) {
            RobotMap.S_DriveTrain.setVelSpeed(-mantisWheelInput[0], mantisWheelInput[1]);
        }
        else {
            // Drive the robot
            haloDrive(driveInput[0], -driveInput[1], false);
        }
        // Camera select
        if (driveInput[0] + driveInput[1] >= 0) {
            RobotMap.S_Cameras.setSelectedCamera(CameraSelect.kFront);
        }
        else {
            RobotMap.S_Cameras.setSelectedCamera(CameraSelect.kRear);
        }
    }

    private void climbControl() {
        // Mantis Arm Control
        double mantisPivot = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_MantisArm), 0.2) *
            KMap.GetKNumeric(KNumeric.DBL_MANTIS_ARM_MAX_PERCENT_OUT);
        RobotMap.S_Climber.setMantisPivotSpeed(mantisPivot);

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

    private void pivotArmElevatorControl() {
        // Manual elevator jog override //
        double elevatorControl = MathExtra.applyDeadband(
            -TORCControls.GetInput(ControllerInput.A_ElevatorJog), 0.2);
        if (elevatorControl != 0) {
            elevator.jogPosition((int)(elevatorControl * ELEVATOR_JOG_MULTIPLIER));
            //elevArmManager.jogElevator((int)(elevatorControl * ELEVATOR_JOG_MULTIPLIER));
        }

        // Manual pivot jog override //
        double pivotArmControl = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_PivotJogLeft) - 
            TORCControls.GetInput(ControllerInput.A_PivotJogRight), 0.2);
        if (pivotArmControl != 0) {
            pivotArm.jogPosition((int)(pivotArmControl * PIVOTARM_JOG_MULTIPLIER));
            //elevArmManager.jogPivotArm((int)(pivotArmControl * PIVOTARM_JOG_MULTIPLIER));
        }

        // Arm position control //

        // Retracted-up position
        if (TORCControls.GetInput(ControllerInput.B_PivotUp, InputState.Pressed) >= 1) {
            // TODO: Change to GamePosition (with wrist position)?
            pivotArm.setPosition(PivotArmPositions.Up);
            elevator.setPosition(ElevatorPositions.Retracted);
        }

        // Invert gamepeice target
        if (TORCControls.GetInput(ControllerInput.B_ToggleGPeice, InputState.Pressed) >= 1) {
            // In case targetedGPeice is not set
            if (targetedGPeice == null) {
                targetedGPeice = GPeiceTarget.kCargo;
            }
            switch (targetedGPeice) {
                case kCargo:
                    targetedGPeice = GPeiceTarget.kHatch;
                    break;
                case kHatch:
                    targetedGPeice = GPeiceTarget.kCargo;
                    break;
            }
        }

        // Select targetedPosition
        if (TORCControls.GetInput(ControllerInput.B_PivotRocket1, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RocketLevel1;
        }
        else if (TORCControls.GetInput(ControllerInput.B_PivotRocket2, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RocketLevel2;
        }
        else if (TORCControls.GetInput(ControllerInput.B_PivotRocket3, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RocketLevel3;
        }

        // Flip side selected
        if (TORCControls.GetInput(ControllerInput.B_PivotFlipSelection) >= 1) {
            targetedSide = RobotSides.kFront;
        }

        // If position is selected, set via GamePositionsManager
        if (targetedPosition != null) {
            System.out.printf("Setting GamePosition:\ntargetedPosition: %s\ntargetedSide: %s\ntargetedGPeice: %s\n",
                targetedPosition.toString(), targetedSide.toString(), targetedGPeice.toString());
            gpManager.setPosition(targetedPosition, targetedSide, targetedGPeice);
        }

        targetedPosition = null;
        targetedSide = RobotSides.kRear;

    }

    private void endEffectorControl() {
        double endEffectorControl = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_WristJog), 0.2);

        if (endEffectorControl != 0) {
            endEffector.jogPosition((int)(endEffectorControl * WRIST_JOG_MULTIPLIER));
        }

        if (TORCControls.GetInput(ControllerInput.B_OpenWrist) >= 1) {
            endEffector.setSolenoid(SolenoidStates.Open);
        }
        else if (TORCControls.GetInput(ControllerInput.B_CloseWrist) >= 1) {
            endEffector.setSolenoid(SolenoidStates.Closed);
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