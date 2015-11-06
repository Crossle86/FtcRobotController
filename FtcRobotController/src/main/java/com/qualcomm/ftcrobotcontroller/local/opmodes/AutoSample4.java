/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more linearopmode classes. See LinearOpmodeBase.
This also demonstrates using enums and how you can set options with
the controller during INIT mode.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.DSDashboard;
import com.qualcomm.ftcrobotcontroller.local.lib.DSMenu;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;

import java.util.Arrays;
import java.util.List;

public class AutoSample4 extends LinearOpmodeBase implements DSMenu.MenuButtons
{
    private String      option = "";
    private Options     enumOption = Options.Option_1;
    private boolean     downPressed, upPressed, donePressed;

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

    public AutoSample4() throws  Exception
    {
        super();
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        boolean optionSelected = false;

        Util.log();

        super.runOpMode();

        // do whatever else is needed for initialization.

        // demonstrate selection options via gamepad buttons.

        while (!optionSelected)
        {
            if (gamepad1.a) option = "A";
            if (gamepad1.b) option = "B";

            // These if statements latch on button pressed, take action on button release. This gets
            // rid of button read errors because of how many times this loop happens while the user
            // just clicks the button.

            if (gamepad1.dpad_down)
                downPressed = true;
            else if (downPressed)
            {
                enumOption = enumOption.next();
                downPressed = false;
            }

            if (gamepad1.dpad_up)
                upPressed = true;
            else if (upPressed)
            {
                enumOption = enumOption.prev();
                upPressed = false;
            }

            if (gamepad1.x)
                donePressed = true;
            else if (donePressed)
            {
                optionSelected = true;
                donePressed = false;
            }

            Util.telemetry("Options", "String=%s   Enum=%s", option, enumOption.toString());

            sleep(25);
        }

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

    // Implements MenuButtons
    //
    public boolean isMenuUp()
    {
        return gamepad1.dpad_up;
    }   //isMenuUp

    public boolean isMenuDown()
    {
        return gamepad1.dpad_down;
    }   //isMenuDown

    public boolean isMenuOk()
    {
        return gamepad1.a;
    }   //isMenuEnter

    public boolean isMenuCancel()
    {
        return gamepad1.b;
    }   //isMenuCancel
}
