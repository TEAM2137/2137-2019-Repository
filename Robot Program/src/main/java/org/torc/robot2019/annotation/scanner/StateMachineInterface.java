package org.torc.robot2019.annotation.scanner;

public interface StateMachineInterface {
    public abstract void INIT();
    public abstract void START();
    public abstract boolean RUNNING();
    public abstract void FINISHED();
    public abstract void TIMEOUT();
}
