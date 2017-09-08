// simple autonomous program that drives bot in a square pattern then ends.
// this code assumes it will end before the period is over but if the period ended while
// still driving, this code would just stop.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class AutoSample extends LinearOpMode
{
    DcMotor leftMotor;
    DcMotor rightMotor;

    // constructor
    public AutoSample() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();
    }

    // called when init button is  pressed.
    @Override
    public void runOpMode() throws InterruptedException
    {
        leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
        rightMotor = hardwareMap.dcMotor.get("M_driveRight");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        Util.telemetry("Mode", "waiting");
        Util.log("waiting");

        // wait for start button.
        waitForStart();

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
