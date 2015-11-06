// Sample LinearOpMode showing loop process until auto period is over.
// We demo loop by driving. LinearOpMode was conceived to help with autonomous but can be used for
// teleop as well, as this sample  shows.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * An example linear op mode where the pushbot
 * will drive in a square pattern using sleep()
 * and a for loop.
 */
public class AutoSample1 extends LinearOpMode
{
    DcMotor leftMotor;
    DcMotor rightMotor;

    public AutoSample1() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();
    }

    // called when init button is pressed.
    @Override
    public void runOpMode() throws InterruptedException
    {
        leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
        rightMotor = hardwareMap.dcMotor.get("M_driveRight");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        Util.telemetry("Mode", "waiting");
        Util.log("waiting");

        waitForStart();

        Util.telemetry("Mode", "running");
        Util.log("%s", "running");

        try
        {
            while (opModeIsActive())
            {
                rightMotor.setPower(gamepad1.right_stick_y);
                leftMotor.setPower(gamepad1.left_stick_y);

                if (gamepad1.left_stick_y != 0.0) Util.log("left stick=%f", gamepad1.left_stick_y);

                //sleep(100);
                waitOneFullHardwareCycle();
            }
        }
        // interrupted means time to stop. Note we can stop due to opModeIsActive going false or
        // interrupted being thrown by sleep or wait function.
        catch (InterruptedException e) {Util.log("Interrupted");}
        catch (Exception e) {e.printStackTrace(Logging.logPrintStream);}

        Util.log("after loop");
        Util.telemetry("Mode", "after loop");

        rightMotor.setPower(0);
        leftMotor.setPower(0);

        Util.log("done");
    }
}
