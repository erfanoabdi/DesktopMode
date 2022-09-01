package com.libremobileos.desktopmode;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class TVModeConfigFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.tv_mode_config_preferences, rootKey);
    }
}
