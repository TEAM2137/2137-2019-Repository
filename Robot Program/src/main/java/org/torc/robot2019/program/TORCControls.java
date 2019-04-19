package org.torc.robot2019.program;

import org.torc.robot2019.commands.ControllerRumble;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class TORCControls {

    /*
     * Declare currently-used controllers up here!
     * (Make sure you define them inline!)
     */
    private static GenericHID driverController = new XboxController(0);
    private static GenericHID operatorController = new XboxController(1);
    private static GenericHID climberController = new XboxController(2);

    /**
     * An Enum used for references to specfic controllers from outside classes.
     */
    public static enum Controllers {
        kDriver(driverController), 
        kOperator(operatorController), 
        kClimber(climberController);

        GenericHID controller;

        private Controllers(GenericHID _controller) {
            controller = _controller;
        }
    }

    public static enum ControllerInput {
		/**Left Drivetrain Control Axis*/
		A_DriveLeft(1, driverController, InputType.Axis), // Left stick Y axis
		/**Right Drivetrain Control Axis*/
        A_DriveRight(4, driverController, InputType.Axis), // Right stick X axis

        /**Mantis-Arm Pivot*/
        A_MantisArm(1, climberController, InputType.Axis), // Left stick Y axis

        A_MantisLeft(2, climberController, InputType.Axis), // Left trigger
        A_MantisRight(3, climberController, InputType.Axis), // Right trigger

        /**Pogo-Sticks Manual Control*/
        A_PogoControl(5, climberController, InputType.Axis), // Right stick Y axis
        /**Pogo-Sticks Auto-adjust enable*/
        B_PogoAuto(10, climberController, InputType.Button), // Right stick push-in

        /**Elevator Manual Up-Down Jog */
        A_ElevatorJog(1, operatorController, InputType.Axis),

        /**Flip PivotArm side-selction*/
        B_PivotFlipSelection(7, operatorController, InputType.Button),//270, operatorController, InputType.POV), // Left POV // Back Button
        /**PivotArm Up position*/
        B_PivotUp(90, operatorController, InputType.POV), // Right POV
        /**Cargo Front Pickup position */
        B_FrontPickupCG(1, driverController, InputType.Button), // A button
        /**Cargo Rear Pickup position */
        B_RearPickupCG(2, driverController, InputType.Button), // B button
        /**Human Player Pickup position */
        B_PickupHumanPlayer(0, operatorController, InputType.POV), // Up POV
        /**PivotArm shuttle position*/
        B_PivotShuttle(3, operatorController, InputType.Button), // X button
        /**PivotArm 1st Rocket position*/
        B_PivotRocket1(1, operatorController, InputType.Button), // A button
        /**PivotArm 2nd Rocket position*/
        B_PivotRocket2(2, operatorController, InputType.Button), // B button
        /**PivotArm 3rd Rocket position*/
        B_PivotRocket3(4, operatorController, InputType.Button), // Y button
        /**Game Peice target toggle*/
        B_ToggleGPeice(8, operatorController, InputType.Button), // Start button

        A_PivotJogLeft(2, operatorController, InputType.Axis), // Left Trigger
        A_PivotJogRight(3, operatorController, InputType.Axis), // Right Trigger

        A_WristJog(5, operatorController, InputType.Axis), // Right stick Y

        B_OpenWrist(5, operatorController, InputType.Button), // Left bumper
        B_CloseWrist(6, operatorController, InputType.Button), // Right bumper

        B_RollersOuttake(5, driverController, InputType.Button), // Left bumper
        B_RollersInTake(6, driverController, InputType.Button), // Right bumper

        B_PivotClimbing(1, climberController, InputType.Button),  // A Button
        B_PivotTravel(4, climberController, InputType.Button), // Y Button

        B_DivideDriveTrain(3, driverController, InputType.Axis),
        /**Vision auto-correction enable*/ 
        B_EnableVisionCorrection(10, driverController, InputType.Button), // Right Stick 

        /**Front Camera select */
        B_SelectCameraFront(0, driverController, InputType.POV), // Up POV
        /**Rear Camera select */
        B_SelectCameraRear(180, driverController, InputType.POV), // Down POV
        
        /**PivotArm Intake position*/
        //B_PivotHorizontal(1, driverController, InputType.Button), // A button
        ;
        
        private int id;
        private GenericHID controller;
        private InputType iType;

		ControllerInput(int _id, GenericHID _controller, InputType _iType) {
            this.id = _id;
            this.controller = _controller;
            this.iType = _iType;
        }
    }
    
    public static enum InputType {
        Axis, Button, POV
    }

    public static enum InputState {
        Raw, Pressed, Released
    }

    /**
     * Returns the appropriate value of the requested ControllerInput.
     * @param _cInput Requested ControllerInput.
     * @param _iState Requested InputState of the ControllerInput.
     * @return Double value of retrieved Input (1.0 for true, 0.0 for false).
     */
    public static double GetInput(ControllerInput _cInput, InputState _iState) {

        double retVal = 0; // Value to return

        switch (_cInput.iType) {
            case Button: // If requested input is a button
                switch (_iState) {
                    case Raw:
                        retVal = BoolToDouble(_cInput.controller.getRawButton(_cInput.id));
                        break;
                    case Pressed:
                        retVal = BoolToDouble(_cInput.controller.getRawButtonPressed(_cInput.id));
                        break;
                    case Released:
                        retVal = BoolToDouble(_cInput.controller.getRawButtonReleased(_cInput.id));
                }
                break;
            case Axis: // If requested input is an axis
                retVal = _cInput.controller.getRawAxis(_cInput.id);
                //System.out.printf("Axis: %d value: %f\n", _cInput.id, retVal);
                break;
            case POV:
                retVal = BoolToDouble(_cInput.controller.getPOV() == _cInput.id);
                break;
            default:
                System.out.println("getInput: Not an implemented InputType for provided ControllerInput!");
                break;
        }

        return retVal; // Return value
    }

    public static double GetInput(ControllerInput _cInput) {
        return GetInput(_cInput, InputState.Raw);
    }

    /**
	 * Retrives the GenericHID Input ID of the provided ControllerInput.
	 * @param _input
	 * @return GenericHID ID of the selected ControllerInput.
	 */
	public static int GetIID(ControllerInput _input) {
		return _input.id;
    }

    /**
     * Convert a boolean value to a double, C-style!
     * (true = 1.0, false = 0.0)
     * @param _value
     * @return
     */
    public static double BoolToDouble(boolean _value) {
        return _value ? 1.0 : 0.0;
    }

    /**
     * Convert a double value to a boolean, C-style!
     * (>=1.0 = true, <1.0 = false)
     * @param _value
     * @return
     */
    public static boolean DoubleToBool(double _value) {
        return (_value >= 1.0);
    }

    public static void SetControllerRumble(Controllers _controller, double _val) {
        _controller.controller.setRumble(RumbleType.kLeftRumble, _val);
        _controller.controller.setRumble(RumbleType.kRightRumble, _val);
    }

    public static void SetControllerRumbleTime(Controllers _controller, double _val, double _time) {
        new ControllerRumble(_controller.controller, _val, _time).start();
    }
}