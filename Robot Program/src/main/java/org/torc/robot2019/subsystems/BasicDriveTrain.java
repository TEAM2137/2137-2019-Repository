package org.torc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
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

    // Practice bot controllers //
    private TalonSRX leftM, rightM;

    private VictorSPX[] leftS; // = new VictorSPX[2];
    private VictorSPX[] rightS; // = new VictorSPX[2];

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

        switch (KMap.GetRobotType()) {
            case Practice:
                leftM = new TalonSRX(_leftMID);
                rightM = new TalonSRX(_rightMID);
            
                leftS = new VictorSPX[2];
                rightS = new VictorSPX[2];

                leftS[0] = new VictorSPX(_leftS0ID);
                rightS[0] = new VictorSPX(_rightS0ID);

                leftS[1] = new VictorSPX(_leftS1ID);
                rightS[1] = new VictorSPX(_rightS1ID);

                MotorControllers.TalonSRXConfig(leftM);
                MotorControllers.TalonSRXConfig(rightM);

                // Configure masters to use quad encoders
                leftM.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
                rightM.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

                // Invert left so it goes forwards with right (same phase)
                leftM.setSensorPhase(true);

                leftM.config_kF(0, 2.2);
                rightM.config_kF(0, 2.2);

                leftM.config_kP(0, 3);
                rightM.config_kP(0, 3);

                leftM.config_kI(0, 0.01);
                rightM.config_kI(0, 0.01);

                leftM.config_IntegralZone(0, 50);
                rightM.config_IntegralZone(0, 50);

                leftM.configClosedloopRamp(0.25);
                rightM.configClosedloopRamp(0.25);

                leftM.configContinuousCurrentLimit(40);
                rightM.configContinuousCurrentLimit(40);
                break;
            case Competition:
                leftMSpark = new CANSparkMax(_leftMID, MotorType.kBrushless);
                rightMSpark = new CANSparkMax(_rightMID, MotorType.kBrushless);

                leftSSpark = new CANSparkMax[1];
                rightSSpark = new CANSparkMax[1];

                leftSSpark[0] = new CANSparkMax(_leftS0ID, MotorType.kBrushless);
                rightSSpark[0] = new CANSparkMax(_rightS0ID, MotorType.kBrushless);
                break;
        }
        
        gyro = new PigeonIMU(_pigeonID);
        
    }

    public void setPercSpeed(double _leftSpd, double _rightSpd) {
        switch (KMap.GetRobotType()) {
            case Practice:
                leftM.set(ControlMode.PercentOutput, -_leftSpd);
                rightM.set(ControlMode.PercentOutput, _rightSpd);

                leftS[0].set(ControlMode.PercentOutput, -_leftSpd);
                rightS[0].set(ControlMode.PercentOutput, _rightSpd);
            
                leftS[1].set(ControlMode.PercentOutput, -_leftSpd);
                rightS[1].set(ControlMode.PercentOutput, _rightSpd);
                break;
            case Competition:
                //one second between 0 and full throttle
                leftMSpark.setClosedLoopRampRate(1);
                rightMSpark.setClosedLoopRampRate(1);
                
                leftMSpark.set(-_leftSpd);
                rightMSpark.set(-_rightSpd);

                for (CANSparkMax s : leftSSpark) {
                    s.set(-_leftSpd);
                }
                for (CANSparkMax s : rightSSpark) {
                    s.set(-_rightSpd);
                }
                break;
        }
    }

    public void setVelSpeed(double _leftSpd, double _rightSpd) {
        switch (KMap.GetRobotType()) {
            case Practice:
                // If switching from a different mode, set slave followers.
                checkSlavesToFollow();

                _leftSpd = -MathExtra.clamp(_leftSpd, -1, 1) * VELOCITY_MAXIMUM;
                _rightSpd = MathExtra.clamp(_rightSpd, -1, 1) * VELOCITY_MAXIMUM;

                leftM.set(ControlMode.Velocity, _leftSpd);
                rightM.set(ControlMode.Velocity, _rightSpd);
                break;
            case Competition:
                System.out.println("setVelSpeed: Compbot not implemented yet!");
                break;
        }
    }

    public void setVelTarget(double _leftTarget, double _rightTarget) {
        switch (KMap.GetRobotType()) {
            case Practice:
                // If switching from a different mode, set slave followers.
                checkSlavesToFollow();

                leftM.set(ControlMode.Velocity, _leftTarget);
                rightM.set(ControlMode.Velocity, _rightTarget);
                break;
            case Competition:
                System.out.println("setVelTarget: Compbot not implemented yet!");
                break;
        }
    }

    public int getDriveEncoder(DriveSide _driveSide) {
        
        int retVal = 0;

        switch (KMap.GetRobotType()) {
            case Practice:
                switch (_driveSide) {
                    case kRight:
                        retVal = rightM.getSelectedSensorPosition(0);
                        break;
                    case kLeft: 
                        retVal = leftM.getSelectedSensorPosition(0);
                        break;
                }
                break;
            case Competition:
                System.out.println("getDriveEncoder: Compbot not implemented yet!");
                break;
        }

        return retVal;
    }

    public void resetDriveEncoder(DriveSide _driveSide) {
        switch (KMap.GetRobotType()) {
            case Practice:
                switch (_driveSide) {
                    case kRight:
                        rightM.getSensorCollection().setQuadraturePosition(0, 0);
                        break;
                    case kLeft:
                        leftM.getSensorCollection().setQuadraturePosition(0, 0);
                    break;
                }
                break;
            case Competition:
                System.out.println("resetDriveEncoder: Compbot not implemented yet!");
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
        switch (KMap.GetRobotType()) {
            case Practice:
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
                break;
            case Competition:
                System.out.println("checkSlavesToFollow: Compbot not implemented yet!");
                break;
        }
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