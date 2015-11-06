/**
 * Driver Station dashboard utility. Wraps displaying information with telemetry class.
 * Adapted from code contributed by team 492.
 */
package com.qualcomm.ftcrobotcontroller.local.lib;

import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.NoSuchElementException;

public class DSDashboard
{
    public static final int MAX_NUM_TEXTLINES = 16;

    private static final String displayKeyFormat = "[%02d]";
    private static Telemetry telemetry = null;
    private static DSDashboard instance = null;
    private static String[] display = new String[MAX_NUM_TEXTLINES];

    /**
     * Create new instance of DSDashboard class.
     * @param telemetry Calling opMode telemetry object.
     */
    public DSDashboard(Telemetry telemetry)
    {
        Util.log();

        instance = this;
        this.telemetry = telemetry;
        telemetry.clearData();
        clearDisplay();
    }   //DSDashboard

    /**
     * Returns the most recently allocated DSDashboard instance.
     * @return DSDashboard instance reference.
     */
    public static DSDashboard getInstance()
    {
        return instance;
    }   //getInstance

    /**
     * Display a line of data on the dashboard.
     * @param lineNum Line number, 0-15
     * @param format Display string with optional formatting parameters.
     * @param args Optional object matching formatting parameters.
     */
    public void displayPrintf(int lineNum, String format, Object... args)
    {
        Util.log("line=%d: %s", lineNum, String.format(format, args));

        if (lineNum >= 0 && lineNum < display.length)
        {
            display[lineNum] = String.format(format, args);
            telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
        }
    }   //displayPrintf

    /**
     * Clear the menu display lines.
     */
    public void clearDisplay()
    {
        Util.log();

        for (int i = 0; i < display.length; i++)
        {
            display[i] = "";
        }

        refreshDisplay();
    }   //clearDisplay

    /**
     * Redisplay the current dashboard data.
     */
    public void refreshDisplay()
    {
        Util.log();

        for (int i = 0; i < display.length; i++)
        {
            telemetry.addData(String.format(displayKeyFormat, i), display[i]);
        }
    }   //refreshDisplay

// I don't know what you would use these methods for...so will hide them for now.
//    public boolean getBoolean(String key)
//    {
//        boolean value;
//        String strValue = getValue(key);
//
//        if (strValue.equals("true"))
//        {
//            value = true;
//        }
//        else if (strValue.equals("false"))
//        {
//            value = false;
//        }
//        else
//        {
//            throw new IllegalArgumentException("object is not boolean");
//        }
//
//        return value;
//    }   //getBoolean
//
//    public boolean getBoolean(String key, boolean defaultValue)
//    {
//        boolean value;
//
//        try
//        {
//            value = getBoolean(key);
//        }
//        catch (NoSuchElementException e)
//        {
//            putBoolean(key, defaultValue);
//            value = defaultValue;
//        }
//
//        return value;
//    }   //getBoolean
//
//    public void putBoolean(String key, boolean value)
//    {
//        telemetry.addData(key, Boolean.toString(value));
//    }   //putBoolean
//
//    public double getNumber(String key)
//    {
//        double value;
//
//        try
//        {
//            value = Double.parseDouble(getValue(key));
//        }
//        catch (NumberFormatException e)
//        {
//            throw new IllegalArgumentException("object is not a number");
//        }
//
//        return value;
//    }   //getNumber
//
//    public double getNumber(String key, double defaultValue)
//    {
//        double value;
//
//        try
//        {
//            value = getNumber(key);
//        }
//        catch (NoSuchElementException e)
//        {
//            putNumber(key, defaultValue);
//            value = defaultValue;
//        }
//
//        return value;
//    }   //getNumber
//
//    public void putNumber(String key, double value)
//    {
//        telemetry.addData(key, Double.toString(value));
//    }   //putNumber
//
//    public String getString(String key)
//    {
//        return getValue(key);
//    }   //getString
//
//    public String getString(String key, String defaultValue)
//    {
//        String value;
//
//        try
//        {
//            value = getString(key);
//        }
//        catch (NoSuchElementException e)
//        {
//            putString(key, defaultValue);
//            value = defaultValue;
//        }
//
//        return value;
//    }   //getString
//
//    public void putString(String key, String value)
//    {
//        telemetry.addData(key, value);
//    }   //putString
//
//    private String getValue(String key)
//    {
//        String value = telemetry.getDataStrings().get(key);
//
//        if (value == null)
//        {
//            throw new NoSuchElementException("No such key");
//        }
//
//        return value;
//    }   //getValue

}   //class DSDashboard
