package org.torc.robot2019.annotation_scanner.filters;

import org.torc.robot2019.annotation_scanner.annotations.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class OpModeRegistrarManager {
    public static AnnotationClassFilter mainClassFilter;
    public static ProjectClassCollector mainClassCollector;
    public List<Class> annotationList = new ArrayList<>();
    public List<String> excludeList = new ArrayList<>();

    public HashMap<Class, Class> OpModeList = new HashMap<>();
    public HashMap<String, Class> listOpMode = new HashMap<>();
    public List<String> listAutonomousNames = new ArrayList<>();
    public List<String> listTeleOpNames = new ArrayList<>();
    public List<String> listRunTimeNames = new ArrayList<>();
    public List<String> listOnDisabledNames = new ArrayList<>();

    public OpModeRegistrarManager(Class main){
        this.excludeList.add("com.google");
        this.excludeList.add("org.checkerframework");
        this.excludeList.add("javax.");

        String path;
        try {
            path = main.newInstance().getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            //File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
            System.out.println(path);
            // path = jarDir.getAbsolutePath();
        } catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Problem With Null Pointer Exception When Getting the Location of the JAR File");
            path = main.getProtectionDomain().getCodeSource().getLocation().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error URI Syntax Expetion When Getting JAR Location");
            path = main.getProtectionDomain().getCodeSource().getLocation().getPath();
        } catch (InstantiationException e) {
            e.printStackTrace();
            path = main.getProtectionDomain().getCodeSource().getLocation().getPath();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Problem with Access to Class");
            path = main.getProtectionDomain().getCodeSource().getLocation().getPath();
        }

        mainClassCollector = new ProjectClassCollector(ProjectClassCollector.SourceCodeStorage.ZIP, path, this.excludeList, false);

        this.annotationList.add(TeleOp.class);
        this.annotationList.add(Autonomous.class);
        this.annotationList.add(Disabled.class);
        this.annotationList.add(RunTime.class);
        this.annotationList.add(OnDisabled.class);

        mainClassFilter = new AnnotationClassFilter(mainClassCollector, annotationList);

        Collection<Class> temp_OpModeList = mainClassFilter.getClassesWithAnnotations();

        System.out.println("Amount of Set Classes to check: " + temp_OpModeList.size());
        for (Class a : temp_OpModeList){
            System.out.println("Get Class Annotation for: " + a.getCanonicalName());

            if (mainClassFilter.getAnnotation(a, Disabled.class) == null) {
                if (a.isAnnotationPresent(TeleOp.class)) {
                    TeleOp teleOp = (TeleOp) a.getAnnotation(TeleOp.class);
                    this.listTeleOpNames.add(teleOp.name());
                    this.listOpMode.put(teleOp.name(), a);

                } else if (a.isAnnotationPresent(Autonomous.class)) {
                    Autonomous autonomous = (Autonomous) a.getAnnotation(Autonomous.class);
                    this.listAutonomousNames.add(autonomous.name());
                    this.listOpMode.put(autonomous.name(), a);

                } else if (a.isAnnotationPresent(RunTime.class)) {
                    RunTime runTime = (RunTime) a.getAnnotation(RunTime.class);
                    this.listRunTimeNames.add(runTime.name());
                    this.listOpMode.put(runTime.name(), a);

                } else if (a.isAnnotationPresent(OnDisabled.class)) {
                    OnDisabled onDisabled = (OnDisabled) a.getAnnotation(OnDisabled.class);
                    this.listOnDisabledNames.add(onDisabled.name());
                    this.listOpMode.put(onDisabled.name(), a);
                } 

                this.OpModeList.put(a, a);
            } else {
                System.out.println(a.getName() + " is Disabled and is not added to an OpMode List");
            }
        }
    }

    public Class searchForOpMode(String name){
        return this.listOpMode.get(name);
    }

    public List<Class> getAnnotationList() {
        return annotationList;
    }

    public List<String> getExcludeList() {
        return excludeList;
    }

    public HashMap<Class, Class> getOpModeList() {
        return OpModeList;
    }

    public HashMap<String, Class> getListOpMode() {
        return listOpMode;
    }

    public List<String> getListAutonomousNames() {
        return listAutonomousNames;
    }

    public List<String> getListTeleOpNames() {
        return listTeleOpNames;
    }

    public List<String> getListOnDisabledNames() {
        return listOnDisabledNames;
    }

    public List<String> getListRunTimeNames() {
        return listRunTimeNames;
    }
}