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
        kAutonomous, kDisabled, kTeleOp, kNone, kTest
    }

    private OpModeRegistrarManager opModeRegistrarManager;

    private CompState m_LastOpModeState = CompState.kNone;
    private CompState m_CurrentOpModeState = CompState.kNone;

    private OpModeSendable opModeSendable;

    private OpMode opMode;
    private OpMode runTimeOpMode;

    private Class lastRunTimeOpMode;

    protected Thread runningThread;
    private Thread runTimeThread;
    private Runnable runTimeRunnable;
    private Runnable runningRunnable;

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

        try {
            runTimeRunnable = (OpMode) opModeSendable.getRunTimeCurrentVal().newInstance();
            runTimeThread = new Thread(runTimeRunnable);
            runTimeThread.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        while (!Thread.interrupted()) {
            LiveWindow.setEnabled(false);
            Shuffleboard.disableActuatorWidgets();

            if (checkForOpModeStateChange()) {
                try {
                    runningRunnable = getRunningOpModeClass();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runningThread.interrupt();
            }

            if (!runningThread.isAlive()) {
                runningThread = new Thread(runningRunnable);
                runningThread.start();
            }

            if (opModeSendable.getRunTimeCurrentVal() != lastRunTimeOpMode) {
                try {
                    runTimeRunnable = (OpMode) opModeSendable.getRunTimeCurrentVal().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runTimeThread.interrupt();
            }

            if(!runTimeThread.isAlive()){
                runTimeThread = new Thread(runTimeRunnable);
                runTimeThread.start();
            }

            sendInfoToDS();
            SmartDashboard.updateValues();
            LiveWindow.updateValues();
            Shuffleboard.update();
        }
    }

    protected OpMode getRunningOpModeClass() throws InstantiationException, IllegalAccessException {
        switch(m_CurrentOpModeState){
            case kAutonomous:
                return (OpMode) opModeSendable.getAutoSendableCurrentVal().newInstance();
            
            case kTeleOp:
                return (OpMode) opModeSendable.getTeleSendableCurrentVal().newInstance();

            default:
                return (OpMode) opModeSendable.getOnDisabledSendableCurrentVal().newInstance();
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