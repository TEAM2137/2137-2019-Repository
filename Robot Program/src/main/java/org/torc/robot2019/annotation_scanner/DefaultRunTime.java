package org.torc.robot2019.annotation_scanner;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.Disabled;
import org.torc.robot2019.annotation_scanner.annotations.RunTime;

@RunTime(name = "Default Run Time")
@Disabled
public class DefaultRunTime extends OpMode{

    @Override
    public void run() {

    }

}
