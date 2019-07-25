package org.torc.robot2019.annotation_scanner;

public abstract class OpMode extends RobotStateTracker implements Runnable {
    private int debugLevel = 5;

    public abstract void run();

    public boolean opModeIsActive(){
        return runningThread.isAlive();
    }

    public void waitForBooleanEvent(OnBooleanEvent onEvent){
        long runTime = System.currentTimeMillis();
        
        /*Loop until user sends true and while doing this call a function a user defines*/
        while(!onEvent.waitForEvent(runTime));
    }

    public void waitForEqualDoubleEvent(OnDoubleEvent onEvent, double goal){
        long runTime = System.currentTimeMillis();

        while(onEvent.waitForEvent(runTime) == goal);
    }

    public void waitForLessDoubleEvent(OnDoubleEvent onEvent, double goal){
        long runTime = System.currentTimeMillis();

        while(onEvent.waitForEvent(runTime) >= goal);
    }

    public void waitForGreaterDoubleEvent(OnDoubleEvent onEvent, double goal){
        long runTime = System.currentTimeMillis();

        while(onEvent.waitForEvent(runTime) <= goal);
    }

    public interface OnBooleanEvent {
        public abstract boolean waitForEvent(long runTime);
    }

    public interface OnDoubleEvent {
        public abstract double waitForEvent(long runTime);
    }

    public int getRobotDebug(){
        return debugLevel;
    }

    public void setRobotDebug(int debugLevel){
        this.debugLevel = debugLevel;
    }
}
