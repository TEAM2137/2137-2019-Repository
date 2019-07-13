package org.torc.robot2019.annotation_scanner;

import org.torc.robot2019.annotation_scanner.annotations.*;
import org.torc.robot2019.annotation_scanner.filters.OpModeRegistrarManager;

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
        for(String name : opModeRegistrarManager.getListAutonomousNames()){
            this.autoSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of TeleOp -- " + opModeRegistrarManager.getListTeleOpNames().size());
        for(String name : opModeRegistrarManager.getListTeleOpNames()){
            this.teleSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of Disabled -- " + opModeRegistrarManager.getListOnDisabledNames().size());
        for(String name : opModeRegistrarManager.getListOnDisabledNames()){
            this.onDisabledSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }

        System.out.println("Amount of Run Time -- " + opModeRegistrarManager.getListRunTimeNames().size());
        for(String name : opModeRegistrarManager.getListRunTimeNames()){
            this.runTimeSendable.addOption(name, opModeRegistrarManager.searchForOpMode(name));
        }
    }

    public Class getAutoSendableCurrentVal(){
        if(this.autoSendable.getSelected() != null)
            return this.autoSendable.getSelected();
        else
            return DefaultAutonomous.class;    
    }

    public Class getTeleSendableCurrentVal(){
        if(this.teleSendable.getSelected() != null)
            return this.teleSendable.getSelected();
        else
            return DefaultTeleOp.class;    
    }
    
    public Class getOnDisabledSendableCurrentVal(){
        if(this.onDisabledSendable.getSelected() != null)
            return this.onDisabledSendable.getSelected();
        else
            return DefaultOnDisbaled.class;
    }

    public Class getRunTimeCurrentVal(){
        if(this.runTimeSendable.getSelected() != null)
            return this.runTimeSendable.getSelected();
        else
            return DefaultRunTime.class;
    }
}