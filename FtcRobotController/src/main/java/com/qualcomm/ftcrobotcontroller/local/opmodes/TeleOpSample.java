// Demonstrates simple teleop program. Shows motor/servo operation.
// Shows usage of logging and telemetry utility classes.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
//import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

//
// TeleOp Mode
// Enables control of the robot via the gamepad
//
public class TeleOpSample extends OpMode
{
    // hardware declarations
    DcMotorController wheelController;
    DcMotorController.DeviceMode devMode;
    DcMotor M_driveRight;
    DcMotor M_driveLeft;
    TouchSensor I_touch;
    //GyroSensor I_gyro;
    Servo S_upDown;
    Servo S_sideSide;

    // variable declarations

    private long    lastLoopTime = 0;
    final float DEAD_ZONE = 0.1f;
    float M_driveRightPower = 0.0f;
    float M_driveLeftPower = 0.0f;
    double A_upDownServoPos = 0.5d;
    double A_sideSideServoPos = 0.5d;

    // class constructor
    public TeleOpSample() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();

        Util.log();
    }

    //
    // Code to run when the op mode is first enabled goes here
    // @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
    //
    @Override
    public void init()
    {
        Util.log();

        wheelController = hardwareMap.dcMotorController.get("Motor Controller 2");
        M_driveRight = hardwareMap.dcMotor.get("M_driveRight");
        M_driveLeft = hardwareMap.dcMotor.get("M_driveLeft");
        S_upDown = hardwareMap.servo.get("S_upDown");
        S_sideSide = hardwareMap.servo.get("S_sideSide");
        //I_gyro = hardwareMap.gyroSensor.get("I_gyro");
        devMode = DcMotorController.DeviceMode.WRITE_ONLY;
        M_driveRight.setDirection(DcMotor.Direction.REVERSE);
        M_driveRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        M_driveLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        M_driveRight.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        M_driveLeft.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // set sensors.
        I_touch = hardwareMap.touchSensor.get("I_touch");

        // initialize stuff.
        gamepad1.setJoystickDeadzone(DEAD_ZONE);
    }

    // called when period start button is pressed.
    @Override
    public void start()
    {
        Util.log();
    }

    // called when period stop button is pressed.
    @Override
    public void stop()
    {
        Util.log();

        M_driveRight.setPower(0);
        M_driveLeft.setPower(0);
    }

    //
    // This method will be called repeatedly in a loop
    // @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
    //
    @Override
    public void loop()
    {
        long startTime = System.currentTimeMillis();

        M_driveRightPower = gamepad1.right_stick_y;
        M_driveLeftPower = gamepad1.left_stick_y;

        if(gamepad1.a && A_upDownServoPos > Servo.MIN_POSITION)
            A_upDownServoPos -= 0.01d;
        else if (gamepad1.b && A_upDownServoPos < Servo.MAX_POSITION)
            A_upDownServoPos += 0.01d;

        if(gamepad1.x && A_sideSideServoPos < Servo.MAX_POSITION)
            A_sideSideServoPos += 0.01d;
        else if(gamepad1.y && A_sideSideServoPos > Servo.MIN_POSITION)
            A_sideSideServoPos -= 0.01d;

        // setting hardware
        M_driveRight.setPower(M_driveRightPower);
        M_driveLeft.setPower(M_driveLeftPower);

        S_sideSide.setPosition(Range.clip(A_sideSideServoPos, 0.0, 1.0));
        S_upDown.setPosition(Range.clip(A_upDownServoPos, 0.0, 1.0));

        // display telemetry on DS. line labels are sorted.
        Util.telemetry("1-LeftGP", "LY=%.2f LP=%.2f  RY=%.2f RP=%.2f", gamepad1.left_stick_y, M_driveLeftPower, gamepad1.right_stick_y, M_driveRightPower);
        Util.telemetry("Buttons", "t=%b x=%b a=%b b=%b y=%b", I_touch.isPressed(), gamepad1.x, gamepad1.a, gamepad1.b, gamepad1.y);
        Util.telemetry("UpDwn Servo", "Sup=%.2f real=%.2f", A_upDownServoPos, S_upDown.getPosition());
        Util.telemetry("Sidew Servo", "Sup=%.2f real=%.2f", A_sideSideServoPos, S_sideSide.getPosition());

        Util.telemetry("Outside Loop Time", Long.toString(startTime - lastLoopTime));

        long endTime = System.currentTimeMillis();
        Util.telemetry("Loop Time", Long.toString(endTime - startTime));

        lastLoopTime = endTime;
    }
}

