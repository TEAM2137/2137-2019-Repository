package org.torc.robot2019.annotation_scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.management.RuntimeErrorException;

//Imports
import org.torc.robot2019.annotation_scanner.annotations.*;
import org.torc.robot2019.annotation_scanner.filters.OpModeRegistrarManager;
import org.torc.robot2019.robot.Main;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("all")
public class RobotStateTracker extends RobotBase {

    public enum CompState {
        kAutonomous, kDisabled, kTeleOp, kTest, kNone, kRunning
    }

    OpModeRegistrarManager opModeRegistrarManager;

    public CompState m_LastOpModeState = CompState.kNone;
    public CompState m_CurrentOpModeState = CompState.kNone;

    public OpModeSendable opModeSendable;

    public boolean requestStop = false;

    public Class lastAutoSendable;
    public Class lastTeleSendable;
    public Class lastRunTimeSendable;
    public Class lastOnDisabledSendable;

    protected OpMode opMode;
    protected OpMode runTimeOpMode;

    public RobotStateTracker() {
        HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Timed);
    }

    @Override
    public void startCompetition() {
        opModeRegistrarManager = new OpModeRegistrarManager(Main.class);
        opModeSendable = new OpModeSendable(opModeRegistrarManager);

        HAL.observeUserProgramStarting();

        sendInfoToDS();
        SmartDashboard.updateValues();
        LiveWindow.updateValues();
        Shuffleboard.update();

        //try {
            // Class<?> clazz = (Class) opModeSendable.getRunTimeCurrentVal();
            // runTimeOpMode = (OpMode) clazz.newInstance();
            Class<?> clazz = opModeSendable.getRunTimeCurrentVal();
            
            //Constructor<?> ctor = clazz.getConstructor(clazz.getClass());
            //Object object = ctor.newInstance(new Object[] {});
        //} 
        //catch(IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e1) {
        //catch(IllegalArgumentException | SecurityException e1) {
        //    e1.printStackTrace();
        //    System.out.println("Run Time Op Mode Error While Loading the Class For The First Time");
        //}

        while (!requestStop) {
            try {
                if (checkForOpModeStateChange()) {
                    this.callINIT(true);
                } else {
                    this.callINIT(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error With Loading Selected Class in RobotStateTracker");
            }

            sendInfoToDS();

            SmartDashboard.updateValues();
            LiveWindow.updateValues();
            Shuffleboard.update();
        }
    }

    protected void sendInfoToDS() {
        switch (m_CurrentOpModeState) {
            case kAutonomous:
                HAL.observeUserProgramAutonomous();
                break;
            case kTeleOp:
                HAL.observeUserProgramTeleop();
                break;
            case kDisabled:
                HAL.observeUserProgramDisabled();
                break;
            case kTest:
                HAL.observeUserProgramTest();
                break;
            case kNone:

                break;
        }
    }

    protected void callINIT(boolean USEONLYINROBOTSTATETRACKER) throws InstantiationException, IllegalAccessException {
        LiveWindow.setEnabled(false);
        Shuffleboard.disableActuatorWidgets();

        if(opModeSendable.getAutoSendableCurrentVal() != lastAutoSendable){
            if(m_CurrentOpModeState == CompState.kAutonomous){
                USEONLYINROBOTSTATETRACKER = true;
            }
        }
        
        if (opModeSendable.getTeleSendableCurrentVal() != lastTeleSendable){
            if(m_CurrentOpModeState == CompState.kTeleOp){
                USEONLYINROBOTSTATETRACKER = true;
            }
        } 
        
        if (opModeSendable.getOnDisabledSendableCurrentVal() != lastOnDisabledSendable){
            if(m_CurrentOpModeState == CompState.kDisabled){
                USEONLYINROBOTSTATETRACKER = true;
            }
        }
        
        if (opModeSendable.getRunTimeCurrentVal() != lastRunTimeSendable){
            this.runTimeOpMode = (OpMode) opModeSendable.getRunTimeCurrentVal().newInstance();
            this.runTimeOpMode.INIT();
        }

        if (USEONLYINROBOTSTATETRACKER) {
            if (m_CurrentOpModeState == CompState.kAutonomous) {
                this.opMode = (OpMode) opModeSendable.getAutoSendableCurrentVal().newInstance();
                this.opMode.INIT();
            } else if (m_CurrentOpModeState == CompState.kTeleOp) {
                this.opMode = (OpMode) opModeSendable.getTeleSendableCurrentVal().newInstance();
                this.opMode.INIT();
            } else if (m_CurrentOpModeState == CompState.kDisabled) {
                this.opMode = (OpMode) opModeSendable.getOnDisabledSendableCurrentVal().newInstance();
                this.opMode.INIT();
            } 
        } else {
            if (m_CurrentOpModeState == CompState.kAutonomous || 
                m_CurrentOpModeState == CompState.kTeleOp ||
                m_CurrentOpModeState == CompState.kDisabled) {
                this.opMode.LOOP();
            }
            this.runTimeOpMode.LOOP();        
        }

        lastAutoSendable = opModeSendable.getAutoSendableCurrentVal();
        lastTeleSendable = opModeSendable.getTeleSendableCurrentVal();
        lastOnDisabledSendable = opModeSendable.getOnDisabledSendableCurrentVal();
        lastRunTimeSendable = opModeSendable.getRunTimeCurrentVal();
    }

    public boolean checkForOpModeStateChange() {
        m_LastOpModeState = m_CurrentOpModeState;

        if (isDisabled()) {
            m_CurrentOpModeState = CompState.kDisabled;
        } else if (isAutonomous()) {
            m_CurrentOpModeState = CompState.kAutonomous;
        } else if (isOperatorControl()) {
            m_CurrentOpModeState = CompState.kTeleOp;
        } else if (isTest()) {
            m_CurrentOpModeState = CompState.kTest;
        } else {
            m_CurrentOpModeState = CompState.kNone;
        }

        if (m_LastOpModeState != m_CurrentOpModeState) {
            return true;
        } else {
            return false;
        }
    }

}