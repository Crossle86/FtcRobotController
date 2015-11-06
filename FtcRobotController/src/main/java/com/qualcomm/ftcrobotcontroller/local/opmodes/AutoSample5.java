/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more linearopmode classes. See LinearOpmodeBase.
This also demonstrates using the DSMenu class to display and select options on the controller
during INIT mode.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.DSDashboard;
import com.qualcomm.ftcrobotcontroller.local.lib.DSMenu;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;

public class AutoSample5 extends LinearOpmodeBase implements DSMenu.MenuButtons
{
    public AutoSample5() throws  Exception
    {
        super();
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        Util.log();

        super.runOpMode();

        // do whatever else is needed for initialization.

        // select auto program or a program option.
        Util.log("selecting auto program");

        AppUtil.playSound(R.raw.nxtstartup);

        // Have to create a dashboard as the DSMenu will use it via static references.
        DSDashboard dashboard = new DSDashboard(telemetry);

        DSMenu menu = new DSMenu("Menu Title", this);

        menu.addChoice("Choice 1", Double.valueOf(1));
        menu.addChoice("Choice 2", Double.valueOf(2));
        menu.addChoice("Choice 3", Double.valueOf(3));

        // Stays in this call until you press A (accept selection) or B (cancel).
        int choice = menu.getChoice();

        Double choiceValue = (Double) menu.getChoiceValue(choice);

        telemetry.clearData();

        if (choice == -1)
        {
            Util.telemetry("Menu selection", "%d", choice);
            Util.log("Menu selection: %d", choice);
        }
        else
        {
            Util.telemetry("Menu selection", "%d=%s", choice, choiceValue.toString());
            Util.log("Menu selection: %d=%s", choice, choiceValue.toString());
        }

        AppUtil.playSound(R.raw.nxtstartup);

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

    // Implements MenuButtons interface.
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
