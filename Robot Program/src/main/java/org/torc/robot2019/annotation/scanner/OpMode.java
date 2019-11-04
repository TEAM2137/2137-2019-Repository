package org.torc.robot2019.annotation.scanner;

import org.torc.robot2019.libraries.Constants.stepState;

public abstract class OpMode implements Runnable {
    private final String RoboRioWorkingPath = "/home/lvuser/";

    public abstract void run();

    public boolean opModeIsActive(){
        return !Thread.currentThread().isInterrupted();
    }

    public String getRoboRioWorkingPath(){
        return this.RoboRioWorkingPath;
    }

    private stepState stateMachineStepState = stepState.STATE_INIT;

    /**
     * This is not for use and only for if anyone that has to have it.
     * It is also here as a teaching tool because then you can forget to fill one of the states.
     */
    @Deprecated
    public void executeStateMachine(StateMachineInterface stateMachineInterface, boolean INIT){
        if(INIT)
            stateMachineStepState = stepState.STATE_INIT;

        switch(stateMachineStepState){
            case STATE_INIT:
                stateMachineInterface.INIT();
                this.stateMachineStepState = stepState.STATE_START;
                break;
            case STATE_START:
                stateMachineInterface.START();
                this.stateMachineStepState = stepState.STATE_RUNNING;
                break;
            case STATE_RUNNING:
                if(stateMachineInterface.RUNNING())
                    this.stateMachineStepState = stepState.STATE_FINISHED;
                break;
            case STATE_FINISHED:
                stateMachineInterface.FINISHED();
                break;
            case STATE_TIMEOUT:
                stateMachineInterface.TIMEOUT();
                break;                    
        }
    }
}