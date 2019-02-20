package org.torc.robot2019.program;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

public class TORCControls {
    /*
     * Declare currently-used controllers up here!
     * (Make sure you define them inline!)
     */
    private static GenericHID driverController = new XboxController(0);

    public static enum ControllerInput {
		/**Left Drivetrain Control Axis*/
		A_DriveLeft(1, driverController, InputType.Axis), // Left stick Y axis
		/**Right Drivetrain Control Axis*/
        A_DriveRight(4, driverController, InputType.Axis), // Right stick X axis
        /**Mantis-Arm Left Drivewheel*/
        A_MantisLeft(2, driverController, InputType.Axis), // Left Trigger
        /**Mantis-Arm Right Drivewheel*/
        A_MantisRight(3, driverController, InputType.Axis), // Right Trigger
        /**Mantis-Arm Pivot*/
        A_MantisArm(1, driverController, InputType.Axis), // Left stick Y axis
        /**Pogo-Sticks Control*/
        A_PogoControl(5, driverController, InputType.Axis), // Right stick Y axis
        /**Flip PivotArm side-selction*/
        B_PivotFlipSelection(270, driverController, InputType.POV), // Left POV
        /**PivotArm Up position*/
        B_PivotUp(4, driverController, InputType.Button), // Y button
        /**PivotArm Low position*/
        B_PivotLow(1, driverController, InputType.Button), // A button
        /**Pivot Arm Up position*/
        B_PivotHigh(4, driverController, InputType.Button), // Y button
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

    //TODO: get rid of this function and implement it correctly
    public static GenericHID GetDriverController() {
        return driverController;
    }
}