package com.libremobileos.desktopmode;

import static android.content.Context.MODE_PRIVATE;

import static com.libremobileos.desktopmode.PCModeAdvancedConfigFragment.*;
import static com.libremobileos.desktopmode.PCModeConfigFragment.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

import com.libremobileos.vncflinger.IVncFlinger;

@SuppressLint("WrongConstant")
public class VNCServiceController extends BroadcastReceiver {

    private Context mContext;
    private IVncFlinger mService;
    private VNCServiceConnection mServiceConnection;
    private VNCServiceListener mListener;

    @SuppressWarnings("unused")
    public VNCServiceController() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.libremobileos.desktopmode.START"))
            start(context);
    }

    public interface VNCServiceListener {
        void onServiceEvent(Boolean connected);
    }

    public VNCServiceController(Context context, VNCServiceListener listener) {
        mContext = context;
        mListener = listener;
        mServiceConnection = new VNCServiceConnection();
        Intent intent = new Intent();
        intent.setClassName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger");
        context.bindService(intent, mServiceConnection, 0x04000000 /*Context.BIND_FOREGROUND_SERVICE*/);
    }

    public void unBind() {
        mContext.unbindService(mServiceConnection);
    }

    class VNCServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = IVncFlinger.Stub.asInterface(binder);
            mListener.onServiceEvent(true);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Intent intent = new Intent();
            intent.setClassName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger");
            mContext.bindService(intent, this, 0x04000000 /*Context.BIND_FOREGROUND_SERVICE*/);
            mListener.onServiceEvent(false);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger");
        SharedPreferences sharedPreferences = context.getSharedPreferences("PCModeConfigs", MODE_PRIVATE);

        // General
        Boolean autoResize = sharedPreferences.getBoolean(KEY_PC_MODE_AUTO_RES, true);
        Integer width = sharedPreferences.getInt(KEY_PC_MODE_RES_WIDTH, 1280);
        Integer height = sharedPreferences.getInt(KEY_PC_MODE_RES_HEIGHT, 720);
        int scale = sharedPreferences.getInt(KEY_PC_MODE_SCALING, 100);

        // Advanced
        Boolean emulateTouchValue = sharedPreferences.getBoolean(KEY_PC_MODE_EMULATE_TOUCH, false);
        Boolean relativeInputValue = sharedPreferences.getBoolean(KEY_PC_MODE_RELATIVE_INPUT, false);
        Boolean mirrorInternalValue = sharedPreferences.getBoolean(KEY_PC_MODE_MIRROR_INTERNAL, false);
        Boolean audioValue = sharedPreferences.getBoolean(KEY_PC_MODE_AUDIO, true);
        Boolean remoteCursorValue = sharedPreferences.getBoolean(KEY_PC_MODE_REMOTE_CURSOR, true);

        int dpi = 160 * scale / 100;
        if (!autoResize) {
            intent.putExtra("width", width);
            intent.putExtra("height", height);
        }
        intent.putExtra("dpi", dpi);
        intent.putExtra("allowResize", autoResize);

        intent.putExtra("emulateTouch", emulateTouchValue);
        intent.putExtra("useRelativeInput", relativeInputValue);
        intent.putExtra("mirrorInternal", mirrorInternalValue);
        intent.putExtra("hasAudio", audioValue);
        intent.putExtra("remoteCursor", remoteCursorValue);

        intent.putExtra("intentEnable", true);
        intent.putExtra("intentPkg", "com.libremobileos.desktopmode");
        intent.putExtra("intentComponent", "com.libremobileos.desktopmode.PCModeConfigActivity");

        context.startForegroundService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.libremobileos.vncflinger", "com.libremobileos.vncflinger.VncFlinger");

        context.stopService(intent);
    }

    public boolean isRunning() {
        if (mService != null) {
            try {
                return mService.isRunning();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
