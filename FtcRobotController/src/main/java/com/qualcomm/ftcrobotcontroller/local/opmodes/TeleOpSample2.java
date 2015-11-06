/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more opmode classes. See OpmodeBase.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class TeleOpSample2 extends OpmodeBase
{
    public TeleOpSample2() throws Exception
    {
        super();
    }

    @Override
    public void init()
    {
        super.init();
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
