/*
Copyright (c) 2015 William Gardner
Copyright (c) 2015 Richard Corn (modifications)

        Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
        documentation files (the "Software"), to deal in the Software without restriction, including without limitation
        the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
        to permit persons to whom the Software is furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all copies or substantial portions of
        the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
        THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
        TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
*/

/**
 * Simple teleop program that demonstrates accessing the controller phone camera through the
 * CameraManager library class.
 * It demonstrates a simple inspection of camera image pixels to determine the predominant
 * color in the image. Could use it to look for red/blue light.
 */

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.ftcrobotcontroller.local.lib.camera.CameraManager;
import com.qualcomm.ftcrobotcontroller.local.lib.I2CDeviceReader;
import com.qualcomm.ftcrobotcontroller.local.lib.I2CDeviceReader.I2CReadCompletedCallback;
import com.qualcomm.ftcrobotcontroller.local.lib.I2CDeviceWriter;
import com.qualcomm.ftcrobotcontroller.local.lib.I2CDeviceWriter.I2CWriteCompletedCallback;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDevice;

public class DetectColor extends OpMode
{
    private CameraManager   camMgr;
    private ColorSensor colorSensor;
    private RelativeLayout  relativeLayout;
    private I2cDevice       i2cDevice;
    private I2CDeviceReader i2cReader;
    private I2CDeviceWriter i2cWriter;
    private int     ds2 = 2;    // additional downsampling of the image
                                // set to 1 to disable further downsampling

    private long    lastLoopTime = 0;

    public DetectColor() throws Exception
    {
        Util.currentOpMode = this;

        Logging.MyLogger.setup();

        Util.log();
    }

    @Override
    public void init()
    {
        Util.log();

        AppUtil.setControllerApp(hardwareMap.appContext);

        Util.logHardwareDevices(hardwareMap);

        //colorSensor = hardwareMap.colorSensor.get("MR_Color");

        //colorSensor.enableLed(false);

        camMgr = new CameraManager(hardwareMap.appContext);

        camMgr.setCameraDownsampling(8);
        // parameter determines how downsampled you want your images
        // 8, 4, 2, or 1.
        // higher number is more downsampled, so less resolution but faster
        // 1 is original resolution, which is detailed but slow.

        relativeLayout = (RelativeLayout) AppUtil.findView(R.id.RelativeLayout);

        camMgr.addPreviewLayout((ViewGroup) relativeLayout);

        // Create i2c objects to test i2c i/o.

        i2cDevice = hardwareMap.i2cDevice.get("MR_Color");

        Util.log("i2cdevice name=%s", Util.getDeviceUserName(hardwareMap.i2cDevice, i2cDevice));

        i2cReader = new I2CDeviceReader(i2cDevice); //, 0x3c, 0x04, 1);

        i2cReader.registerReadCompletedCallback(new i2cReadCompleted());

        i2cWriter = new I2CDeviceWriter(i2cDevice);

        i2cWriter.registerWriteCompletedCallback(new i2cWriteCompleted());

        i2cWriter.startWrite(0x3c, 0x03, 0);
        i2cWriter.waitForDone();
    }

    @Override
    public void loop()
    {
        long startTime = System.currentTimeMillis();
        String colorString = "", colorString1 = "";
        int color = Color.WHITE;
        byte [] colorData = {0xF};

        try
        {
            if (camMgr.imageReady())
            { // only do this if an image has been returned from the camera
                Bitmap rgbImage = camMgr.getBitMap(ds2);

                color = 0; // camMgr.predominateRGBColor(rgbImage, 100);

                switch (color)
                {
                    case Color.RED:
                        colorString = "RED";
                        break;
                    case Color.GREEN:
                        colorString = "GREEN";
                        break;
                    case Color.BLUE:
                        colorString = "BLUE";
                        break;
                    default:
                        colorString = "UNKNOWN";
                }
            } else
                colorString = "NONE";

            Util.telemetry("Color", "Color detected is: " + colorString);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
        }

//        try
//        {
//            // Modern Robotix Color Sensor returns very low values for colors. The predominateRGBColor
//            // function expects color component values in the 0-255 range (color-int) as returned by
//            // the ZTE camera. The color sensor does not return color-ints despite that suggestion
//            // by the sensor doc. The component values are very low 0-5 and so the component value
//            // threshold does not work as well to only identify predom color based on how high its
//            // value is.
//
//            color = camMgr.predominateRGBColor(colorSensor.argb(), 1);
//
//            switch (color)
//            {
//                case Color.RED:
//                    colorString1 = "RED";
//                    break;
//                case Color.GREEN:
//                    colorString1 = "GREEN";
//                    break;
//                case Color.BLUE:
//                    colorString1 = "BLUE";
//                    break;
//                default:
//                    colorString1 = "UNKNOWN";
//            }
//
//            Util.log("sensor: argb=%d r=%d g=%d b=%d a=%d", colorSensor.argb(), colorSensor.red(), colorSensor.green(), colorSensor.blue(), colorSensor.alpha());
//            Util.telemetry("Color1", "Color detected is: %s;c=%s;argb=%d", colorString1, color, colorSensor.argb());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace(Logging.logPrintStream);
//        }
//
//        AppUtil.setBackgroundColor(relativeLayout, color);

        // Test i2c functions.

        i2cReader.startRead(0x3c, 0x04, 1);

        //colorData = i2cReader.getReadBuffer();
        colorData = i2cReader.waitForData();

        if (colorData != null) Util.log("colorData=%s", ((Byte) colorData[0]).toString());

        // Tests of various utility functions.

        String testPref = AppUtil.getStringPreference("test","start");

        if (gamepad1.x) AppUtil.showToast("PAD 1 X pressed.");
        if (gamepad1.y) AppUtil.setControllerError("PAD 1 Y pressed.");
        if (gamepad1.a) AppUtil.clearControllerError();
        if (gamepad1.b) AppUtil.setControllerError("");

        if (gamepad2.x) AppUtil.deletePreference("test");

        if (gamepad1.y) colorSensor.enableLed(true);
        if (gamepad1.a) colorSensor.enableLed(false);

        long endTime = System.currentTimeMillis();
        telemetry.addData("Loop Time", Long.toString(endTime - startTime));
        telemetry.addData("Outside Loop Time", Long.toString(startTime - lastLoopTime));
        telemetry.addData("testpref", testPref);
        if (colorData != null) Util.telemetry("colorData", "%s", ((Byte) colorData[0]).toString());

        lastLoopTime = endTime;
    }

    @Override
    public void start()
    {
        Util.log();
        camMgr.startCamera(true);
    }

    @Override
    public void stop()
    {
        Util.log();

        camMgr.stopCamera(); // stops camera functions

        // turn off led.
        i2cWriter.startWrite(0x3c, 0x03, 1);
        i2cWriter.waitForDone();
    }

    public class i2cWriteCompleted implements I2CWriteCompletedCallback
    {
        public void writeCompleted()
        {
            Util.log("i2c write completed!");
        }
    }

    public class i2cReadCompleted implements I2CReadCompletedCallback
    {
        public void readCompleted(byte data[])
        {
            if (data != null) Util.log("read completed data=%s", ((Byte) data[0]).toString());
        }
    }
}
