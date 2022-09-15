package com.libremobileos.desktopmode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener {

    public static final String KEY_PC_MODE_CONFIG = "pc_mode_config";
    public static final String KEY_TV_MODE_CONFIG = "tv_mode_config";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        final Preference pcModeSetting = findPreference(KEY_PC_MODE_CONFIG);
        final Preference tvModeSetting = findPreference(KEY_TV_MODE_CONFIG);
        assert pcModeSetting != null;
        assert tvModeSetting != null;
        pcModeSetting.setOnPreferenceClickListener(this);
        tvModeSetting.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        final Context context = getActivity();
        if (context == null) {
            return false;
        }

        switch (pref.getKey()) {
            case KEY_PC_MODE_CONFIG:
                final Intent pcIntent = new Intent(context, PCModeConfigActivity.class);
                startActivity(pcIntent);
                return true;
            case KEY_TV_MODE_CONFIG:
                final Intent tvIntent = new Intent(context, TVModeConfigActivity.class);
                startActivity(tvIntent);
                return true;
        }

        return false;
    }
}
