package org.torc.robot2019.annotation.scanner;

public abstract class OpMode implements Runnable {
    public abstract void run();

    public boolean opModeIsActive(){
        return !Thread.currentThread().isInterrupted();
    }
}
