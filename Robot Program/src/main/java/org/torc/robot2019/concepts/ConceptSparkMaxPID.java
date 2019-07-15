package org.torc.robot2019.concepts;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@TeleOp(name = "Concept Spark Max PID")
public class ConceptSparkMaxPID extends OpMode {
    private CANSparkMax m_motor_left;
    private CANSparkMax m_motor_left_slave1;
    private CANSparkMax m_motor_left_slave2;

    private CANSparkMax m_motor_right;
    private CANSparkMax m_motor_right_slave1;
    private CANSparkMax m_motor_right_slave2;

    private CANPIDController m_pidController_left;
    private CANPIDController m_pidController_right;

    private CANEncoder m_encoder_left;
    private CANEncoder m_encoder_right;

    public double kLeftP, kLeftI, kLeftD, kLeftIZ, kLeftFF, kMaxOutput, kMinOutput;
    public double kRightP, kRightI, kRightD, kRightIZ, kRightFF;
    public int leftMasterID, rightMasterID, leftSlave1ID, rightSlave1ID, leftSlave2ID, rightSlave2ID;
    boolean readyToRun = false;
    boolean firstTimeRunning = true;

    @Override
    public void INIT() {
        // PID coefficients
        kLeftP = 0.1;
        kLeftI = 1e-4;
        kLeftD = 1;
        kLeftIZ = 0;
        kLeftFF = 0;

        kRightP = 0.1;
        kRightI = 1e-4;
        kRightD = 1;
        kRightIZ = 0;
        kRightFF = 0;

        kMaxOutput = 1;
        kMinOutput = -1;

        leftMasterID = 10;
        rightMasterID = 11;
        leftSlave1ID = 12;
        rightSlave1ID = 13;
        leftSlave2ID = 14;
        rightSlave2ID = 15;

        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("P Gain Left", kLeftP);
        SmartDashboard.putNumber("I Gain Left", kLeftI);
        SmartDashboard.putNumber("D Gain Left", kLeftD);
        SmartDashboard.putNumber("I Zone Left", kLeftIZ);
        SmartDashboard.putNumber("Feed Forward Left", kLeftFF);

        SmartDashboard.putNumber("P Gain Right", kRightP);
        SmartDashboard.putNumber("I Gain Right", kRightI);
        SmartDashboard.putNumber("D Gain Right", kRightD);
        SmartDashboard.putNumber("I Zone Right", kRightIZ);
        SmartDashboard.putNumber("Feed Forward Right", kRightFF);

        SmartDashboard.putNumber("Max Output", kMaxOutput);
        SmartDashboard.putNumber("Min Output", kMinOutput);
        SmartDashboard.putNumber("Set Inches", 0);
        SmartDashboard.putNumber("Wheel Dia", 6.0);

        SmartDashboard.putNumber("CAN ID Left Master", leftMasterID);
        SmartDashboard.putNumber("CAN ID Left Slave 1", leftSlave1ID);
        SmartDashboard.putNumber("CAN ID Left Slave 2", leftSlave2ID);

        SmartDashboard.putNumber("CAN ID Right Master", rightMasterID);
        SmartDashboard.putNumber("CAN ID Right Slave 1", rightSlave1ID);
        SmartDashboard.putNumber("CAN ID Right Slave 2", rightSlave2ID);

        SmartDashboard.putBoolean("Ready To Run", false);

        while (!SmartDashboard.getBoolean("Ready To Run", false)) {
            // This is were you could put code
            // It is not proper to have a while loop in this program
            // However for this example it is fine
        }

        // initialize motor
        m_motor_left = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Left Master", leftMasterID),
                MotorType.kBrushless);
        m_motor_left_slave1 = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Left Slave 1", leftSlave1ID),
                MotorType.kBrushless);
        m_motor_left_slave2 = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Left Slave 2", leftSlave2ID),
                MotorType.kBrushless);

        m_motor_right = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Right Master", rightMasterID),
                MotorType.kBrushless);
        m_motor_right_slave1 = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Right Slave 1", rightSlave1ID),
                MotorType.kBrushless);
        m_motor_right_slave2 = new CANSparkMax((int) SmartDashboard.getNumber("CAN ID Right Slave 2", rightSlave2ID),
                MotorType.kBrushless);

        /**
         * remove unneeded values
         */
        SmartDashboard.delete("CAN ID Left Master");
        SmartDashboard.delete("CAN ID Left Slave 1");
        SmartDashboard.delete("CAN ID Left Slave 2");

        SmartDashboard.delete("CAN ID Right Master");
        SmartDashboard.delete("CAN ID Right Slave 1");
        SmartDashboard.delete("CAN ID Right Slave 2");

        /**
         * The restoreFactoryDefaults method can be used to reset the configuration
         * parameters in the SPARK MAX to their factory default state. If no argument is
         * passed, these parameters will not persist between power cycles
         */
        m_motor_left.restoreFactoryDefaults();
        m_motor_left_slave1.restoreFactoryDefaults();
        m_motor_left_slave2.restoreFactoryDefaults();

        m_motor_right.restoreFactoryDefaults();
        m_motor_right_slave1.restoreFactoryDefaults();
        m_motor_right_slave2.restoreFactoryDefaults();

        /**
         * Make the other motors follow each other
         */
        m_motor_left_slave1.follow(m_motor_left);
        m_motor_left_slave2.follow(m_motor_left);

        m_motor_right_slave1.follow(m_motor_right);
        m_motor_right_slave2.follow(m_motor_right);
        /**
         * In order to use PID functionality for a controller, a CANPIDController object
         * is constructed by calling the getPIDController() method on an existing
         * CANSparkMax object
         */
        m_pidController_left = m_motor_left.getPIDController();
        m_pidController_right = m_motor_right.getPIDController();

        // Encoder object created to display position values
        m_encoder_left = m_motor_left.getEncoder();
        m_encoder_right = m_motor_right.getEncoder();

        // set PID coefficients
        m_pidController_left.setP(kLeftP);
        m_pidController_left.setI(kLeftI);
        m_pidController_left.setD(kLeftD);
        m_pidController_left.setIZone(kLeftIZ);
        m_pidController_left.setFF(kLeftFF);
        m_pidController_left.setOutputRange(kMinOutput, kMaxOutput);

        m_pidController_right.setP(kRightP);
        m_pidController_right.setI(kRightI);
        m_pidController_right.setD(kRightD);
        m_pidController_right.setIZone(kRightIZ);
        m_pidController_right.setFF(kRightFF);
        m_pidController_right.setOutputRange(kMinOutput, kMaxOutput);
    }

    @Override
    public void LOOP() {
        // read PID coefficients from SmartDashboard
        double leftP = SmartDashboard.getNumber("P Gain Left", 0);
        double leftI = SmartDashboard.getNumber("I Gain Left", 0);
        double leftD = SmartDashboard.getNumber("D Gain Left", 0);
        double leftIZ = SmartDashboard.getNumber("I Zone Left", 0);
        double leftFF = SmartDashboard.getNumber("Feed Forward Left", 0);

        double rightP = SmartDashboard.getNumber("P Gain Right", 0);
        double rightI = SmartDashboard.getNumber("I Gain Right", 0);
        double rightD = SmartDashboard.getNumber("D Gain Right", 0);
        double rightIZ = SmartDashboard.getNumber("I Zone Right", 0);
        double rightFF = SmartDashboard.getNumber("Feed Forward Right", 0);

        double max = SmartDashboard.getNumber("Max Output", 0);
        double min = SmartDashboard.getNumber("Min Output", 0);
        double inches = SmartDashboard.getNumber("Set Inches", 0);
        double wheelDia = SmartDashboard.getNumber("Wheel Dia", 6.0);

        // if PID coefficients on SmartDashboard have changed, write new values to
        // controller
        if (leftP != kLeftP) {
            m_pidController_left.setP(leftP);
            kLeftP = leftP;
        }
        if (leftI != kLeftI) {
            m_pidController_left.setI(leftI);
            kLeftI = leftI;
        }
        if (leftD != kLeftD) {
            m_pidController_left.setD(leftD);
            kLeftD = leftD;
        }
        if (leftIZ != kLeftIZ) {
            m_pidController_left.setIZone(leftIZ);
            kLeftIZ = leftIZ;
        }
        if (leftFF != kLeftFF) {
            m_pidController_left.setFF(leftFF);
            kLeftFF = leftFF;
        }
        if (rightP != kRightP) {
            m_pidController_right.setP(rightP);
            kRightP = rightP;
        }
        if (rightI != kRightI) {
            m_pidController_right.setI(rightI);
            kRightI = rightI;
        }
        if (rightD != kRightD) {
            m_pidController_right.setD(rightD);
            kRightD = rightD;
        }
        if (rightIZ != kRightIZ) {
            m_pidController_right.setIZone(rightIZ);
            kRightIZ = rightIZ;
        }
        if (rightFF != kRightFF) {
            m_pidController_right.setFF(rightFF);
            kRightFF = rightFF;
        }
        if ((max != kMaxOutput) || (min != kMinOutput)) {
            m_pidController_left.setOutputRange(min, max);
            kMinOutput = min;
            kMaxOutput = max;
        }

        /**
         * PIDController objects are commanded to a set point using the SetReference()
         * method.
         * 
         * The first parameter is the value of the set point, whose units vary depending
         * on the control type set in the second parameter.
         * 
         * The second parameter is the control type can be set to one of four
         * parameters: com.revrobotics.ControlType.kDutyCycle
         * com.revrobotics.ControlType.kPosition com.revrobotics.ControlType.kVelocity
         * com.revrobotics.ControlType.kVoltage
         */

        double revPerInch = (wheelDia * 3.14);
        double rotations = inches / revPerInch;
        m_pidController_left.setReference(rotations, ControlType.kPosition);
        m_pidController_right.setReference(rotations, ControlType.kPosition);

        SmartDashboard.putNumber("SetPoint Revs", rotations);
        SmartDashboard.putNumber("ProcessVariable Revs", m_encoder_left.getPosition());
        SmartDashboard.putNumber("ProcessVariable Revs", m_encoder_right.getPosition());
    }
}