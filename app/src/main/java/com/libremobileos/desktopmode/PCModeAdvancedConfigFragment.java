package com.libremobileos.desktopmode;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class PCModeAdvancedConfigFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pc_mode_advanced_config_preferences, rootKey);
    }
}
