package org.torc.robot2019.program.teleopcontrols;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;
import com.revrobotics.CANSparkMax.IdleMode;

import org.torc.robot2019.subsystems.ElevatorArmManager;
import org.torc.robot2019.commands.GPPickup;
import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.RobotMap;
import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.Controllers;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.subsystems.Elevator;
import org.torc.robot2019.subsystems.EndEffector;
import org.torc.robot2019.subsystems.Elevator.ElevatorPositions;
import org.torc.robot2019.subsystems.EndEffector.EndEffectorPositions;
import org.torc.robot2019.subsystems.EndEffector.SolenoidStates;
import org.torc.robot2019.subsystems.PivotArm;
import org.torc.robot2019.subsystems.PivotArm.PivotArmPositions;
import org.torc.robot2019.subsystems.PivotArm.PivotArmSides;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.LimelightControl;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.LimelightControl.LightMode;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.GPeiceTarget;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.GamePositions;
import org.torc.robot2019.subsystems.gamepositionmanager.GamePositionManager.RobotSides;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopDrive extends CLCommand {

    final double QUICK_TURN_CONSTANT = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN);
	final double QUICK_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_QUICK_TURN_SENSITIVITY);
    final double SPEED_TURN_SENSITIVITY = KMap.GetKNumeric(KNumeric.DBL_SPEED_TURN_SENSITIVITY);
    
    final double ELEVATOR_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_ELEVATOR_JOG_CONTROL_MULTIPLIER);
    final double PIVOTARM_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_JOG_CONTROL_MULTIPLIER);
    final double WRIST_JOG_MULTIPLIER = KMap.GetKNumeric(KNumeric.DBL_WRIST_JOG_CONTROL_MULTIPLIER);

    private BasicDriveTrain driveTrain;

    private PivotArm pivotArm;

    private Climber climber;

    private Elevator elevator;

    private EndEffector endEffector;

    private GamePositionManager gpManager;

    private ElevatorArmManager elevArmManager;

    public static enum ArmSide { kFront, kBack }

    private RobotAutoLevel autoLevelCommand;

    /** Controller inputs for driveline speeds. (0 = left, 1 = right) */
    private double[] driveInput = {0, 0};
    /** Controller inputs for mantis wheel speeds. (0 = left, 1 = right) */
    private double[] mantisWheelInput = {0, 0};

    private GamePositions targetedPosition;
    
    private RobotSides targetedSide;

    private GPeiceTarget targetedGPeice = GPeiceTarget.kHatch; // Default targetedGPeice to Hatch

    private GPPickup pickupCommand;

    private double lastRollerControlVal = 0;

    private boolean lastHatchPanelSensorVal;

    private int currentLevel = 1;

    private boolean prevPressedAutoLevelToggle = false;

    private boolean autoLevelToggled = false;

    private boolean prevPressedAutoLevelNormal = false;

    public TeleopDrive(BasicDriveTrain _driveTrain, GamePositionManager _gpManager,
         PivotArm _pivotArm, Climber _climber, Elevator _elevator, EndEffector _endEffector, 
         ElevatorArmManager _elevArmManager) {

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

        lastHatchPanelSensorVal = endEffector.getHatchPanelSensor();
    }
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        autoLevelCommand.start();
        writeTargetedGPeiceDashboard();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        drivelineMantisControl();
        
        climbControl();

        pivotArmElevatorControl();

        endEffectorControl();

        autoEndEffector();
        
    }

    private void drivelineMantisControl() {
        // Robot driving
        driveInput[0] = MathExtra.applyDeadband(TORCControls.GetInput(ControllerInput.A_DriveLeft), 0.2);
        driveInput[1] = MathExtra.applyDeadband(TORCControls.GetInput(ControllerInput.A_DriveRight), 0.2);
        
        if (TORCControls.GetInput(ControllerInput.B_DivideDriveTrain) >= 0.5) {
            double multiplier = 0.50;//KMap.GetKNumeric(KNumeric.DBL_TELEOP_DRIVE_SLOW_MULTIPLIER);
            driveInput[0] *= multiplier;
            driveInput[1] *= multiplier;
        }
        
        mantisWheelInput[0] = TORCControls.GetInput(ControllerInput.A_MantisLeft);
        mantisWheelInput[1] = TORCControls.GetInput(ControllerInput.A_MantisRight);

        RobotMap.S_Climber.setMantisSpeed(mantisWheelInput[0], mantisWheelInput[1]); 

        // Set drive mode based on if mantis wheels should move or not
        if (mantisWheelInput[0] > 0.2 || mantisWheelInput[1] > 0.2) {
            RobotMap.S_DriveTrain.setPercSpeed(-mantisWheelInput[0] * 0.5, -mantisWheelInput[1] * 0.5);
        }
        // Drive-Controller Drive
        else {
            // Drive the robot
            if (TORCControls.GetInput(ControllerInput.B_EnableVisionCorrection) >= 1) {
                // Turn light on
                LimelightControl.setLedMode(LightMode.eOn);
                LimelightControl.setPipeline(currentLevel - 1);

                double forwardSpeed = MathExtra.clamp(driveInput[0], -0.25, 1);

                double offset = RobotMap.S_VisionCorrector.getOffset();
                RobotMap.S_DriveTrain.setPercSpeed(forwardSpeed - offset, forwardSpeed + offset);
            }
            else {
                // Turn light off
                LimelightControl.setLedMode(LightMode.eOff);
                
                haloDrive(driveInput[0], -driveInput[1], false);
            }
            if (TORCControls.GetInput(ControllerInput.A_OverrideLEDs, InputState.Pressed) >= 1 ) {
                LimelightControl.setLedMode(LightMode.eOn);
            }
        }
    }

    private void climbControl() {
        // Mantis Arm Control
        double mantisPivot = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_MantisArm), 0.2) *
            KMap.GetKNumeric(KNumeric.DBL_MANTIS_ARM_MAX_PERCENT_OUT);
        RobotMap.S_Climber.setMantisPivotSpeed(mantisPivot);

        if (!prevPressedAutoLevelToggle && TORCControls.GetInput(ControllerInput.B_PogoAutoToggle) >= 1) {
            autoLevelToggled = !autoLevelToggled;
        }

        if (prevPressedAutoLevelNormal && !(TORCControls.GetInput(ControllerInput.B_PogoAuto) >= 1)) {
            autoLevelToggled = false;
        }
        
        prevPressedAutoLevelToggle = TORCControls.GetInput(ControllerInput.B_PogoAutoToggle) >= 1;
        prevPressedAutoLevelNormal = TORCControls.GetInput(ControllerInput.B_PogoAuto) >= 1;

        // Pogo sticks auto control
        if (TORCControls.GetInput(ControllerInput.B_PogoAuto) >= 1 || autoLevelToggled) {
            autoLevelCommand.setApplyPID(true);
            // Set brake idle mode
            driveTrain.setIdleMode(IdleMode.kBrake);
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
            pickupCommandInterrupt();
            elevator.jogPosition((int)(elevatorControl * ELEVATOR_JOG_MULTIPLIER));
            //elevArmManager.jogElevator((int)(elevatorControl * ELEVATOR_JOG_MULTIPLIER));
        }

        // Manual pivot jog override //
        double pivotArmControl = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_PivotJogLeft) - 
            TORCControls.GetInput(ControllerInput.A_PivotJogRight), 0.2);
        if (pivotArmControl != 0) {
            pickupCommandInterrupt();
            pivotArm.jogPosition((int)(pivotArmControl * PIVOTARM_JOG_MULTIPLIER));
            //elevArmManager.jogPivotArm((int)(pivotArmControl * PIVOTARM_JOG_MULTIPLIER));
        }

        // CG Pickup command
        
        // Cargo Front Pickup
        if (TORCControls.GetInput(ControllerInput.B_FrontPickupCG, InputState.Pressed) >= 1) {

            System.out.println("Front Pickup Command Pressed!");
            
            pickupCommandInterrupt();
            
            pickupCommand = new GPPickup(gpManager, pivotArm, elevator, endEffector, RobotSides.kFront);
            pickupCommand.start();
        }
        else if (TORCControls.GetInput(ControllerInput.B_RearPickupCG, InputState.Pressed) >= 1) {

            System.out.println("Rear Pickup Command Pressed!");

            pickupCommandInterrupt();
            
            pickupCommand = new GPPickup(gpManager, pivotArm, elevator, endEffector, RobotSides.kRear);
            pickupCommand.start();
        }
        else if (TORCControls.GetInput(ControllerInput.B_PickupHumanPlayer, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.CargoHumanPlayer;
            //targetedGPeice = GPeiceTarget.kHatch;
        }

        // Arm position control //

        // Retracted-up position
        if (TORCControls.GetInput(ControllerInput.B_PivotUp, InputState.Pressed) >= 1) {
            // TODO: Change to GamePosition (with wrist position)?
            pivotArm.setPosition(PivotArmPositions.Up);
            elevator.setPosition(ElevatorPositions.Retracted);
            endEffector.setPosition(EndEffectorPositions.Travel);
        }

        // After Climb-up position
        if (TORCControls.GetInput(ControllerInput.B_PivotTravel, InputState.Pressed) >= 1) {
            // TODO: Make this a variable or something. Corgi.
            /*
            elevArmManager.setPosition(2052, 0);
            
            */
            pivotArm.setPosition(PivotArmPositions.Up);
            elevator.setPosition(ElevatorPositions.Retracted);
            endEffector.setPosition(3210);
        }

        // Up-for-climing position
        if (TORCControls.GetInput(ControllerInput.B_PivotClimbing, InputState.Pressed) >= 1) {
            pivotArm.setPosition(PivotArmPositions.Climbing);
            elevator.setPosition(ElevatorPositions.Retracted);
            endEffector.setPosition(EndEffectorPositions.Climbing);
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
                    writeTargetedGPeiceDashboard();
                    break;
                case kHatch:
                    targetedGPeice = GPeiceTarget.kCargo;
                    writeTargetedGPeiceDashboard();
                    break;
            }
        }

        // Select targetedPosition
        if (TORCControls.GetInput(ControllerInput.B_PivotRocket1, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RL1AndHatchPanelPickup;
            currentLevel = 1;
        }
        else if (TORCControls.GetInput(ControllerInput.B_PivotRocket2, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RocketLevel2;
            currentLevel = 2;
        }
        else if (TORCControls.GetInput(ControllerInput.B_PivotRocket3, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.RocketLevel3;
            currentLevel = 3;
        }
        else if (TORCControls.GetInput(ControllerInput.B_PivotShuttle, InputState.Pressed) >= 1) {
            targetedPosition = GamePositions.CargoShuttle;
        }

        // Flip side selected
        if (TORCControls.GetInput(ControllerInput.B_PivotFlipSelection) >= 1) {
            targetedSide = RobotSides.kRear;
        }

        // If position is selected, set via GamePositionsManager
        if (targetedPosition != null) {
            try {
                pickupCommandInterrupt(); // Inturrupt Pickup command

                System.out.printf("Setting GamePosition:\ntargetedPosition: %s\ntargetedSide: %s\ntargetedGPeice: %s\n",
                    targetedPosition.toString(), targetedSide.toString(), targetedGPeice.toString());
                gpManager.setPosition(targetedPosition, targetedSide, targetedGPeice);
            }
            catch (Exception e) {
                System.err.println("TeleopDrive ElevArmSystem setPosition exception. Not setting position: ");
                e.printStackTrace();
            }
        }

        targetedPosition = null;
        // Default targeted side to front
        targetedSide = RobotSides.kFront;

    }

    private void endEffectorControl() {
        // Manual Wrist Control
        double endEffectorControl = MathExtra.applyDeadband(
            TORCControls.GetInput(ControllerInput.A_WristJog), 0.2);
        if (endEffectorControl != 0) {
            // Determine control position based on current pivotArm side
            if (pivotArm.getPivotArmSide() == PivotArmSides.kRear) {
                endEffectorControl *= -1;
            }
            endEffector.jogPosition((int)(endEffectorControl * WRIST_JOG_MULTIPLIER));
        }

        // Manual Solenoid control
        if (TORCControls.GetInput(ControllerInput.B_OpenWrist) >= 1) {
            endEffector.setSolenoid(SolenoidStates.Open);
        }
        else if (TORCControls.GetInput(ControllerInput.B_CloseWrist) >= 1) {
            endEffector.setSolenoid(SolenoidStates.Closed);
        }

        double rollerControl = TORCControls.GetInput(ControllerInput.B_RollersOuttake) - 
            TORCControls.GetInput(ControllerInput.B_RollersInTake);
        if (rollerControl != 0) {
            pickupCommandInterrupt();
            endEffector.setRollerPercSpeed(rollerControl * 0.75);
        } else {
            endEffector.setRollerPercSpeed(endEffector.getBallSensor() ? -0.2 : 0);
        }

        lastRollerControlVal = rollerControl;

    }

    public void autoEndEffector() {
        if (endEffector.getSolenoid() == SolenoidStates.Closed) {
            // TODO: Determine if the not needs to be flipped
            if (!endEffector.getHatchPanelSensor() && lastHatchPanelSensorVal) {
                // Set Solenoid to be open
                endEffector.setSolenoid(SolenoidStates.Open);
                // Rumble driver & operator controller at half for half a second.
                /*
                new ControllerRumble(TORCControls.GetDriverController(), 0.5, 0.5).start();
                new ControllerRumble(TORCControls.GetOperatorController(), 0.5, 0.5).start();
                */
            }
        }

        lastHatchPanelSensorVal = endEffector.getHatchPanelSensor();
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
    
    public void haloDrive(double _throttle, double _wheel, boolean _squared) {
		
		double driverThrottle = MathExtra.clamp(_throttle, -1, 1);
        double driverWheel = MathExtra.clamp(_wheel, -1, 1);
        
        SmartDashboard.putNumber("Power", driverThrottle);
        SmartDashboard.putNumber("Turn", driverWheel);
		
		if (_squared) {
			driverThrottle = (Math.pow(driverThrottle, 2) * (driverThrottle<0?-1:1));
			driverWheel = (Math.pow(driverWheel, 2) * (driverWheel<0?-1:1));
		}
		
		double rightMotorOutput = 0;
		double leftMotorOutput = 0;

		// Halo Driver Control Algorithm
		if (Math.abs(driverThrottle) < QUICK_TURN_CONSTANT) {
			rightMotorOutput = driverThrottle - (driverWheel * QUICK_TURN_SENSITIVITY);
			leftMotorOutput = driverThrottle + (driverWheel * QUICK_TURN_SENSITIVITY);
		} else {
			rightMotorOutput = driverThrottle - (Math.abs(driverThrottle) * driverWheel * SPEED_TURN_SENSITIVITY);
			leftMotorOutput = driverThrottle + (Math.abs(driverThrottle) * driverWheel * SPEED_TURN_SENSITIVITY);
		}
        // Set drivetrain speed to MotorOutput values
        //driveTrain.setVelSpeed(leftMotorOutput, rightMotorOutput);
        driveTrain.setPercSpeed(leftMotorOutput, rightMotorOutput);
    }

    private void pickupCommandInterrupt() {
        if (pickupCommand != null && pickupCommand.isRunning()) {
            pickupCommand.setDirectInterrupt(true);
            pickupCommand.cancel();
            pickupCommand = null;
        }
        // endEffector.setRollerPercSpeed(0);
    }

    private void writeTargetedGPeiceDashboard() {
        switch (targetedGPeice) {
            case kHatch:
                SmartDashboard.putBoolean("HatchSelect", true);
                SmartDashboard.putBoolean("CargoSelect", false);
                break;
            case kCargo:
                SmartDashboard.putBoolean("HatchSelect", false);
                SmartDashboard.putBoolean("CargoSelect", true);
        }
        
    }
}