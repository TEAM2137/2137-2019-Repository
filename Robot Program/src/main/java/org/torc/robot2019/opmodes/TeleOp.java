package org.torc.robot2019.opmodes;

import org.torc.robot2019.annotation.scanner.OpMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleOp extends OpMode {

    @Override
    public void run() {
        System.out.println("Running the Main TeleOp");
        SmartDashboard.putNumber("Number", 1);
    }
}
