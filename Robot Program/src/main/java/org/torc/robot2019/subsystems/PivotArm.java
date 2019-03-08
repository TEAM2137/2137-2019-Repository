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
import org.torc.robot2019.tools.MathExtra;
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
    // Rear-side positions
    PickupR(824),
    Level1R(1024),
    Level2R(1224),
    Level3R(1424),
    // Front-side positions
    PickupF(3272),
    Level1F(3072),
    Level2F(2872),
    Level3F(2672),
    // McBride
    HorizontalR(1024),
    HorizontalF(3072),
    ;

    private int positionValue;

    PivotArmPositions(int _positionValue) {
      positionValue = _positionValue;
    }
  }

  private static final double ARM_360_RESOLUTION_MULTIPLIER = 
    KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_360_DEGREE_RESOLUTION) / 360;

  private static final int ARM_MIN_POSITION = 
    (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_MIN_POSITION);
  private static final int ARM_MAX_POSITION = 
    (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_MAX_POSITION);

  private static final int JOG_ERROR_CUTOFF = 
    (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_JOG_ERROR_CUTOFF);

  TalonSRX m_armPivot;

  private int targetPosition = 0;

  public PivotArm(int _armPivotID) {
    // Add this class to InheritedPeriodic list
    Robot.AddToPeriodic(this);

    m_armPivot = new TalonSRX(_armPivotID);

    MotorControllers.TalonSRXConfig(m_armPivot);

    // Limit ArmPivot to a max current
    m_armPivot.configContinuousCurrentLimit(10);

    // Limit maximum output speed
    double maxOutputForward = 
      KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_FORWARD);
    double maxOutputReverse = 
      KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_MAX_PERCENT_OUTPUT_REVERSE);
    m_armPivot.configPeakOutputForward(maxOutputForward, 0);
    m_armPivot.configPeakOutputReverse(maxOutputReverse, 0);

    m_armPivot.config_kF(0, 0);
    m_armPivot.config_kP(0, KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_KP));
    m_armPivot.config_kI(0, KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_KI));
    m_armPivot.config_kD(0, KMap.GetKNumeric(KNumeric.DBL_PIVOT_ARM_KD));
    m_armPivot.config_IntegralZone(0, (int)KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_KIZONE));

    m_armPivot.configAllowableClosedloopError(0, 0);

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

  public void setPosition(int _position) {
    targetPosition = MathExtra.clamp(_position, ARM_MIN_POSITION, ARM_MAX_POSITION);
    m_armPivot.set(ControlMode.Position, targetPosition);
  }

  public void setAnglePosition(double _angle) {
    setPosition(AngleToPosition(_angle));
  }

  public void setPosition(PivotArmPositions _position) {
    switch (_position) {
      case Level1R:
        System.out.println("Pivot: Setting kF to 0.2");
        m_armPivot.config_kF(0, 0.2);
        break;
      case Level1F:
        System.out.println("Pivot: Setting kF to -0.2");
        m_armPivot.config_kF(0, -0.2);
        break;
      default:
        m_armPivot.config_kF(0, 0);
        break;
    }
    setPosition(GetPivotArmPositionsValue(_position));
  }

  public void jogPosition(int _positionInc) {
    /* 
		* If error is too big, set elevatorTargetPosition to encoder count so jogging is 
		* instantanious
		*/
    if (Math.abs(targetPosition - getEncoder()) > JOG_ERROR_CUTOFF) {
      targetPosition = getEncoder();
    }
    
    setPosition(targetPosition += _positionInc);
  }

  public int getTargetPosition() {
    return targetPosition;
  }

  public boolean isAtTarget() {
		return MathExtra.InRange(getEncoder(), targetPosition, 
			KMap.GetKNumeric(KNumeric.INT_PIVOT_ARM_RANGE_WITHIN_TARGET));
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

  @Override
  public void Periodic() {
    SmartDashboard.putNumber("ArmRawEncoder", m_armPivot.getSensorCollection().getPulseWidthPosition());
    SmartDashboard.putNumber("ArmCorrectedEncoder", getEncoder());
    //SmartDashboard.putNumber("ArmPositionError", m_armPivot.getClosedLoopTarget() - getEncoder());
    SmartDashboard.putNumber("ArmTargetPosition", targetPosition);
  }

  @Override
  public void initDefaultCommand() {
  }
}
