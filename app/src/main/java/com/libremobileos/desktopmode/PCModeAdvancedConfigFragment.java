package com.libremobileos.desktopmode;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class PCModeAdvancedConfigFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener{

    public static final String KEY_PC_MODE_EMULATE_TOUCH = "pc_mode_emulate_touch";
    public static final String KEY_PC_MODE_RELATIVE_INPUT = "pc_mode_relative_input";
    public static final String KEY_PC_MODE_MIRROR_INTERNAL = "pc_mode_mirror_internal";
    public static final String KEY_PC_MODE_AUDIO = "pc_mode_audio";
    public static final String KEY_PC_MODE_REMOTE_CURSOR = "pc_mode_remote_cursor";

    protected SharedPreferences mSharedPreferences;
    protected Boolean mEmulateTouchValue;
    protected Boolean mRelativeInputValue;
    protected Boolean mMirrorInternalValue;
    protected Boolean mAudioValue;
    protected Boolean mRemoteCursorValue;

    protected SwitchPreference pcModeEmulateTouch;
    protected SwitchPreference pcModeRelativeInput;
    protected SwitchPreference pcModeMirrorInternal;
    protected SwitchPreference pcModeAudio;
    protected SwitchPreference pcModeRemoteCursor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pc_mode_advanced_config_preferences, rootKey);

        mSharedPreferences = requireActivity().getSharedPreferences("PCModeConfigs", MODE_PRIVATE);
        mEmulateTouchValue = mSharedPreferences.getBoolean(KEY_PC_MODE_EMULATE_TOUCH, false);
        mRelativeInputValue = mSharedPreferences.getBoolean(KEY_PC_MODE_RELATIVE_INPUT, false);
        mMirrorInternalValue = mSharedPreferences.getBoolean(KEY_PC_MODE_MIRROR_INTERNAL, false);
        mAudioValue = mSharedPreferences.getBoolean(KEY_PC_MODE_AUDIO, true);
        mRemoteCursorValue = mSharedPreferences.getBoolean(KEY_PC_MODE_REMOTE_CURSOR, true);

        pcModeEmulateTouch = findPreference(KEY_PC_MODE_EMULATE_TOUCH);
        pcModeRelativeInput = findPreference(KEY_PC_MODE_RELATIVE_INPUT);
        pcModeMirrorInternal = findPreference(KEY_PC_MODE_MIRROR_INTERNAL);
        pcModeAudio = findPreference(KEY_PC_MODE_AUDIO);
        pcModeRemoteCursor = findPreference(KEY_PC_MODE_REMOTE_CURSOR);

        pcModeEmulateTouch.setOnPreferenceChangeListener(this);
        pcModeRelativeInput.setOnPreferenceChangeListener(this);
        pcModeMirrorInternal.setOnPreferenceChangeListener(this);
        pcModeAudio.setOnPreferenceChangeListener(this);
        pcModeRemoteCursor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        Boolean isChecked = (Boolean) newValue;
        switch (pref.getKey()) {
            case KEY_PC_MODE_EMULATE_TOUCH:
                if (isChecked != mEmulateTouchValue) {
                    mEmulateTouchValue = isChecked;
                    applyChanges();
                }
                return true;
            case KEY_PC_MODE_RELATIVE_INPUT:
                if (isChecked != mRelativeInputValue) {
                    mRelativeInputValue = isChecked;
                    applyChanges();
                }
                return true;
            case KEY_PC_MODE_MIRROR_INTERNAL:
                if (isChecked != mMirrorInternalValue) {
                    mMirrorInternalValue = isChecked;
                    applyChanges();
                }
                return true;
            case KEY_PC_MODE_AUDIO:
                if (isChecked != mAudioValue) {
                    mAudioValue = isChecked;
                    applyChanges();
                }
                return true;
            case KEY_PC_MODE_REMOTE_CURSOR:
                if (isChecked != mRemoteCursorValue) {
                    mRemoteCursorValue = isChecked;
                    applyChanges();
                }
                return true;
        }

        return false;
    }

    private void applyChanges() {
        SharedPreferences.Editor myEdit = mSharedPreferences.edit();

        myEdit.putBoolean(KEY_PC_MODE_EMULATE_TOUCH, mEmulateTouchValue);
        myEdit.putBoolean(KEY_PC_MODE_RELATIVE_INPUT, mRelativeInputValue);
        myEdit.putBoolean(KEY_PC_MODE_MIRROR_INTERNAL, mMirrorInternalValue);
        myEdit.putBoolean(KEY_PC_MODE_AUDIO, mAudioValue);
        myEdit.putBoolean(KEY_PC_MODE_REMOTE_CURSOR, mRemoteCursorValue);

        myEdit.apply();
    }
}
