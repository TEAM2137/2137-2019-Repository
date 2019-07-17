package org.torc.robot2019;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class LockSwerveDrive {
    public CANSparkMax LeftFrontDrive;
    public CANSparkMax LeftBackDrive;
    public CANSparkMax RightFrontDrive;
    public CANSparkMax RightBackDrive;
    public CANSparkMax LeftFrontPivot;
    public CANSparkMax LeftBackPivot;
    public CANSparkMax RightFrontPivot;
    public CANSparkMax RightBackPivot;

    enum RobotSides {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        ALL
    }

    enum MotorSystems{
        PIVOT_MOTORS,
        DRIVE_MOTORS
    }

    public LockSwerveDrive(int _LeftFrontDriveID, int _LeftBackDriveID, int _RightFrontDriveID, int _RightBackDriveID, int _LeftFrontPivotID, int _LeftBackPivotID, int _RightFrontPivotID, int _RightBackPivotID){
        this.LeftFrontDrive     = new CANSparkMax(_LeftFrontDriveID, MotorType.kBrushless);
        this.LeftBackDrive      = new CANSparkMax(_LeftBackDriveID, MotorType.kBrushless);
        this.RightFrontDrive    = new CANSparkMax(_RightFrontDriveID, MotorType.kBrushless);
        this.RightBackDrive     = new CANSparkMax(_RightBackDriveID, MotorType.kBrushless);

        this.LeftFrontPivot     = new CANSparkMax(_LeftFrontPivotID, MotorType.kBrushless);
        this.LeftBackPivot      = new CANSparkMax(_LeftBackPivotID, MotorType.kBrushless);
        this.RightFrontPivot    = new CANSparkMax(_RightFrontPivotID, MotorType.kBrushless);
        this.RightBackPivot     = new CANSparkMax(_RightBackPivotID, MotorType.kBrushless);
    }

    public void setGroupMotors(double power, MotorSystems systems, RobotSides side){
        if(systems == MotorSystems.DRIVE_MOTORS){
            switch (side) {
                case LEFT:
                    this.LeftFrontPivot.set(power);
                    this.LeftBackPivot.set(power);
                    break;

                case RIGHT:
                    this.RightFrontPivot.set(power);
                    this.RightBackPivot.set(power);
                    break;

                case FRONT:
                    this.LeftFrontPivot.set(power);
                    this.RightFrontPivot.set(power);
                    break;

                case BACK:
                    this.LeftBackPivot.set(power);
                    this.RightBackPivot.set(power);
                    break;

                case ALL:
                    this.LeftFrontPivot.set(power);
                    this.RightFrontPivot.set(power);
                    this.LeftBackPivot.set(power);
                    this.RightBackPivot.set(power);
                    break;
            }
        } else {
            switch (side) {
                case LEFT:
                    this.LeftFrontDrive.set(power);
                    this.LeftBackDrive.set(power);
                    break;

                case RIGHT:
                    this.RightFrontDrive.set(power);
                    this.RightBackDrive.set(power);
                    break;

                case FRONT:
                    this.LeftFrontDrive.set(power);
                    this.RightFrontDrive.set(power);
                    break;

                case BACK:
                    this.LeftBackDrive.set(power);
                    this.RightBackDrive.set(power);
                    break;

                case ALL:
                    this.LeftFrontDrive.set(power);
                    this.RightFrontDrive.set(power);
                    this.LeftBackDrive.set(power);
                    this.RightBackDrive.set(power);
                    break;
            }
        }
    }
}