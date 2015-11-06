package com.qualcomm.ftcrobotcontroller.local.lib;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Replaces I2cDeviceReader to provide packaged asynchronous read capability.
 */
public class I2CDeviceReader
{
    private final I2cDevice device;
    private boolean         transactionComplete;
    private boolean         bufferReadComplete;
    private boolean         readInProgress;
    private byte[]          deviceData;
    private Object          syncObj = new Object();

    private I2CReadCompletedCallback   callback;

    public I2CDeviceReader(I2cDevice i2cDevice)
    {
        device = i2cDevice;
    }

    /**
     * Starts an asynchronous read operation on the i2c device.
     * @param i2cAddress Device address.
     * @param memAddress Register address to read.
     * @param num_bytes Number of data bytes  to read.
     */
    public void startRead(int i2cAddress, int memAddress, int num_bytes)
    {
        deviceData = null;
        transactionComplete = false;
        bufferReadComplete = false;
        readInProgress = true;

        Util.log();

        device.enableI2cReadMode(i2cAddress, memAddress, num_bytes);
        device.setI2cPortActionFlag();
        device.writeI2cCacheToController();

        device.registerForI2cPortReadyCallback(new I2cController.I2cPortReadyCallback()
        {
            public void portIsReady(int port)
            {
                I2CDeviceReader.this.portDone();
            }
        });
    }

    /**
     * Check if a read operation is in progress on the i2c device.
     * @return True if read is in progress.
     */
    public boolean isBusy()
    {
        return readInProgress;
    }

    /**
     * Check if read operation on i2c device has completed.
     * @return True if complete, false if still in progress.
     */
    public boolean isDone()
    {
        return transactionComplete && bufferReadComplete;
    }

    private void portDone()
    {
        if (!transactionComplete && device.isI2cPortReady())
        {
            Util.log("port ready");
            transactionComplete = true;
            device.readI2cCacheFromController();
        }
        else if (transactionComplete)
        {
            Util.log("data ready");
            deviceData = device.getCopyOfReadBuffer();
            device.deregisterForPortReadyCallback();
            bufferReadComplete = true;
            readInProgress = false;
            if (callback != null) callback.readCompleted(deviceData);
            synchronized (syncObj){syncObj.notify();};
        }
    }

    /**
     * Wait for read from i2c device to complete.
     */
    public void waitForDone()
    {
        Util.log();

        if (!readInProgress) return;

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
     * Wiat for read fromm i2c device to complete with timeout.
     * @param timeoutMs Milliseonds to wait.
     */
    public void waitForDone(long timeoutMs)
    {
        Util.log();

        if (!readInProgress) return;

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
     * Waits for read to complete then returns the data read from i2c device.
     * @return Data as byte array or null.
     */
    public byte[] waitForData()
    {
        Util.log();

        if (!readInProgress) return null;

        waitForDone();

        return getReadBuffer();
    }

    /**
     * Waits for read to complete then returns the data read from i2c device.
     * @timeoutMs Milliseconds to wait.
     * @return Data as byte array or null.
     */
    public byte[] waitForData(long timeoutMs)
    {
        Util.log();

        if (!readInProgress) return null;

        waitForDone(timeoutMs);

        return getReadBuffer();
    }

    /**
     * Returns last data read from i2c device.
     * @return Data as byte array or null.
     */
    public byte[] getReadBuffer()
    {
        return deviceData;
    }

    /**
     * Interface definition for read completed callback class.
     */
    public interface I2CReadCompletedCallback
    {
        public void readCompleted(byte data[]);
    }

    /**
     * Register a callback to be called when read completed. If a class is registered, it will be
     * called when read has completed.
     * @param callback Callback class object.
     */
    public void registerReadCompletedCallback(I2CReadCompletedCallback callback)
    {
        this.callback = callback;
    }

    /**
     * Deregister read completed callback function.
     */
    public void deRegisterReadCompletedCallback()
    {
        this.callback = null;
    }
}
