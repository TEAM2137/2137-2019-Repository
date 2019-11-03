package org.torc.robot2019.opmodes;

import org.torc.robot2019.annotation.scanner.OpMode;
import org.torc.robot2019.annotation.scanner.TeleOpFunction;

//Just to note you can not see the annotation code because it is hiden and you have to go
//to work place settings and unexclude it.

@TeleOpFunction(name = "Concept OpMode")
public class ConceptOpMode extends OpMode {

    @Override
    public void run() {
        System.out.println("Concept OpMode Running");
        //Starting the Program this is the INIT where you an place the set up stuff

        while(opModeIsActive()){
            //This is the loop where you can add the code that you need and
            //make sure to always use opModeIsActive in the loop so that yo
            //u can change programs.
        }

        //This is where you can close every thing
        System.out.println("Ended");
    }
}
