// Utility functions.

package com.qualcomm.ftcrobotcontroller.local.lib;

import java.lang.Throwable;
import java.lang.StackTraceElement;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.*;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Utility functions.
 */
public abstract class Util
{
    public static OpMode currentOpMode;

    /**
     * Does telemetry with parameter formatting. Needed to set currentOpMode in calling class.
     * @param key Label for telemetry line (sorted)
     * @param message Message to be shown including format specifiers
     * @param parms comma separated object list matching format specifiers
     */
    public static void telemetry(String key, String message, Object... parms)
    {
        currentOpMode.telemetry.addData(key, String.format(message, parms));
    }

    /**
     * Log blank line with program location.
     */
    public static void log()
    {
        if (!Logging.enabled) return;

        Logging.logger.log(Level.INFO, currentMethod(2));
    }

    /**
     * Log message with optional formatting and program location.
     * @param message message with optional format specifiers for listed parameters
     * @param parms parameter list matching format specifiers
     */
    public static void log(String message, Object... parms)
    {
        if (!Logging.enabled) return;

        Logging.logger.log(Level.INFO, String.format("%s: %s", currentMethod(2), String.format(message, parms)));
    }

    /**
     * Log message with optional formatting and no program location.
     * @param message message with optional format specifiers for listed parameters
     * @param parms parameter list matching format specifiers
     */
    public static void logNoMethod(String message, Object... parms)
    {
        if (!Logging.enabled) return;

        Logging.logger.log(Level.INFO, String.format(message, parms));
    }

    /**
     * Log message with no formatting and program location.
     * @param message message with optional format specifiers for listed parameters
     */
    public static void logNoFormat(String message)
    {
        if (!Logging.enabled) return;

        Logging.logger.log(Level.INFO, String.format("%s: %s", currentMethod(2), message));
    }

    /**
     * Log message with no formatting and no program location.
     * @param message message with optional format specifiers for listed parameters
     */
    public static void logNoFormatNoMethod(String message)
    {
        if (!Logging.enabled) return;

        Logging.logger.log(Level.INFO, message);
    }

    /**
     * Returns program location where call to this method is located.
     */
    public static String currentMethod()
    {
        return currentMethod(2);
    }

    private static String currentMethod(Integer level)
    {
        StackTraceElement stackTrace[];

        stackTrace = new Throwable().getStackTrace();

        try
        {
            return stackTrace[level].toString().split("opmodes.")[1];
        }
        catch (Exception e)
        {
            try
            {
                return stackTrace[level].toString().split("lib.")[1];
            }
            catch (Exception e1)
            {
                return stackTrace[level].toString().split("activities.")[1];
            }
        }
    }

    /**
     * Write a list of configured hardware devices to the log file. Can be called in init()
     * function or later.
     * @param map hardwareMap object.
     */
    public static void logHardwareDevices(HardwareMap map)
    {
        Util.log();

        // This list must be manually updated when First releases support for new devices.

        logDevices(map.dcMotorController);
        logDevices(map.dcMotor);
        logDevices(map.servoController);
        logDevices(map.servo);
        logDevices(map.deviceInterfaceModule);
        logDevices(map.analogInput);
        logDevices(map.analogOutput);
        logDevices(map.digitalChannel);
        logDevices(map.pwmOutput);
        logDevices(map.accelerationSensor);
        logDevices(map.colorSensor);
        logDevices(map.compassSensor);
        logDevices(map.gyroSensor);
        logDevices(map.irSeekerSensor);
        logDevices(map.i2cDevice);
        logDevices(map.led);
        logDevices(map.lightSensor);
        logDevices(map.opticalDistanceSensor);
        logDevices(map.touchSensor);
        logDevices(map.ultrasonicSensor);
        logDevices(map.legacyModule);
    }

    @SuppressWarnings("unchecked")

    private static void logDevices(HardwareMap.DeviceMapping deviceMap)
    {
        for (Map.Entry<String, HardwareDevice> entry :(Set<Map.Entry<String,HardwareDevice>>) deviceMap.entrySet())
        {
            HardwareDevice device = entry.getValue();
            Util.log("%s;%s;%s", entry.getKey(), device.getDeviceName(), device.getConnectionInfo());
        }
    }

    /**
     * Get the user assigned name for a hardware device.
     * @param deviceMap The DEVICE_TYPE map, such as hardwareDevice.dcMotor, that the dev belongs to.
     * @param dev Instance of a device of DEVICE_TYPE.
     * @return User assigned name or empty string if not found.
     */

    @SuppressWarnings("unchecked")

    public static String getDeviceUserName(HardwareMap.DeviceMapping deviceMap, HardwareDevice dev)
    {
        for (Map.Entry<String, HardwareDevice> entry : (Set<Map.Entry<String,HardwareDevice>>) deviceMap.entrySet())
        {
            HardwareDevice device = entry.getValue();
            if (dev == device) return entry.getKey();
        }

        return "";
    }
}

