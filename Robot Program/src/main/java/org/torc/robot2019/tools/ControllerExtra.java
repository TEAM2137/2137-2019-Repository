package org.torc.robot2019.tools;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class ControllerExtra {
    /**
     * Quick Method for setting both of a GenericHID's rumble motors.
     * @param _controller GenericHID controller.
     * @param _value Double normalized value (0-1) to rumble
     */
    public static void SetDualRumble(GenericHID _controller, double _value) {
        _controller.setRumble(RumbleType.kLeftRumble, _value);
        _controller.setRumble(RumbleType.kRightRumble, _value);
    }
}