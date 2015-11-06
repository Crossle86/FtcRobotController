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
import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;

import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;

import java.io.IOException;

/**
 * Created by FTC-5648 on 9/15/2015.
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PreviewCallback previewCallback;

    public CameraPreview(Context context, Camera camera, Camera.PreviewCallback previewCallback)
    {
        super(context);

        Util.log();

        mCamera = camera;

        this.previewCallback = previewCallback;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera)
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        Util.log("rot=%d facing=%d", rotation, info.facing);

        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Util.log();

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Camera.Parameters parameters = mCamera.getParameters();
   //         mCamera.setDisplayOrientation(90);
            setCameraDisplayOrientation(((Activity) this.getContext()), Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);

            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {e.printStackTrace(Logging.logPrintStream);}
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        Util.log();

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try
        {
            mCamera.stopPreview();
        } catch (Exception e){e.printStackTrace(Logging.logPrintStream);}

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try
        {
            Camera.Parameters parameters = mCamera.getParameters();
            setCameraDisplayOrientation(((Activity) this.getContext()), Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);
            mCamera.setParameters(parameters);

            mCamera.setPreviewCallback(previewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){e.printStackTrace(Logging.logPrintStream);}
    }
}
