package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class BasicDriveTrain extends Subsystem {

    public enum ShifterState {Low, High}

    private TalonSRX leftM, leftS, rightM, rightS;

    Solenoid rightShifter;
	Solenoid leftShifter;

    public final double VELOCITY_MAXIMUM = 4800;

    public BasicDriveTrain(int _leftMID, int _rightMID, int _leftSID, int _rightSID,
        int _rightShifterID, int _leftShifterID) {
        leftM = new TalonSRX(_leftMID);
        rightM = new TalonSRX(_rightMID);
        leftS = new TalonSRX(_leftSID);
        rightS = new TalonSRX(_rightSID);

        MotorControllers.TalonSRXConfig(leftM);
        MotorControllers.TalonSRXConfig(rightM);

        leftM.config_kF(0, 0.21);
        rightM.config_kF(0, 0.21);

        leftM.config_kP(0, 0.1604);
        rightM.config_kP(0, 0.1604);

        rightShifter = new Solenoid(_rightShifterID);
		leftShifter = new Solenoid(_leftShifterID);
    }

    public void setPercSpeed(double _leftSpd, double _rightSpd) {
        leftM.set(ControlMode.PercentOutput, _leftSpd);
        rightM.set(ControlMode.PercentOutput, _rightSpd);
        leftS.set(ControlMode.PercentOutput, _leftSpd);
        rightS.set(ControlMode.PercentOutput, _rightSpd);
    }

    public void setVelSpeed(double _leftSpd, double _rightSpd) {
        // If switching from a different mode, set slave followers.
        if (leftM.getControlMode() != ControlMode.Velocity || 
            rightM.getControlMode() != ControlMode.Velocity) {
                leftS.set(ControlMode.Follower, leftM.getDeviceID());
                rightS.set(ControlMode.Follower, rightM.getDeviceID());
        }

        _leftSpd = MathExtra.clamp(_leftSpd, -1, 1) * VELOCITY_MAXIMUM;
        _rightSpd = MathExtra.clamp(_rightSpd, -1, 1) * VELOCITY_MAXIMUM;

        leftM.set(ControlMode.Velocity, _leftSpd);
        rightM.set(ControlMode.Velocity, _rightSpd);
    }

    public void setGearShifters(ShifterState _shiftState) {
        if (_shiftState == ShifterState.High) {
            rightShifter.set(false);
            leftShifter.set(false);
        }
        else {
            rightShifter.set(true);
            leftShifter.set(true);
        }
    }

    public ShifterState getGearShifters() {
        if (rightShifter.get() || leftShifter.get()) {
            return ShifterState.Low;
        }
        else {
            return ShifterState.High;
        }
    }

    @Override
    protected void initDefaultCommand() {

    }
}