// LinearOpMode showing use of a thread to control functions.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class AutoSample2 extends LinearOpMode
{
    DcMotor leftMotor;
    DcMotor rightMotor;
    Thread  myThread;

    // constructor.
    public AutoSample2() throws Exception
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

        myThread = new TestThread();

        Util.telemetry("Mode", "waiting");
        Util.log("waiting");

        // wait for start button.
        waitForStart();

        Util.telemetry("Mode", "running");
        Util.log("%s", "running");

        try
        {
            myThread.run();

            while (opModeIsActive())
            {
                sleep(100);
            }
        }
        catch (InterruptedException e) {}
        catch (Exception e) {e.printStackTrace(Logging.logPrintStream);}

        Util.log("after loop");
        Util.telemetry("Mode", "after loop");

        myThread.interrupt();

        rightMotor.setPower(0);
        leftMotor.setPower(0);

        Util.log("done");
    }

    // TestThread thread class.

    private class TestThread extends Thread
    {
        private DcMotor _leftMotor;
        private DcMotor _rightMotor;

        TestThread()
        {
            this.setName("TestThread");

            Util.log("%s", this.getName());

            _leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
            _rightMotor = hardwareMap.dcMotor.get("M_driveRight");
            _rightMotor.setDirection(DcMotor.Direction.REVERSE);
        }

        public void run()
        {
            Util.log("%s", this.getName());

            try
            {
                while (!isInterrupted())
                {
                    _rightMotor.setPower(gamepad1.right_stick_y);
                    _leftMotor.setPower(gamepad1.left_stick_y);

                    if (gamepad1.left_stick_y != 0.0) Util.log("left stick=%f", gamepad1.left_stick_y);

                    sleep(100);
                }
            }
            catch (InterruptedException e) {}
            catch (Throwable e) {e.printStackTrace(Logging.logPrintStream);}

            Util.log("end of %s", this.getName());
        }
    }	// end of TestThread thread class.
}
