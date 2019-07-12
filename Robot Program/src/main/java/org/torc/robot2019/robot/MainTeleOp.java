package org.torc.robot2019.robot;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;

import org.torc.robot2019.program.TeleopMode;

@TeleOp(name = "Main TeleOp")
public class MainTeleOp extends OpMode {

    @Override
    public void INIT() {
		TeleopMode.Init();
    }

    @Override
    public void LOOP() {
		TeleopMode.Periodic();
    }

}