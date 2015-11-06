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
public class PushBotSquare_RAC extends LinearOpMode {
    DcMotor leftMotor;
    DcMotor rightMotor;
    Thread  myThread;

    public PushBotSquare_RAC() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
        rightMotor = hardwareMap.dcMotor.get("M_driveRight");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        //leftMotor.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        //rightMotor.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        myThread = new TestThread();

        Util.telemetry("Mode", "waiting");
        Util.log("waiting");

        waitForStart();

        Util.telemetry("Mode", "running");
        Util.log("%s", "running");

//        for(int i=0; i<4; i++)
//        {
//            leftMotor.setPower(0.25);
//            rightMotor.setPower(0.25);
//
//            sleep(1000);
//
//            leftMotor.setPower(0.0);
//            rightMotor.setPower(0.0);
//
//            sleep(500);
//
//            leftMotor.setPower(0.25);
//            rightMotor.setPower(-0.25);
//
//            sleep(500);
//
//            leftMotor.setPower(0.0);
//            rightMotor.setPower(0.0);
//
//            sleep(500);
//        }

        leftMotor.setPowerFloat();
        rightMotor.setPowerFloat();

            leftMotor.setPower(1.0);
            rightMotor.setPower(1.0);

            sleep(3000);

            leftMotor.setPower(0.0);
            rightMotor.setPower(0.0);

        Util.telemetry("Mode", "done auto");
        Util.log();
        Util.log("done auto");

        try
        {
            myThread.run();

            while (opModeIsActive())
            {
//                rightMotor.setPower(gamepad1.right_stick_y);
//                leftMotor.setPower(gamepad1.left_stick_y);
//
//                if (gamepad1.left_stick_y != 0.0) Util.log("left stick=%f", gamepad1.left_stick_y);
//
//                try
//                {
//                    if (gamepad1.right_stick_y != 0.0) Util.log("left stick=%d", gamepad1.right_stick_y);
//                }
//                catch (Exception e)
//                {
//                    Util.log("caught inner exception");
//                    Util.log("inner exception=%s", e.toString());
//                    e.printStackTrace(Logging.logPrintStream);
//                }

                sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            Util.log("caught interrupted exception");
        }
        catch (Exception e)
        {
            Util.log("caught exception: %s", e.toString());
            e.printStackTrace(Logging.logPrintStream);
        }

        Util.log("after loop");
        Util.telemetry("Mode", "after loop");

        myThread.interrupt();

        rightMotor.setPower(0);
        leftMotor.setPower(0);

        //sleep(5000);
        Util.telemetry("Mode", "done");
        Util.log();
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
            Util.telemetry("mode", "thread %s running", this.getName());

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
