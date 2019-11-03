package org.torc.robot2019.annotation.scanner;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.torc.robot2019.opmodes.Autonomous;
import org.torc.robot2019.opmodes.Disabled;
import org.torc.robot2019.opmodes.TeleOp;
import org.torc.robot2019.robot.Main;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("all")
public class MultiTeleOp extends RobotBase {

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
            this.robotStateCurrent = getRobotState();
            this.robotSendableCurrent = functionSendable.getSelected();

            LiveWindow.setEnabled(false);
            Shuffleboard.disableActuatorWidgets();

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

            sendHALInfo();

            SmartDashboard.updateValues();
            LiveWindow.updateValues();
            Shuffleboard.update();


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
}
