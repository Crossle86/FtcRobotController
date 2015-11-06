package com.qualcomm.ftcrobotcontroller.local.lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;

import com.qualcomm.robotcore.util.RobotLog;

import java.util.Map;
import java.util.Set;

/**
 * Utility class supporting access to the parent app, the FtcController.
 */
public abstract class AppUtil
{
    private static Activity controllerApp;

    private static SharedPreferences preferences;

    /**
     * Called to set the FTC Controller app context, which is used by the functions in this
     * class. Must be called in the init() method of the opmode before any other methods.
     * @param context hardwareMap.appContext
     */
    public static void setControllerApp(android.content.Context context)
    {
        controllerApp = (Activity) context;

        Logging.enabled = AppUtil.getBooleanPreference("logging", true);
    }

    /**
     * Find a parent app View by id.
     * @param id view id from R.id.
     * @return View or null.
     */
    public static View findView(int id)
    {
        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return null;
        }

        return controllerApp.findViewById(id);
    }

    /**
     * Find a parent app TextView by id.
     * @param id view id from R.id.
     * @return TextView or null.
     */
    public static TextView findTextView(int id)
    {
        return (TextView) findView(id);
    }

    /**
     * Set the background color of a View.
     * @param view View to set.
     * @param color Color.x color value.
     */
    public static void setBackgroundColor(View view, int color)
    {
        final View innerView = view;
        final int  innerColor = color;

        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return;
        }

        controllerApp.runOnUiThread(new Runnable()
        {
            public void run()
            {
                innerView.setBackgroundColor(innerColor);
            }
        });
    }

    /**
     * Set the text in a TextView.
     * @param view TextView to set.
     * @param text Text string to set in the TextView.
     */
    public static void setText(TextView view, CharSequence text)
    {
        final TextView      innerView = view;
        final CharSequence  innerText = text;

        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return;
        }

        controllerApp.runOnUiThread(new Runnable()
        {
            public void run()
            {
                innerView.setText(innerText);
            }
        });
    }

    /**
     * Append the text to a TextView.
     * @param view TextView to set.
     * @param text Text string to append to the TextView.
     */
    public static void appendText(TextView view, CharSequence text)
    {
        final TextView      innerView = view;
        final CharSequence  innerText = text;

        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return;
        }

        controllerApp.runOnUiThread(new Runnable()
        {
            public void run()
            {
                innerView.append(innerText);
            }
        });
    }

    /**
     * Set the foreground (text) color of a View.
     * @param view TextView to set.
     * @param color Color.x color value.
     */
    public static void setTextColor(TextView view, int color)
    {
        final TextView innerView = view;
        final int      innerColor = color;

        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return;
        }

        controllerApp.runOnUiThread(new Runnable()
        {
            public void run()
            {
                innerView.setTextColor(innerColor);
            }
        });
    }

    /**
     * Displays a Toast (pop-up) message on the Controller over the controller app. Uses the
     * built-in "short" time-out.
     * @param message Message to display.
     */
    public static void showToast(String message)
    {
        final String innerMessage = message;

        Util.log(message);

        if (controllerApp == null)
        {
            Util.log("controllerApp not set in AppUtil.");
            return;
        }

        controllerApp.runOnUiThread(new Runnable()
        {
            public void run() {Toast.makeText(controllerApp, innerMessage, Toast.LENGTH_SHORT).show();}
        });
    }

    /**
     * Display a message in the Controller error message area. Also displays as an error on the
     * driver station.
     * @param message Message to display.
     */
    public static void setControllerError(String message)
    {
        Util.log(message);

        RobotLog.setGlobalErrorMsg(message);
    }

    /**
     * Clear the Controller error message area. Does not clear the driver station display.
     */
    public static void clearControllerError()
    {
        RobotLog.clearGlobalErrorMsg();
    }

    // Note that preferences created by a list widget always store thier values as strings so
    // if the value list for the list widget has numbers, you have to read them as string and
    // convert. Other preferences appear to be stored as thier data type and have to be read
    // with the correct getXPreference function.

    /**
     * Get a string preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return Sting value of the preference.
     */
    public static String getStringPreference(String name, String defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e) {return defaultValue;}

        return preferences.getString(name, defaultValue);
    }

    /**
     * Set a string shared preference.
     * @param name Name of the preference.
     * @param value String value to set.
     */
    public static void setStringPreference(String name, String value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    /**
     * Get an integer preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return Integer value of the preference.
     */
    public static int getIntPreference(String name, int defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e) {return defaultValue;}

        return preferences.getInt(name, defaultValue);
    }

    /**
     * Set an integer  shared preference.
     * @param name Name of the preference.
     * @param value Integer value to set.
     */
    public static void setIntPreference(String name, int value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    /**
     * Get a boolean preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return True or False value of the preference.
     */
    public static boolean getBooleanPreference(String name, boolean defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e) {return defaultValue;}

        return preferences.getBoolean(name, defaultValue);
    }

    /**
     * Set a boolean shared preference.
     * @param name Name of the preference.
     * @param value Boolean value to set.
     */
    public static void setBooleanPreference(String name, boolean value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    /**
     * Get a float preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return Float value of the preference.
     */
    public static float getFloatPreference(String name, float defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e) {return defaultValue;}

        return preferences.getFloat(name, defaultValue);
    }

    /**
     * Set a float shared preference.
     * @param name Name of the preference.
     * @param value Float value to set.
     */
    public static void setFloatPreference(String name, float value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(name, value);
        editor.commit();
    }

    /**
     * Get a long preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return Long value of the preference.
     */
    public static double getLongPreference(String name, long defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e) {return defaultValue;}

        return preferences.getLong(name, defaultValue);
    }

    /**
     * Set a long shared preference.
     * @param name Name of the preference.
     * @param value Long value to set.
     */
    public static void setLongPreference(String name, long value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, value);
        editor.commit();
    }

    /**
     * Get a stringset preference value from the app shared preferences.
     * @param name name of preference.
     * @param defaultValue value returned if preference not found.
     * @return StringSet value of the preference.
     */
    public static Set getStringSetPreference(String name, Set<String> defaultValue)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            return defaultValue;
        }

        return preferences.getStringSet(name, defaultValue);
    }

    /**
     * Set a stringset shared preference.
     * @param name Name of the preference.
     * @param value StringSet value to set.
     */
    public static void setStringSetPreference(String name, Set<String> value)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(name, value);
        editor.commit();
    }

    public static void deletePreference(String name)
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(name);
        editor.commit();
    }

    /**
     * Write the contents of the default shared preferences to the log file.
     */
    public static void logPreferences()
    {
        try
        {
            if (preferences == null)
                preferences = PreferenceManager.getDefaultSharedPreferences(controllerApp);

            for (Map.Entry pref : preferences.getAll().entrySet()) Util.log(pref.toString());
        }
        catch (Exception e) {}
    }

    /**
     * Return the program VersionCode and VersionName from AndroidManifest.xml file.
     * @return The version information.
     */
    public static String getVersion()
    {
        try
        {
            PackageInfo info = controllerApp.getPackageManager().getPackageInfo(controllerApp.getPackageName(), 0);
            return String.format("Version=%d:%s", info.versionCode, info.versionName);
        }
        catch (Exception e)
        {
            return "ControllerApp not set";
        }
    }

    /**
     * Plays a sound given the sound's identity as a (raw) resource.
     * Thanks to Swerve Robotics for this function.
     * @param resourceId    The resource number of the sound to play
     */
    public static void playSound(int resourceId)
    {
        Util.log("%d", resourceId);

        try
        {
            // Turn up the volume!
            AudioManager am = (AudioManager) controllerApp.getSystemService(controllerApp.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

            // Get a media player for the resource
            MediaPlayer mediaPlayer = MediaPlayer.create(controllerApp, resourceId);

            // Start the sound playing and wait until it's done
            mediaPlayer.start();

            while (mediaPlayer.isPlaying()) Thread.yield();

            // Shutdown the media player entirely and cleanly. reset() is needed to
            // have the internal media player event handler remove it's callbacks so
            // it won't later get notifications. release() is good citizenship, and is
            // certainly a good thing to do, but it might not strictly be needed.
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
        }

        Util.log("done");
    }
}
