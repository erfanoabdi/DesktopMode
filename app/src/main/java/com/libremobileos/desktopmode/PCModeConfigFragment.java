package com.libremobileos.desktopmode;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.libremobileos.desktopmode.preferences.ResolutionPreference;
import com.libremobileos.desktopmode.preferences.SeekBarPreference;

public class PCModeConfigFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public static final String KEY_PC_MODE_AUTO_RES = "pc_mode_auto_resolution";
    public static final String KEY_PC_MODE_RES = "pc_mode_resolution";
    public static final String KEY_PC_MODE_SCALING = "pc_mode_scaling";
    public static final String KEY_PC_MODE_SERVICE_BUTTON = "pc_mode_service_button";

    public static final String KEY_PC_MODE_RES_WIDTH = "pc_mode_res_width";
    public static final String KEY_PC_MODE_RES_HEIGHT = "pc_mode_res_height";

    protected SharedPreferences mSharedPreferences;
    protected Boolean mAutoResValue;
    protected Integer mCustomResWidthValue;
    protected Integer mCustomResHeightValue;
    protected Integer mScalingValue;

    protected SwitchPreference pcModeAutoRes;
    protected ResolutionPreference pcModeRes;
    protected SeekBarPreference pcModeScaling;
    protected Preference pcModeServiceButton;

    VNCServiceController mVncService;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pc_mode_config_preferences, rootKey);

        mSharedPreferences = getActivity().getSharedPreferences("PCModeConfigs", MODE_PRIVATE);
        mAutoResValue = mSharedPreferences.getBoolean(KEY_PC_MODE_AUTO_RES, true);
        mCustomResWidthValue = mSharedPreferences.getInt(KEY_PC_MODE_RES_WIDTH, 1280);
        mCustomResHeightValue = mSharedPreferences.getInt(KEY_PC_MODE_RES_HEIGHT, 720);
        mScalingValue = mSharedPreferences.getInt(KEY_PC_MODE_SCALING, 100);

        pcModeAutoRes = findPreference(KEY_PC_MODE_AUTO_RES);
        pcModeRes = findPreference(KEY_PC_MODE_RES);
        pcModeScaling = findPreference(KEY_PC_MODE_SCALING);
        pcModeServiceButton = findPreference(KEY_PC_MODE_SERVICE_BUTTON);

        pcModeAutoRes.setOnPreferenceChangeListener(this);
        pcModeRes.setOnPreferenceChangeListener(this);
        pcModeScaling.setOnPreferenceChangeListener(this);
        pcModeServiceButton.setOnPreferenceClickListener(this);

        pcModeAutoRes.setChecked(mAutoResValue);
        pcModeRes.setWidth(mCustomResWidthValue, false);
        pcModeRes.setHeight(mCustomResHeightValue, false);
        pcModeRes.setEnabled(!mAutoResValue);
        pcModeScaling.setValue(mScalingValue);

        mVncService = new VNCServiceController(getActivity(), new VNCServiceController.VNCServiceListener() {
            @Override
            public void onServiceEvent(Boolean connected) {
                if (connected)
                    pcModeServiceButton.setTitle(R.string.pc_mode_service_stop);
                else
                    pcModeServiceButton.setTitle(R.string.pc_mode_service_start);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVncService.unBind();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        final Context context = getActivity();
        if (context == null) {
            return false;
        }

        if (pref.getKey().equals(KEY_PC_MODE_SERVICE_BUTTON)) {
            CharSequence title = pref.getTitle();
            if (getString(R.string.pc_mode_service_stop).equals(title)) {
                VNCServiceController.stop(getActivity());
                return true;
            } else if (getString(R.string.pc_mode_service_start).equals(title)) {
                VNCServiceController.start(getActivity());
                return true;
            } else if (getString(R.string.pc_mode_service_restart_apply).equals(title)) {
                VNCServiceController.stop(getActivity());
                applyChanges();
                VNCServiceController.start(getActivity());
                return true;
            } else if (getString(R.string.pc_mode_service_restart).equals(title)) {
                VNCServiceController.stop(getActivity());
                VNCServiceController.start(getActivity());
                return true;
            } else if (getString(R.string.pc_mode_service_apply).equals(title)) {
                applyChanges();
                VNCServiceController.start(getActivity());
                pcModeServiceButton.setTitle(R.string.pc_mode_service_stop);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        final Context context = getActivity();
        if (context == null) {
            return false;
        }

        switch (pref.getKey()) {
            case KEY_PC_MODE_AUTO_RES:
                Boolean isAutoChecked = (Boolean) newValue;
                if (isAutoChecked != mAutoResValue) {
                    pcModeRes.setEnabled(!isAutoChecked);
                    mAutoResValue = isAutoChecked;
                    handleChange(true);
                }
                return true;
            case KEY_PC_MODE_RES:
                if (pcModeRes.getWidth() != mCustomResWidthValue) {
                    mCustomResWidthValue = pcModeRes.getWidth();
                    handleChange(false);
                }
                if (pcModeRes.getHeight() != mCustomResHeightValue) {
                    mCustomResHeightValue = pcModeRes.getHeight();
                    handleChange(false);
                }
                return true;
            case KEY_PC_MODE_SCALING:
                Integer progress = (Integer) newValue;
                if (progress != mScalingValue) {
                    mScalingValue = progress;
                    handleChange(false);
                }
                return true;
        }

        return false;
    }

    private void handleChange(boolean needRestart) {
        if (!mVncService.isRunning()) {
            applyChanges();
            pcModeServiceButton.setTitle(R.string.pc_mode_service_start);
        } else if (needRestart) {
            pcModeServiceButton.setTitle(R.string.pc_mode_service_restart_apply);
        } else {
            pcModeServiceButton.setTitle(R.string.pc_mode_service_apply);
        }
    }

    private void applyChanges() {
        SharedPreferences.Editor myEdit = mSharedPreferences.edit();

        myEdit.putBoolean(KEY_PC_MODE_AUTO_RES, mAutoResValue);
        myEdit.putInt(KEY_PC_MODE_RES_WIDTH, mCustomResWidthValue);
        myEdit.putInt(KEY_PC_MODE_RES_HEIGHT, mCustomResHeightValue);
        myEdit.putInt(KEY_PC_MODE_SCALING, mScalingValue);

        myEdit.commit();
    }
}
