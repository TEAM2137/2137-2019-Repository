package org.torc.robot2019.annotation.scanner;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.torc.robot2019.opmodes.Autonomous;
import org.torc.robot2019.opmodes.Disabled;
import org.torc.robot2019.opmodes.TeleOp;
import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Main;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("all")
public class MultiTeleOp extends RobotBase {

    private Watchdog m_watchdog;
    
    public boolean commandEnabled = false;

    public enum RobotState {
        Autonomous("Autonomous"), TeleOp("TeleOp"), Test("Test"), Disabled("Disabled");

        public String name = "";

        RobotState(String _name) {
            this.name = _name;
        }

        public String toString() {
            return name;
        }
    }

    private Future<?> future;
    private ExecutorService executorService;
    private SendableChooser<Class> functionSendable = new SendableChooser<Class>();

    private RobotState robotStateCurrent = RobotState.Disabled;
    private RobotState robotStatePassed = RobotState.Disabled;

    private Class robotSendableCurrent;
    private Class robotSendablePassed;

    @Override
    public void startCompetition() {

        double peroid = 0.02;
        m_watchdog = new Watchdog(peroid, this::printLoopOverrunMessage);

        // Tell the DS that the robot is ready to be enabled
        HAL.observeUserProgramStarting();

        sendHALInfo();
        SmartDashboard.updateValues();
        LiveWindow.updateValues();
        Shuffleboard.update();

        executorService = Executors.newSingleThreadExecutor();

        try {
            future = executorService.submit(Disabled.class.newInstance());
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        functionSendable.addDefault(TeleOp.class.getSimpleName(), TeleOp.class);

        Class main = Main.class;
        try {
            for (Class a : ClassCollector.AnnotationFilter(TeleOpFunction.class,
                    main.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())) {
                functionSendable.addObject(((TeleOpFunction) a.getAnnotation(TeleOpFunction.class)).name(), a);
            }
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        SmartDashboard.putData("TeleOp Chooser", functionSendable);
        this.robotSendableCurrent = this.functionSendable.getSelected();

        while (!Thread.interrupted()) {
            m_watchdog.reset();

            this.robotStateCurrent = getRobotState();
            this.robotSendableCurrent = functionSendable.getSelected();

            LiveWindow.setEnabled(false);
            Shuffleboard.disableActuatorWidgets();

            if(commandEnabled){
                commandPeriodic();
            }

            if (robotStateCurrent != robotStatePassed || this.robotSendableCurrent != this.robotSendablePassed) {
                future.cancel(true);

                if (this.robotStateCurrent == RobotState.TeleOp || this.robotStateCurrent == RobotState.Test) {
                    try {
                        System.out.println("Submiting New OpMode");
                        future = executorService.submit((OpMode) functionSendable.getSelected().newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (this.robotStateCurrent == RobotState.Autonomous) {
                    try {
                        future = executorService.submit(Autonomous.class.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(robotStateCurrent == RobotState.Disabled){
                if(robotStatePassed != RobotState.Disabled){
                    m_watchdog.addEpoch("disabledInit()");
                }

                m_watchdog.addEpoch("disablePeriodic()");
            } else if (robotStateCurrent == RobotState.Autonomous){
                if(robotStatePassed != RobotState.Autonomous){
                    m_watchdog.addEpoch("autonomousInit()");
                }

                m_watchdog.addEpoch("autonomousPeriodic()");
            } else if (robotStateCurrent == RobotState.TeleOp){
                if(robotStatePassed != RobotState.TeleOp){
                    m_watchdog.addEpoch("teleopInit()");
                }

                m_watchdog.addEpoch("teleopPeriodic()");
            } else {
                if(robotStatePassed != RobotState.Test){
                    m_watchdog.addEpoch("testInit()");
                }

                m_watchdog.addEpoch("testPeriodic()");
            }

            sendHALInfo();

            m_watchdog.addEpoch("robotPeriodic()");
            m_watchdog.disable();

            SmartDashboard.updateValues();
            LiveWindow.updateValues();
            Shuffleboard.update();


            // Warn on loop time overruns
            if (m_watchdog.isExpired()) {
                m_watchdog.printEpochs();
            }

            this.robotSendablePassed = this.robotSendableCurrent;
            this.robotStatePassed = this.robotStateCurrent;
        }

        future.cancel(true);
        executorService.shutdownNow();
    }

    public RobotState getRobotState(){
        if(isAutonomous()) return RobotState.Autonomous;
        if(isOperatorControl()) return RobotState.TeleOp;
        if(isTest()) return RobotState.Test;

        return RobotState.Disabled;
    }

    public void sendHALInfo(){
        if(isAutonomous()){
            HAL.observeUserProgramAutonomous();
        } else if(isOperatorControl()){
            HAL.observeUserProgramTeleop();
        } else if(isTest()){
            HAL.observeUserProgramTest();
        } else {
            HAL.observeUserProgramDisabled();
        }
    }

    private void printLoopOverrunMessage() {
        DriverStation.reportWarning("Loop time of " + 0.02 + "s overrun\n", false);
    }

    private static ArrayList<InheritedPeriodic> PeriodicList = new ArrayList<InheritedPeriodic>();

	public static void AddToPeriodic(InheritedPeriodic toAdd) {
		PeriodicList.add(toAdd);
	}

    private void commandPeriodic(){
        // Manually call the Scheduler "run" command.
		Scheduler.getInstance().run();
		// Call all perodic funtions in PeriodicList
		for(InheritedPeriodic per : PeriodicList) {
			per.Periodic();
		}
    }
}