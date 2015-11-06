/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more opmode classes. See OpmodeBase.
Also demonstrates enums and using the controller to input options during INIT phase.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.Arrays;
import java.util.List;

public class TeleOpSample3 extends OpmodeBase
{
    private String      option = "";
    private Options     enumOption = Options.Option_1;
    private boolean     xPressed, yPressed;

    public enum Options
    {
        Option_1,
        Option_2,
        Option_3;

        // Generic enum navigation functions.
        public static List<Options> list = Arrays.asList(values());
        public static int count() {return list.size();}
        public static Options get(int index) {return list.get(index);}
        public static Options first() {return list.get(0);}
        public Options next() {return list.get((this.ordinal() + 1) % list.size());}
        public Options prev() {return list.get((this.ordinal() -1 + list.size()) % list.size());}
    }

    public TeleOpSample3() throws Exception
    {
        super();
    }

    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public void init_loop()
    {
        Util.log(enumOption.toString());

        // demonstrate selection options via gamepad buttons.

        if (gamepad1.a) option = "A";
        if (gamepad1.b) option = "B";

        // These if statements latch on button pressed, take action on button release. This gets
        // rid of button read errors because of how many times this loop happens while the user
        // just clicks the button.

        if (gamepad1.x)
            xPressed = true;
        else if (xPressed)
        {
            enumOption = enumOption.next();
            xPressed = false;
        }

        if (gamepad1.y)
            yPressed = true;
        else if (yPressed)
        {
            enumOption = enumOption.prev();
            yPressed = false;
        }

        Util.telemetry("Options", "String=%s   Enum=%s", option, enumOption.toString());
    }

    //
    // This method will be called repeatedly in a loop
    // @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
    //
    @Override
    public void loop()
    {
        String selectedEnumOption = "";

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

        // Employ the enum option set in init_loop with if and switch statements.

        if (enumOption == Options.Option_1) selectedEnumOption = "Option 1";

        switch (enumOption)
        {
            case Option_2:
                selectedEnumOption = "Option 2";
                break;

            case Option_3:
                selectedEnumOption = "Option 3";
                break;
        }

        // display telemetry on DS. line labels are sorted.
        Util.telemetry("1-LeftGP", "LY=%.2f LP=%.2f  RY=%.2f RP=%.2f", gamepad1.left_stick_y, M_driveLeftPower, gamepad1.right_stick_y, M_driveRightPower);
        Util.telemetry("Buttons", "t=%b x=%b a=%b b=%b y=%b", I_touch.isPressed(), gamepad1.x, gamepad1.a, gamepad1.b, gamepad1.y);
        Util.telemetry("UpDwn Servo", "Sup=%.2f real=%.2f", A_upDownServoPos, S_upDown.getPosition());
        Util.telemetry("Sidew Servo", "Sup=%.2f real=%.2f", A_sideSideServoPos, S_sideSide.getPosition());
        Util.telemetry("Options", "String=%s   Enum=%s  SelEnum=%s", option, enumOption.toString(), selectedEnumOption);

        Util.telemetry("Outside Loop Time", Long.toString(startTime - lastLoopTime));

        long endTime = System.currentTimeMillis();
        Util.telemetry("Loop Time", Long.toString(endTime - startTime));

        lastLoopTime = endTime;
    }
}
