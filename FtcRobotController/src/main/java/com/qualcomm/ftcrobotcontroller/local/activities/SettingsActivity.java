package com.qualcomm.ftcrobotcontroller.local.activities;

import android.app.Activity;
import android.os.Bundle;

import com.qualcomm.ftcrobotcontroller.local.lib.*;

public class SettingsActivity extends Activity
{
    public SettingsActivity() throws Exception
    {
        Logging.MyLogger.setup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.log();

        try
        {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            throw e;
        }
    }
}
