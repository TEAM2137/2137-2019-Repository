/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.program.teleopcontrols;

import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.subsystems.Climber;
import org.torc.robot2019.tools.CLCommand;

public class RobotAutoLevel extends CLCommand {

  Climber climber;
  BasicDriveTrain driveTrain;

  private static final double TARGET_ANGLE = 6;

  private double errSum = 0;
	private double dLastPos = 0;

  double kFF = 0;
  double kP = 0.08;
  double kI = 0;
  double kD = 0;

  boolean applyPIDLast = false;
  boolean applyPID = false;

  public RobotAutoLevel(Climber _climber, BasicDriveTrain _driveTrain) {
    climber = _climber;
    driveTrain = _driveTrain;

    requires(climber);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {

    double currentAngle = driveTrain.getGyroYPR()[2];

    double err = TARGET_ANGLE + currentAngle; // TODO: Correct this

    // Add to error sum for Integral
    errSum += err;

    double offset = (kP * err) + (errSum * kI) + (kD * (currentAngle - dLastPos)) + (kFF);

    // Drive pogos to correct
    if (applyPID) {
      climber.setPogoStickSpeed(offset);
    }
    if (!applyPID && applyPIDLast) {
      climber.setPogoStickSpeed(0);
    }
    
    dLastPos = currentAngle;

    applyPIDLast = applyPID;
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }

  public void setApplyPID(boolean _apply) {
    applyPID = _apply;
  }
}
