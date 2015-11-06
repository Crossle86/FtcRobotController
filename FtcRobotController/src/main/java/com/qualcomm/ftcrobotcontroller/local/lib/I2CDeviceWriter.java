package com.qualcomm.ftcrobotcontroller.local.lib;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Provides packaged asynchronous i2c device write capability.
 */
public class I2CDeviceWriter
{
    private final I2cDevice device;
    private boolean         transactionComplete;
    private boolean         writeInProgress;
    private Object          syncObj = new Object();

    private I2CWriteCompletedCallback   callback;

    public I2CDeviceWriter(I2cDevice i2cDevice)
    {
        device = i2cDevice;
    }

    /**
     * Starts an asynchronous write operation to i2c device.
     * @param i2cAddress Device address.
     * @param memAddress Register address.
     * @param deviceData Single byte to write.
     */
    public void startWrite(int i2cAddress, int memAddress, int deviceData)
    {
        byte data[] = {(byte) deviceData};

        startWrite(i2cAddress, memAddress, data);
    }

    /**
     * Starts an asynchronous write operation to i2c device.
     * @param i2cAddress Device address.
     * @param memAddress Register address.
     * @param deviceData Array of bytes to write.
     */
    public void startWrite(int i2cAddress, int memAddress, byte deviceData[])
    {
        transactionComplete = false;
        writeInProgress = true;

        Util.log();

        device.copyBufferIntoWriteBuffer(deviceData);
        device.enableI2cWriteMode(i2cAddress, memAddress, deviceData.length);
        device.setI2cPortActionFlag();
        device.writeI2cCacheToController();

        device.registerForI2cPortReadyCallback(new I2cController.I2cPortReadyCallback()
        {
            public void portIsReady(int port)
            {
                I2CDeviceWriter.this.portDone();
            }
        });
    }

    /**
     * Check if write operation is in progress on i2c Device.
     * @return True if write is in progress.
     */
    public boolean isBusy()
    {
        return writeInProgress;
    }

    /**
     * Check if write operation is done.
     * @return True if done, false if still in progress.
     */
    public boolean isDone()
    {
        return transactionComplete;
    }

    private void portDone()
    {
        Util.log("write completed");
        device.deregisterForPortReadyCallback();
        transactionComplete = true;
        writeInProgress = false;
        if (callback != null) callback.writeCompleted();
        synchronized (syncObj){syncObj.notify();};
    }

    /**
     * Wait for write operation to complete.
     */
    public void waitForDone()
    {
        Util.log();

        if (!writeInProgress) return;

        synchronized (syncObj)
        {
            try
            {
                syncObj.wait();
            }
            catch (Exception e) {e.printStackTrace(Logging.logPrintStream);}
        }
    }

    /**
     * Wait for write operation to complete with timeout.
     * @param timeoutMs Milliseconds to wait.
     */
    public void waitForDone(long timeoutMs)
    {
        Util.log();

        if (!writeInProgress) return;

        synchronized (syncObj)
        {
            try
            {
                syncObj.wait(timeoutMs);
            }
            catch (Exception e) {e.printStackTrace(Logging.logPrintStream);}
        }
    }

    /**
     * Interface definition for write completed callback class.
     */
    public interface I2CWriteCompletedCallback
    {
        public void writeCompleted();
    }

    /**
     * Register a callback object to be called when the write is completed.
     * @param callback Callback object (see callback interface definition).
     */
    public void registerWriteCompletedCallback(I2CWriteCompletedCallback callback)
    {
        this.callback = callback;
    }

    /**
     * Deregister callback object.
     */
    public void deRegisterWriteCompletedCallback()
    {
        this.callback = null;
    }
}

