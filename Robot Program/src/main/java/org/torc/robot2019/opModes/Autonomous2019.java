package org.torc.robot2019.opModes;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.torc.robot2019.annotation_scanner.OpMode;
import org.torc.robot2019.annotation_scanner.annotations.Autonomous;
import org.torc.robot2019.functions.FileLogger;
import org.torc.robot2019.hardware.BaseDrive;

@Autonomous(name = "Autonomous")
public class Autonomous2019 extends OpMode{

    //Hardware Objects
    public BaseDrive baseDrive = new BaseDrive();
    public FileLogger fileLogger = new FileLogger(5);

    @Override
    public void run() {
    
        fileLogger.
        baseDrive.init(fileLogger, 10, 12, 11, 13, MotorType.kBrushless);
        
        while(opModeIsActive()){
            
        }
    }
}