// Sample LinearOpMode showing how to manipulate the controller app user interface.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.ftcrobotcontroller.R;

import android.graphics.Color;
import android.view.View;
import android.widget.*;

public class ColorSample extends LinearOpMode
{
    DcMotor leftMotor;
    DcMotor rightMotor;

    public ColorSample() throws Exception
    {
        Util.currentOpMode = this;
        Logging.MyLogger.setup();
        Util.log();
    }

    // called when init button is pressed.
    @Override
    public void runOpMode() throws InterruptedException
    {
        leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
        rightMotor = hardwareMap.dcMotor.get("M_driveRight");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        AppUtil.setControllerApp(hardwareMap.appContext);

        //final View controllerMain = AppUtil.findView(R.id.display_area);
        final View controllerTop = AppUtil.findView(R.id.top_bar);
        final TextView controllerFileName = AppUtil.findTextView(R.id.active_filename);
        final TextView controllerOpMode = AppUtil.findTextView(R.id.textOpMode);

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

                if (gamepad1.x)
                {
                    //AppUtil.setBackgroundColor(controllerMain, Color.BLUE);

                    AppUtil.setBackgroundColor(controllerTop, Color.GREEN);

                    AppUtil.setBackgroundColor(controllerFileName, Color.YELLOW);

                    AppUtil.appendText(controllerFileName, "-rich");

                    AppUtil.setTextColor(controllerFileName, Color.RED);

                    AppUtil.setTextColor(controllerOpMode, Color.RED);

                    AppUtil.appendText(controllerOpMode, "rich");
                }

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
