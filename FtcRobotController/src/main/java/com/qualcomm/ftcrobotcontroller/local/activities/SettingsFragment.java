package com.qualcomm.ftcrobotcontroller.local.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;

public class SettingsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.log();

        try
        {
            addPreferencesFromResource(R.xml.local_preferences);
        }
        catch (Exception e)
        {
            e.printStackTrace(Logging.logPrintStream);
            throw e;
        }
    }
}
