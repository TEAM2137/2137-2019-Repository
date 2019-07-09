package org.torc.robot2019;

import com.AnnotationScanner.Filters.OpModeRegistrarManager;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

@SuppressWarnings("all")
public class OpModeSendable {
    public SendableChooser<Class> autoSendable = new SendableChooser<Class>();
    public SendableChooser<Class> teleSendable = new SendableChooser<Class>();
    public SendableChooser<Class> runTimeSendable = new SendableChooser<Class>();
    public SendableChooser<Class> onDisabledSendable = new SendableChooser<Class>();

    public OpModeSendable(OpModeRegistrarManager opModeRegistrarManager){
        System.out.println("Running OpMode Sendable");
        
        System.out.println("Amount of Autonomous Names -- " + opModeRegistrarManager.getListAutonomousNames().size());
        boolean firstTime = true;
        for(String name : opModeRegistrarManager.getListAutonomousNames()){
            if(firstTime){
                this.autoSendable.setDefaultOption(name, opModeRegistrarManager.searchForOpMode(name));
                System.out.println("Added Autonomous Default: " + name + " " + opModeRegistrarManager.searchForOpMode(name).getName());
                firstTime = false;
            }

            this.autoSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of TeleOp -- " + opModeRegistrarManager.getListTeleOpNames().size());
        firstTime = true;
        for(String name : opModeRegistrarManager.getListTeleOpNames()){
            if(firstTime){
                this.teleSendable.setDefaultOption(name, opModeRegistrarManager.searchForOpMode(name));
                System.out.println("Added TeleOp Default: " + name + " " + opModeRegistrarManager.searchForOpMode(name).getName());
                firstTime = false;
            }

            this.teleSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of Disabled -- " + opModeRegistrarManager.getListOnDisabledNames().size());
        firstTime = true;
        for(String name : opModeRegistrarManager.getListOnDisabledNames()){
            if(firstTime){
                this.onDisabledSendable.setDefaultOption(name, opModeRegistrarManager.searchForOpMode(name));
                System.out.println("Added Disabled Default: " + name + " " + opModeRegistrarManager.searchForOpMode(name).getName());
                firstTime = false;
            }

            this.onDisabledSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of Run Time -- " + opModeRegistrarManager.getListRunTimeNames().size());
        firstTime = true;
        for(String name : opModeRegistrarManager.getListRunTimeNames()){
            if(firstTime){
                this.runTimeSendable.setDefaultOption(name, opModeRegistrarManager.searchForOpMode(name));
                System.out.println("Added RunTime Default: " + name + " " + opModeRegistrarManager.searchForOpMode(name).getName());
                firstTime = false;
            }

            this.runTimeSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }
    }

    public Class getAutoSendableCurrentVal(){
        return this.autoSendable.getSelected();
    }

    public Class getTeleSendableCurrentVal(){
        return this.teleSendable.getSelected();
    }
    
    public Class getOnDisabledSendableCurrentVal(){
        return this.onDisabledSendable.getSelected();
    }

    public Class getRunTimeCurrentVal(){
        return this.runTimeSendable.getSelected();
    }
}