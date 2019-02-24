/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Robot;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PivotArm extends Subsystem implements InheritedPeriodic {

  public static enum PivotArmPositions { 
    /** 
     * Used for when robot should be driving around,
     * with no need to use the pivot arm.
     */
    Up(2048),
    // Front-side positions
    PickupF(0),
    Level1F(0),
    Level2F(0),
    Level3F(0),
    // Rear-side positions
    PickupR(0),
    Level1R(0),
    Level2R(0),
    Level3R(0),
    // McBride
    HorizontalF(AngleToPosition(90)),
    HorizontalR(AngleToPosition(270))
    ;

    private int positionValue;

    PivotArmPositions(int _positionValue) {
      positionValue = _positionValue;
    }
  }

  private static final double ARM_360_RESOLUTION_MULTIPLIER = 
    KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_360_DEGREE_RESOLUTION) / 360;

  TalonSRX m_armPivot;

  public PivotArm(int _armPivotID) {
    // Add this class to InheritedPeriodic list
    Robot.AddToPeriodic(this);

    m_armPivot = new TalonSRX(_armPivotID);

    MotorControllers.TalonSRXConfig(m_armPivot);

    // Limit ArmPivot to a max current
    m_armPivot.configContinuousCurrentLimit(5);

    // Limit maximum output speed
    double maxOutputForward = 
      KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_FORWARD);
    double maxOutputReverse = 
      KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_REVERSE);
    m_armPivot.configPeakOutputForward(maxOutputForward, 0);
    m_armPivot.configPeakOutputReverse(maxOutputReverse, 0);

    m_armPivot.config_kF(0, 0);
    m_armPivot.config_kP(0, 8);
    m_armPivot.config_kI(0, 0.01);
    m_armPivot.config_kD(0, 300);
    m_armPivot.config_IntegralZone(0, 40);

    int absolutePosition = m_armPivot.getSensorCollection().getPulseWidthPosition();
    absolutePosition += KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_ENCODER_OFFSET);
		absolutePosition &= 0xFFF;// Mask out overflows, keep bottom 12 bits
    //TODO: Set timeoutMS argument here to be a constant
    m_armPivot.setSelectedSensorPosition(absolutePosition, 0, 10);
  }

  public static int GetPivotArmPositionsValue(PivotArmPositions _position) {
    return _position.positionValue;
  }

  public void setPercSpeed(double _speed) {
    m_armPivot.set(ControlMode.PercentOutput, _speed);
  }

  public void setRawPosition(int _position) {
    configNominalRange(false);
    m_armPivot.set(ControlMode.Position, _position);
  }

  public void setAnglePosition(double _angle) {
    setRawPosition(AngleToPosition(_angle));
  }

  public void setPosition(PivotArmPositions _position) {
    switch (_position) {
      case Up:
        configNominalRange(true);
        break;
      default:
        configNominalRange(false);
        break;
    }
    m_armPivot.set(ControlMode.Position, GetPivotArmPositionsValue(_position));
  }

  public int getEncoder() {
    return m_armPivot.getSelectedSensorPosition();
  }

  public static int AngleToPosition(double _angle) {
    return (int)(_angle * ARM_360_RESOLUTION_MULTIPLIER);
  }

  public static double PositionToAngle(int _position) {
    return _position / ARM_360_RESOLUTION_MULTIPLIER;
  }

  private void configNominalRange(boolean _enable) {
    if (_enable) {
      m_armPivot.configAllowableClosedloopError(0, 
        (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_ALLOWABLE_ERROR));
    }
    else {
      m_armPivot.configAllowableClosedloopError(0, 0);
    }
  }

  @Override
  public void Periodic() {
    SmartDashboard.putNumber("ArmCorrectedEncoder", getEncoder());
    SmartDashboard.putNumber("ArmPositionError", m_armPivot.getClosedLoopTarget() - getEncoder());
  }

  @Override
  public void initDefaultCommand() {
  }
}
