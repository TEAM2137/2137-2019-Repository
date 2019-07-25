package org.torc.robot2019.annotation_scanner;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.*;

/**
 * Do NOT Delet this because this is always default
 */
@OnDisabled(name = "Default Disbaled")
@Disabled //This is oeverrided by the main software
public class DefaultOnDisbaled extends OpMode {

    @Override
    public void run() {

    }
}
