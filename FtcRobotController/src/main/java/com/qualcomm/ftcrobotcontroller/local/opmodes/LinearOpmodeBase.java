/*
Example of how to create a base class with common initialization or utility functions and
then extend that in one or more linearopmode classes. See AutoSample3.
 */
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class LinearOpmodeBase extends LinearOpMode
{
    DcMotor leftMotor;
    DcMotor rightMotor;

    // Class constructor.
    public LinearOpmodeBase() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();

        Util.log();
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        AppUtil.setControllerApp(hardwareMap.appContext);

        Util.log(AppUtil.getVersion());

        leftMotor = hardwareMap.dcMotor.get("M_driveLeft");
        rightMotor = hardwareMap.dcMotor.get("M_driveRight");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }
}
