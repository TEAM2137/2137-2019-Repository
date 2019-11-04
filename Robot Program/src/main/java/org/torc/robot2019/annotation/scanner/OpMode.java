package org.torc.robot2019.annotation.scanner;

public abstract class OpMode implements Runnable {
    private final String RoboRioWorkingPath = "/home/lvuser/";

    public abstract void run();

    public boolean opModeIsActive(){
        return !Thread.currentThread().isInterrupted();
    }

    public String getRoboRioWorkingPath(){
        return this.RoboRioWorkingPath;
    }
}
