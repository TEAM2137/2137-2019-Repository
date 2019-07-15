package org.torc.robot2019.hardware;

import java.util.HashMap;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.torc.robot2019.functions.FileLogger;
import org.torc.robot2019.functions.Range;

public class CustomDrive2019 {

    public MotorType motorType = MotorType.kBrushless;

    // public int intLeftMainMotorID = 10;// ID of the main motor the other motors
    // will follow
    // public int intLeftSlaveMotor1ID = 12;// ID of a motor that follows the main
    // motor
    // public int intLeftSlaveMotor2ID = 14;// ID of a motor that follows the main
    // motor

    // public int intRightMainMotorID = 11;// ID of the main motor the other motors
    // will follow
    // public int intRightSlaveMotor1ID = 13;// ID of a motor that follows the main
    // motor
    // public int intRightSlaveMotor2ID = 13;// Or 15 IDK ID of a motor that follows
    // the main motor

    // Wrong Class
    // public static int intPivotMantisMotorID = 40;//Id of the motor moving the
    // mantis arm
    // public static int intRightMantisMotorID = 41;//ID of the motor running at the
    // end of the mantis arm
    // public static int intLeftMantisMotorID = 42;//ID of the motor running at the
    // end of the mantis arm

    // public static int intPogoMotorID = 43;
    // public static int intPivotArmMotorID = 20;
    // public static int intElevatorArmMotorID = 21;

    public double maxRobotSpeed = 1.0;
    public double minRobotSpeed = -1.0;

    CANSparkMax leftMotor1, leftMotor2, leftMotor3, rightMotor1, rightMotor2, rightMotor3;
    FileLogger fileLogger;

    enum RobotBases {
        DEEP_SPACE_2019, POWER_UP_2018, STEAM_WORKS_2017
    }

    public CustomDrive2019() {
        // Place your code here
    }

    public void init(FileLogger filelogger, int lm1ID, int lm2ID, int lm3ID, int rm1ID, int rm2ID, int rm3ID,
            MotorType motorType) {
        if (motorType == null)
            motorType = MotorType.kBrushless;

        this.leftMotor1 = new CANSparkMax(lm1ID, motorType);
        this.leftMotor2 = new CANSparkMax(lm2ID, motorType);
        this.leftMotor3 = new CANSparkMax(lm3ID, motorType);

        this.leftMotor1 = new CANSparkMax(rm1ID, motorType);
        this.leftMotor2 = new CANSparkMax(rm2ID, motorType);
        this.leftMotor3 = new CANSparkMax(rm3ID, motorType);
    
        this.fileLogger = filelogger;
    }

    public void init(FileLogger filelogger, int lm1ID, int lm2ID, int rm1ID, int rm2ID, MotorType motorType) {
        if (motorType == null)
            motorType = MotorType.kBrushless;

        this.leftMotor1 = new CANSparkMax(lm1ID, motorType);
        this.leftMotor2 = new CANSparkMax(lm2ID, motorType);

        this.rightMotor1 = new CANSparkMax(rm1ID, motorType);
        this.rightMotor2 = new CANSparkMax(rm2ID, motorType);
    
        this.fileLogger = filelogger;
    }

    public void init(FileLogger filelogger, int lm1ID, int rm1ID, MotorType motorType) {
        if (motorType == null)
            motorType = MotorType.kBrushless;

        this.leftMotor1 = new CANSparkMax(lm1ID, motorType);
        this.rightMotor1 = new CANSparkMax(rm1ID, motorType);
    
        this.fileLogger = filelogger;
    }

    public void setMotorDirection(RobotBases robotBases) {
        switch (robotBases) {
        case DEEP_SPACE_2019:
            if (this.leftMotor1 != null)
                this.leftMotor1.setInverted(false);
            if (this.leftMotor2 != null)
                this.leftMotor2.setInverted(false);
            if (this.leftMotor3 != null)
                this.leftMotor3.setInverted(false);

            if (this.rightMotor1 != null)
                this.rightMotor1.setInverted(false);
            if (this.rightMotor2 != null)
                this.rightMotor2.setInverted(false);
            if (this.rightMotor3 != null)
                this.rightMotor3.setInverted(false);

            break;

        case POWER_UP_2018:
            if (this.leftMotor1 != null)
                this.leftMotor1.setInverted(false);
            if (this.leftMotor2 != null)
                this.leftMotor2.setInverted(false);
            if (this.leftMotor3 != null)
                this.leftMotor3.setInverted(false);

            if (this.rightMotor1 != null)
                this.rightMotor1.setInverted(false);
            if (this.rightMotor2 != null)
                this.rightMotor2.setInverted(false);
            if (this.rightMotor3 != null)
                this.rightMotor3.setInverted(false);

            break;

        case STEAM_WORKS_2017:
            if (this.leftMotor1 != null)
                this.leftMotor1.setInverted(false);
            if (this.leftMotor2 != null)
                this.leftMotor2.setInverted(false);
            if (this.leftMotor3 != null)
                this.leftMotor3.setInverted(false);

            if (this.rightMotor1 != null)
                this.rightMotor1.setInverted(false);
            if (this.rightMotor2 != null)
                this.rightMotor2.setInverted(false);
            if (this.rightMotor3 != null)
                this.rightMotor3.setInverted(false);

            break;
        }
    }

    public void stopAllMotors() {
        if (this.leftMotor1 != null)
            this.leftMotor1.stopMotor();
        if (this.leftMotor2 != null)
            this.leftMotor2.stopMotor();
        if (this.leftMotor3 != null)
            this.leftMotor3.stopMotor();

        if (this.rightMotor1 != null)
            this.rightMotor1.stopMotor();
        if (this.rightMotor2 != null)
            this.rightMotor2.stopMotor();
        if (this.rightMotor3 != null)
            this.rightMotor3.stopMotor();
    }

    public double checkMotorSpeed(double speed) {
        return Range.clip(speed, this.minRobotSpeed, this.maxRobotSpeed);
    }

    public void setMaxRobotSpeed(double max) {  this.maxRobotSpeed = max;  }
    public double getMaxRobotSpeed() {  return this.maxRobotSpeed;  }

    public void setMinRobotSpeed(double min) {  this.minRobotSpeed = min;  }
    public double getMinRobotSpeed() {  return this.minRobotSpeed;  }

    public void setLeftMotorPower(double power) {
        if (this.leftMotor1 != null)
            this.leftMotor1.set(power);
        if (this.leftMotor2 != null)
            this.leftMotor2.set(power);
        if (this.leftMotor3 != null)
            this.leftMotor3.set(power);
    }

    public void setRightMotorPower(double power){
        if (this.rightMotor1 != null)
            this.rightMotor1.set(power);
        if (this.rightMotor2 != null)
            this.rightMotor2.set(power);
        if (this.rightMotor3 != null)
            this.rightMotor3.set(power);
    }

    public void TankDrive(double leftSpeed, double rightSpeed){     
        if (rightMotor1 != null)
            rightMotor1.set(checkMotorSpeed(-rightSpeed));
        if (rightMotor2 != null)
            rightMotor2.set(checkMotorSpeed(-rightSpeed));
        if (rightMotor3 != null)
            rightMotor3.set(checkMotorSpeed(-rightSpeed));  
            
        if (leftMotor1 != null)
            leftMotor1.set(checkMotorSpeed(-leftSpeed));
        if (leftMotor2 != null)
            leftMotor2.set(checkMotorSpeed(-leftSpeed));
        if (leftMotor3 != null)
            leftMotor3.set(checkMotorSpeed(-leftSpeed)); 
    }

    public void POVDrive(double joyStickY, double joyStickX){
        setLeftMotorPower(-joyStickY + joyStickX);
        setRightMotorPower(-joyStickY - joyStickX);
    }

}