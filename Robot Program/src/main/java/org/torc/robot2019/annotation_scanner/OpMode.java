package org.torc.robot2019.annotation_scanner;

public abstract class OpMode extends RobotStateTracker implements Runnable {
    public abstract void run();

    public boolean opModeIsActive(){
        return runningThread.isAlive();
    }

    public void waitForBooleanEvent(OnEvent onEvent){
        long runTime = System.currentTimeMillis();
        
        /*Loop until user sends true and while doing this call a function a user defines*/
        while(!onEvent.waitForEvent(runTime));
    }

    public interface OnEvent {
        public abstract boolean waitForEvent(long runTime);
    }
}
