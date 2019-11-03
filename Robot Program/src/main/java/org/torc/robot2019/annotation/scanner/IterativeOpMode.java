package org.torc.robot2019.annotation.scanner;

public abstract class IterativeOpMode extends OpMode{

    @Override
    public void run() {
        init();

        while(opModeIsActive()){
            loop();
        }

        stop();
    }

    public abstract void init();
    public abstract void loop();
    public abstract void stop();    
}
