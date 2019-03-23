package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.torc.robot2019.program.KMap;
import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Robot;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.tools.MotorControllers;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BasicDriveTrain extends Subsystem implements InheritedPeriodic {

    public static enum DriveSide { kRight, kLeft };

    // Comp bot controllers //
    private CANSparkMax leftMSpark, rightMSpark;

    private CANSparkMax[] leftSSpark; 
    private CANSparkMax[] rightSSpark;

    private PigeonIMU gyro;

    public final double VELOCITY_MAXIMUM = 480;

    public BasicDriveTrain(int _leftMID, int _rightMID, int _leftS0ID, int _rightS0ID,
        int _leftS1ID, int _rightS1ID, int _pigeonID) {
        // "Subscribe" to inherited Periodic
        Robot.AddToPeriodic(this);

        leftMSpark = new CANSparkMax(_leftMID, MotorType.kBrushless);
        rightMSpark = new CANSparkMax(_rightMID, MotorType.kBrushless);

        leftSSpark = new CANSparkMax[1];
        rightSSpark = new CANSparkMax[1];

        leftSSpark[0] = new CANSparkMax(_leftS0ID, MotorType.kBrushless);
        rightSSpark[0] = new CANSparkMax(_rightS0ID, MotorType.kBrushless);

        leftMSpark.setIdleMode(IdleMode.kCoast);
        rightMSpark.setIdleMode(IdleMode.kCoast);

        leftMSpark.setOpenLoopRampRate(0.2);
        rightMSpark.setOpenLoopRampRate(0.2);

        for (CANSparkMax s : leftSSpark) {
            s.setIdleMode(IdleMode.kCoast);
            s.setOpenLoopRampRate(0.2);
        }

        for (CANSparkMax s : rightSSpark) {
            s.setIdleMode(IdleMode.kCoast);
            s.setOpenLoopRampRate(0.2);
        }
        
        gyro = new PigeonIMU(_pigeonID);
        
    }

    public void setPercSpeed(double _leftSpd, double _rightSpd) {
        leftMSpark.set(-_leftSpd);
        rightMSpark.set(-_rightSpd);

        for (CANSparkMax s : leftSSpark) {
            s.set(-_leftSpd);
        }
        for (CANSparkMax s : rightSSpark) {
            s.set(-_rightSpd);
        }
    }

    public void setVelSpeed(double _leftSpd, double _rightSpd) {
        System.out.println("setVelSpeed: Sparks not implemented yet!");
    }

    public void setVelTarget(double _leftTarget, double _rightTarget) {
        System.out.println("setVelTarget: Sparks not implemented yet!");
    }

    public int getDriveEncoder(DriveSide _driveSide) {
        int retVal = 0;
        
        System.out.println("getDriveEncoder: Sparks not implemented yet!");

        return retVal;
    }

    public void resetDriveEncoder(DriveSide _driveSide) {
        System.out.println("resetDriveEncoder: Sparks not implemented yet!");
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

    public void setIdleMode(IdleMode _idleMode) {
        leftMSpark.setIdleMode(_idleMode);
        rightMSpark.setIdleMode(_idleMode);

        for (CANSparkMax s : leftSSpark) {
            s.setIdleMode(_idleMode);
        }

        for (CANSparkMax s : rightSSpark) {
            s.setIdleMode(_idleMode);
        }
    }

    /** 
     * Checks the drivetrain's slave motor controllers, and garuntees that
     * they are set to follow mode in relation to the masters.
     */
    private void checkSlavesToFollow() {
        System.out.println("checkSlavesToFollow: Sparks not implemented yet!");
    }

    @Override
    protected void initDefaultCommand() {

    }

    @Override
    public void Periodic() {
        //SmartDashboard.putNumber("rightEncoder", getDriveEncoder(DriveSide.kRight));
        //SmartDashboard.putNumber("leftEncoder", getDriveEncoder(DriveSide.kLeft));

        //int rVel = rightM.getSelectedSensorVelocity();
        //int lVel = leftM.getSelectedSensorVelocity();

        //SmartDashboard.putNumber("rightVelocity", rVel);
        //SmartDashboard.putNumber("leftVelocity", lVel);

        SmartDashboard.putNumberArray("GyroYPR", getGyroYPR());
    }
}