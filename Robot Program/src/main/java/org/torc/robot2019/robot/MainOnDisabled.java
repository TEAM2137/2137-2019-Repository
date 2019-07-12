package org.torc.robot2019.robot;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;
import org.torc.robot2019.program.DisabledMode;

@OnDisabled(name = "Main On Disabled")
public class MainOnDisabled extends OpMode {

    @Override
    public void INIT() {
		DisabledMode.Init();
    }

    @Override
    public void LOOP() {
		DisabledMode.Periodic();
    }

}