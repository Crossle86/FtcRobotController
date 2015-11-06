/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more linearopmode classes. See LinearOpmodeBase.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Util;

public class AutoSample3 extends LinearOpmodeBase
{
    public AutoSample3() throws  Exception
    {
        super();
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        Util.log();

        super.runOpMode();

        // do whatever else is needed for initialization.

        // Wait for start button.
        Util.telemetry("Mode", "waiting");
        Util.log("waiting");

        waitForStart();

        // Start pressed, off and running.
        Util.telemetry("Mode", "running");
        Util.log("%s", "running");

        for(int i=0; i<4; i++)
        {
            leftMotor.setPower(0.25);
            rightMotor.setPower(0.25);

            sleep(1000);

            leftMotor.setPower(0.0);
            rightMotor.setPower(0.0);

            sleep(500);

            leftMotor.setPower(0.25);
            rightMotor.setPower(-0.25);

            sleep(500);

            leftMotor.setPower(0.0);
            rightMotor.setPower(0.0);

            sleep(500);
        }

        rightMotor.setPower(0);
        leftMotor.setPower(0);

        Util.telemetry("Mode", "done");
        Util.log();
        Util.log("done");
    }
}
