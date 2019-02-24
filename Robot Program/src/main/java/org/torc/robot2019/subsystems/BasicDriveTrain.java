package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Robot;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BasicDriveTrain extends Subsystem implements InheritedPeriodic {

    public static enum DriveSide { kRight, kLeft };

    private TalonSRX leftM, rightM;

    private VictorSPX[] leftS = new VictorSPX[2];
    private VictorSPX[] rightS = new VictorSPX[2];

    private PigeonIMU gyro;

    public final double VELOCITY_MAXIMUM = 440;

    public BasicDriveTrain(int _leftMID, int _rightMID, int _leftS0ID, int _rightS0ID,
        int _leftS1ID, int _rightS1ID, int _pigeonID) {
        // "Subscribe" to inherited Periodic
        Robot.AddToPeriodic(this);

        leftM = new TalonSRX(_leftMID);
        rightM = new TalonSRX(_rightMID);

        leftS[0] = new VictorSPX(_leftS0ID);
        rightS[0] = new VictorSPX(_rightS0ID);
        leftS[1] = new VictorSPX(_leftS1ID);
        rightS[1] = new VictorSPX(_rightS1ID);

        MotorControllers.TalonSRXConfig(leftM);
        MotorControllers.TalonSRXConfig(rightM);

        // Configure masters to use quad encoders
        leftM.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        rightM.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

        gyro = new PigeonIMU(_pigeonID);

        // Invert left so it goes forwards with right (same phase)
        leftM.setSensorPhase(true);

        leftM.config_kF(0, 2.2);
        rightM.config_kF(0, 2.2);

        leftM.config_kP(0, 3);
        rightM.config_kP(0, 3);

        leftM.config_kI(0, 0.05);
        rightM.config_kI(0, 0.05);

        leftM.configClosedloopRamp(0.25);
        rightM.configClosedloopRamp(0.25);

        leftM.config_IntegralZone(0, 30);
        rightM.config_IntegralZone(0, 30);
    }

    public void setPercSpeed(double _leftSpd, double _rightSpd) {
        leftM.set(ControlMode.PercentOutput, -_leftSpd);
        rightM.set(ControlMode.PercentOutput, _rightSpd);

        leftS[0].set(ControlMode.PercentOutput, -_leftSpd);
        rightS[0].set(ControlMode.PercentOutput, _rightSpd);
        leftS[1].set(ControlMode.PercentOutput, -_leftSpd);
        rightS[1].set(ControlMode.PercentOutput, _rightSpd);
    }

    public void setVelSpeed(double _leftSpd, double _rightSpd) {
        // If switching from a different mode, set slave followers.
        checkSlavesToFollow();

        _leftSpd = -MathExtra.clamp(_leftSpd, -1, 1) * VELOCITY_MAXIMUM;
        _rightSpd = MathExtra.clamp(_rightSpd, -1, 1) * VELOCITY_MAXIMUM;

        leftM.set(ControlMode.Velocity, _leftSpd);
        rightM.set(ControlMode.Velocity, _rightSpd);
    }

    public void setVelTarget(double _leftTarget, double _rightTarget) {
        // If switching from a different mode, set slave followers.
        checkSlavesToFollow();

        leftM.set(ControlMode.Velocity, -_leftTarget);
        rightM.set(ControlMode.Velocity, _rightTarget);
    }

    public int getDriveEncoder(DriveSide _driveSide) {
        int retVal = 0;

        switch (_driveSide) {
            case kRight:
                retVal = rightM.getSelectedSensorPosition(0);
                break;
            case kLeft: 
                retVal = leftM.getSelectedSensorPosition(0);
                break;
        }

        return retVal;
    }

    public void resetDriveEncoder(DriveSide _driveSide) {
        switch (_driveSide) {
            case kRight:
                rightM.getSensorCollection().setQuadraturePosition(0, 0);
                break;
            case kLeft:
                leftM.getSensorCollection().setQuadraturePosition(0, 0);
            break;
        }
    }

    public void resetGyro() {
        gyro.setYaw(0);
    }

    public double getGyroAngle() {
        return getGyroYPR()[0];
    }

    public double[] getGyroYPR() {
        double[] ypr = new double[3];
        gyro.getYawPitchRoll(ypr);
        return ypr;
    }

    /** 
     * Checks the drivetrain's slave motor controllers, and garuntees that
     * they are set to follow mode in relation to the masters.
     */
    private void checkSlavesToFollow() {
        for (VictorSPX v : leftS) {
            if (v.getControlMode() != ControlMode.Follower) {
                v.follow(leftM);
            }
        }
        for (VictorSPX v : rightS) {
            if (v.getControlMode() != ControlMode.Follower) {
                v.follow(rightM);
            }
        }
    }

    @Override
    protected void initDefaultCommand() {

    }

    @Override
    public void Periodic() {
        SmartDashboard.putNumber("rightEncoder", getDriveEncoder(DriveSide.kRight));
        SmartDashboard.putNumber("leftEncoder", getDriveEncoder(DriveSide.kLeft));

        int rVel = rightM.getSelectedSensorVelocity();
        int lVel = leftM.getSelectedSensorVelocity();

        SmartDashboard.putNumber("rightVelocity", rVel);
        SmartDashboard.putNumber("leftVelocity", lVel);

        SmartDashboard.putNumberArray("GyroYPR", getGyroYPR());
    }
}