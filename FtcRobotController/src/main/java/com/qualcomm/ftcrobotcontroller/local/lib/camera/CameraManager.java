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

package com.qualcomm.ftcrobotcontroller.local.lib.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.ftcrobotcontroller.R;

import java.io.ByteArrayOutputStream;

/**
 * Manages capturing images fromm the camera using the camera preview call back.
 * Provides some image processing support.
 * Provides support for placing a view on the controller screen that displays the
 * camera preview images.
 */
@SuppressWarnings("deprecation")
public class CameraManager
{
    private Camera camera;
    private CameraPreview preview;

    private int width;
    private int height;
    public YuvImage yuvImage = null;

    volatile private boolean imageReady = false;

    private static Activity controllerApp;

    private int ds = 1; // downsampling parameter

    /**
     * Constructor.
     * @param context hardwareMap.appContext.
     */
    public CameraManager(android.content.Context context)
    {
        controllerApp = (Activity) context;
    }

    // Camera preview call back. Creates and saves YubImage from camera data when camera posts
    // a preview.
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            try
            {
                Camera.Parameters parameters = camera.getParameters();
                width = parameters.getPreviewSize().width;
                height = parameters.getPreviewSize().height;
                yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                imageReady = true;
            } catch (Exception e) {e.printStackTrace(Logging.logPrintStream);}
        }
    };

    /**
     * Determines how downsampled you want your images
     * @param downSampling 8, 4, 2, or 1.
     * higher number is more downsampled, so less resolution but faster
     * 1 is original resolution, which is detailed but slow.
     */
    public void setCameraDownsampling(int downSampling)
    {
        ds = downSampling;
    }

    /**
     * Indicates that at least one image has been captured from the camera preview.
     * @return True if an image is avaliable.
     */
    public boolean imageReady()
    {
        return imageReady;
    }

    /**
     * Determines if a camera is available on this device.
     * @return True if a camera is available.
     */
    public boolean isCameraAvailable()
    {
        int cameraId = -1;
        Camera cam;
        int numberOfCameras = Camera.getNumberOfCameras();

        Util.log();

        for (int i = 0; i < numberOfCameras; i++)
        {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            { // Camera.CameraInfo.CAMERA_FACING_FRONT or BACK
                cameraId = i;
                break;
            }
        }
        try
        {
            cam = Camera.open(cameraId);
        }
        catch (Exception e)
        {
            Util.log("Camera Not Available!");
            return false;
        }

        cam.release();

        return true;
    }

    /**
     * Opens the camera.
     * @param cameraInfoType Indicates front or back facing camera.
     * @return Opened Camera object.
     */
    public Camera openCamera(int cameraInfoType)
    {
        int cameraId = -1;
        Camera cam = null;
        int numberOfCameras = Camera.getNumberOfCameras();

        Util.log();

        for (int i = 0; i < numberOfCameras; i++)
        {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == cameraInfoType)
            { // Camera.CameraInfo.CAMERA_FACING_FRONT or BACK
                cameraId = i;
                break;
            }
        }

        try
        {
            cam = Camera.open(cameraId);
        }
        catch (Exception e) {Util.log("Can't Open Camera");}

        return cam;
    }

    /**
     * Return the red component (value) of a Color-int
     * @param color Color-Int
     * @return Red component 0-255.
     */
    public int red(int color) {return Color.red(color);}

    /**
     * Return the green component (value) of a Color-int
     * @param color Color-Int
     * @return Green component 0-255.
     */
    public int green(int color) {return Color.green(color);}

    /**
     * Return the blue component (value) of a Color-int
     * @param color Color-Int
     * @return Blue component 0-255.
     */
    public int blue(int color) {return Color.blue(color);}

    /**
     * Takes rgb Color-int and indicates if the color is a shade of gray.
     * @param color rgb Color-int.
     * @return true if shade of gray false if not.
     */
    public boolean isGrayShade(int color)
    {
        if (red(color) == green(color) && red(color) == blue(color))
            return true;
        else
            return false;
    }

    /**
     * Takes rgb color values and indicates if the color is a shade of gray.
     * @param red rgb red component.
     * @param green rgb green component.
     * @param blue rgb blue component.
     * @return true if shade of gray false if not.
     */
    public boolean isGrayShade(int red, int green, int blue)
    {
        if (red == green && red == blue)
            return true;
        else
            return false;
    }

    /**
     * Converts Yuvimage to RGB Bitmap
     * @param yuvImage Yuvimage to convert.
     * @param downSample 8, 4, 2, or 1.
     * higher number is more downsampled, so less resolution but faster
     * 1 is original resolution, which is detailed but slow.
     * @return Bitmap image.
     */
    public Bitmap convertYuvImageToRgb(YuvImage yuvImage, int downSample)
    {
        Util.log();

        Bitmap rgbImage;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 0, out);
        byte[] imageBytes = out.toByteArray();

        BitmapFactory.Options opt;
        opt = new BitmapFactory.Options();
        opt.inSampleSize = downSample;

        rgbImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opt);

        return rgbImage;
    }

    /**
     * Get the last camera image posted by the camera preview as a Bitmap.
     * @param downSample 8, 4, 2, or 1.
     * higher number is more downsampled, so less resolution but faster
     * 1 is original resolution, which is detailed but slow. Applied in
     * addition to the global downsampling that may be set.
     * @return Camera preview image.
     */
    public Bitmap getBitMap(int downSample)
    {
        return convertYuvImageToRgb(yuvImage, downSample);
    }

    /**]
     * Return a Color-int that is the predominate color of the image.
     * @param rgbImage Image to process.
     * @return Color-int.
     */
    public int predominateColor(Bitmap rgbImage)
    {
        return averageImageColor(rgbImage, true);
    }

    /**
     * Return a Color-int that is the predominate basic (red, green or blue) color of the image.
     * @param rgbImage Image to process.
     * @param colorThreshold Value 0-255 that predominate color must exceed to be selected.
     * @return Color.RED, Color.GREEN, Color.BLUE or -1 if no color exceeds threshold.
     */
    public int predominateRGBColor(Bitmap rgbImage, int colorThreshold)
    {
        int avgColor = averageImageColor(rgbImage, false);

        return predominateRGBColor(avgColor, colorThreshold);
    }

    /**
     * Return a Color-int that is the predominate basic (red, green or blue) color of a Color-int.
     * @param colorRGB Color-int to process.
     * @param colorThreshold Value 0-255 that predominate color must exceed to be selected.
     * @return Color.RED, Color.GREEN, Color.BLUE or -1 if no color exceeds threshold.
     */
    public int predominateRGBColor(int colorRGB, int colorThreshold)
    {
        int[] color = {Color.red(colorRGB), Color.green(colorRGB), Color.blue(colorRGB)};
        int value = 0, basicColor;

        // Which color has highest value?
        for (int i = 1; i < 3; i++)
            if (color[value] < color[i]) {value = i;}

        // Is that colors value belong threshold?
        if (color[value] < colorThreshold) value = -1;

        // If all color values the same, we can't choose.
        if (color[0] == color[1] && color[0] == color[2]) value = -1;

        switch (value)
        {
            case 0:
                basicColor = Color.RED;
                break;
            case 1:
                basicColor = Color.GREEN;
                break;
            case 2:
                basicColor = Color.BLUE;
                break;
            default:
                basicColor = -1;
        }

        Util.log("in=%d red=%d green=%d blue=%d high=%d th=%d", colorRGB, color[0], color[1], color[2], value, colorThreshold);

        return basicColor;
    }

    // Generate a color int that is the average color for the image with or without gray shades.
    private int averageImageColor(Bitmap rgbImage, boolean includeGrayShades)
    {
        int redValue = 0, blueValue = 0, greenValue = 0;
        int pixels = 0;

        for (int x = 0; x < rgbImage.getWidth(); x++)
        {
            for (int y = 0; y < rgbImage.getHeight(); y++)
            {
                int pixelColor = rgbImage.getPixel(x, y);
                //Util.log("%d %x %d %d %d", pixels, pixelColor, camMgr.red(pixelColor), camMgr.green(pixelColor), camMgr.blue(pixelColor));

                // skip shades of gray, they have no color.
                if (!includeGrayShades && isGrayShade(pixelColor)) continue;

                redValue += red(pixelColor);
                blueValue += blue(pixelColor);
                greenValue += green(pixelColor);
                pixels++;
            }
        }

        if (pixels == 0) pixels = 1;

        return Color.rgb(redValue / pixels, greenValue / pixels, blueValue / pixels);
    }

    /**
     * Opens camera and starts preview capture.
     * @param withPreviewDisplay Indicates if the preview framelayout has been added to the controller
     *                    screen and you want captured previews displayed on it.
     */
    public void startCamera(boolean withPreviewDisplay)
    {
        Util.log();

        camera = openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        camera.setPreviewCallback(previewCallback);

        Camera.Parameters parameters = camera.getParameters();

        width = parameters.getPreviewSize().width / ds;
        height = parameters.getPreviewSize().height / ds;
        parameters.setPreviewSize(width, height);
        camera.setParameters(parameters);

        if (withPreviewDisplay && preview == null) {initPreview();}
    }

    /**
     * Stops camera preview capture.
     */
    public void stopCamera()
    {
        Util.log();

        if (camera != null)
        {
            if (preview != null)
            {
                removePreview();
                preview = null;
            }

            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public void stopCameraInSecs(int seconds)
    {
        Util.log("%d", seconds);
    }

    private void initPreview()
    {
        Util.log();

        controllerApp.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                preview = new CameraPreview(controllerApp, camera, previewCallback);
                FrameLayout previewLayout = (FrameLayout) controllerApp.findViewById(R.id.previewLayout);
                previewLayout.addView(preview);
            }
        });
    }

    private void removePreview()
    {
        Util.log();

        controllerApp.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                FrameLayout previewLayout = (FrameLayout) controllerApp.findViewById(R.id.previewLayout);
                previewLayout.removeAllViews();
            }
        });
    }

    /**
     * Adds the camera preview framelayout from camera_preview_frame.xml to the controller screen.
     * @param view RelativeLayout that is the controller app main display area.
     */
    public void addPreviewLayout(final ViewGroup view)
    {
        Util.log();

        controllerApp.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                View.inflate(controllerApp, R.layout.camera_preview_frame, view);
            }
        });
    }
}
