/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class ManagedArmElevator extends Subsystem {

  public static enum ArmElevatorPositions {
    fdsa(3,3)
    ;

    private double armPosition;
    private int elevatorPosition;
    
    ArmElevatorPositions(double _armPosition, int _elevatorPosition) {
      armPosition = _armPosition;
      elevatorPosition = _elevatorPosition;
    }
  }

  private PivotArm pivotArm;
  private Elevator elevator;

  public ManagedArmElevator(PivotArm _pivotArm, Elevator _elevator) {
    pivotArm = _pivotArm;
    elevator = _elevator;
  }

  public void setPosition(ArmElevatorPositions _position) {

  }

  @Override
  public void initDefaultCommand() {
  }
}
