/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more opmode classes. See TeleOpSample2.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class OpmodeBase extends OpMode
{
    // hardware declarations
    DcMotorController wheelController;
    DcMotorController.DeviceMode devMode;
    DcMotor M_driveRight;
    DcMotor M_driveLeft;
    TouchSensor I_touch;
    Servo S_upDown;
    Servo S_sideSide;

    // variable declarations

    public long lastLoopTime = 0;
    final float DEAD_ZONE = 0.1f;
    float M_driveRightPower = 0.0f;
    float M_driveLeftPower = 0.0f;
    double A_upDownServoPos = 0.5d;
    double A_sideSideServoPos = 0.5d;

    // class constructor
    public OpmodeBase() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();

        Util.log();
    }

    // Called when INIT button is pressed.
    public void init()
    {
        AppUtil.setControllerApp(hardwareMap.appContext);

        Util.log(AppUtil.getVersion());

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

    @Override
    public void loop()
    {
    }
}