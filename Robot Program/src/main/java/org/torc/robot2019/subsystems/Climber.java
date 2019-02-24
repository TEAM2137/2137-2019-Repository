package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.KMap.KNumeric;
import org.torc.robot2019.tools.MathExtra;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {

	public static enum MantisSide {
		kRight, kLeft
	}

	private static final double MANTIS_ARM_MAX_PERC = KMap.GetKNumeric(
		KNumeric.DBL_MANTIS_ARM_MAX_PERCENT_OUT);

	/** Motor Controller for the Mantis arm */
	private CANSparkMax rightMantis, leftMantis;
	private CANSparkMax pivotMantis;
	private CANSparkMax pogoStick;

	public Climber(int _leftManID, int _rightManID, int _pivotManID, int _pogoStickID) {
		rightMantis = new CANSparkMax(_rightManID, MotorType.kBrushless);
		leftMantis = new CANSparkMax(_leftManID, MotorType.kBrushless);

		pivotMantis = new CANSparkMax(_pivotManID, MotorType.kBrushless);

		pogoStick = new CANSparkMax(_pogoStickID, MotorType.kBrushless);
	}

	public void setMantisSpeed(double _speed) {
		setMantisSpeed(_speed, -_speed);
	}
	public void setMantisSpeed(double _leftSpeed, double _rightSpeed) {
		leftMantis.set(-_leftSpeed);
		rightMantis.set(_rightSpeed);
	}
	public void setMantisSpeed(double _speed, MantisSide _side) {
		switch (_side) {
			case kRight:
				rightMantis.set(_speed);
				break;
			case kLeft:
				leftMantis.set(_speed);
				break;
		}
	}

	public void setMantisPivotSpeed(double _speed) {
		// Clamp Mantis arm to max speed
		_speed = MathExtra.clamp(_speed, -MANTIS_ARM_MAX_PERC, MANTIS_ARM_MAX_PERC);
		pivotMantis.set(_speed);
	}

	public void setPogoStickSpeed(double _speed) {
		pogoStick.set(_speed);
	}

	public void initDefaultCommand() {
	}
}
