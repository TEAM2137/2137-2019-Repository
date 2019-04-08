package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANPIDController;

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

    public final double VELOCITY_MAXIMUM = 5700;

    private final double VELOCITY_FF = 0.0001639344;
    private final double VELOCITY_P = 0.0001;
    private final double VELOCITY_I = 0;
    private final double VELOCITY_D = 0;
    private final double VELOCITY_IZONE = 0;

    private CANPIDController leftVelController, rightVelController;

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

        leftVelController = leftMSpark.getPIDController();
        rightVelController = rightMSpark.getPIDController();

        leftVelController.setFF(0);
        rightVelController.setFF(0);
        leftVelController.setP(VELOCITY_P);
        rightVelController.setP(VELOCITY_P);
        leftVelController.setI(VELOCITY_I);
        rightVelController.setI(VELOCITY_I);
        leftVelController.setD(VELOCITY_D);
        rightVelController.setD(VELOCITY_D);
        leftVelController.setIZone(VELOCITY_IZONE);
        rightVelController.setIZone(VELOCITY_IZONE);
        leftVelController.setOutputRange(-1, 1);
        rightVelController.setOutputRange(-1, 1);

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
        double leftFF = VELOCITY_FF;
        double rightFF = VELOCITY_FF;

        double leftSpeed = -_leftSpd;
        double rightSpeed = _rightSpd;

        // double leftSpeed = 0.5;
        // double rightSpeed = 0.5;

        double leftTargetInRPM = leftSpeed * VELOCITY_MAXIMUM;
        double rightTargetInRPM = rightSpeed * VELOCITY_MAXIMUM; 

        // double leftFF = 1;
        // double rightFF = 1;

        leftVelController.setFF(leftFF);
        rightVelController.setFF(rightFF);
        
        // leftVelController.setReference(-leftSpeed, ControlType.kVelocity);
        // rightVelController.setReference(-rightSpeed, ControlType.kVelocity);

        leftVelController.setReference(leftTargetInRPM, ControlType.kVelocity);
        rightVelController.setReference(rightTargetInRPM, ControlType.kVelocity);

        for (CANSparkMax s : leftSSpark) {
            s.follow(leftMSpark);
        }
        for (CANSparkMax s : rightSSpark) {
            s.follow(rightMSpark);
        }
        
        SmartDashboard.putNumber("LeftTarget", leftTargetInRPM);
        SmartDashboard.putNumber("RightTarget", rightTargetInRPM);
        SmartDashboard.putNumber("LeftError", leftTargetInRPM - leftMSpark.getEncoder().getVelocity());
        SmartDashboard.putNumber("RightError", rightTargetInRPM - rightMSpark.getEncoder().getVelocity());

        System.out.println(leftFF);

        // System.out.println("setVelSpeed: Sparks not implemented yet!");
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
        SmartDashboard.putNumber("LeftVelocity", leftMSpark.getEncoder().getVelocity());
        SmartDashboard.putNumber("RightVelocity", rightMSpark.getEncoder().getVelocity());
    }
}