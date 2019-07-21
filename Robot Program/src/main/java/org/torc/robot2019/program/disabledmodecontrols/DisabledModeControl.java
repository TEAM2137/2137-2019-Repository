/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.program.disabledmodecontrols;

import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.Controllers;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.functions.CLCommand;
import org.torc.robot2019.functions.LimelightControl;
import org.torc.robot2019.functions.LimelightControl.LightMode;

public class DisabledModeControl extends CLCommand {
  public DisabledModeControl() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //So this is primarially for just limelight calibration and aiding in that, by adding a turn on the LED button
    //Left on the drive controller D-Pad

    if (TORCControls.GetInput(ControllerInput.A_OverrideLEDs, InputState.Pressed) >= 1 ) {
      LimelightControl.setLedMode(LightMode.eOn);
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
}
