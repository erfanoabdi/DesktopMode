package com.libremobileos.desktopmode;

import static android.content.Context.MODE_PRIVATE;

import static com.libremobileos.desktopmode.PCModeConfigFragment.*;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.eu.droid_ng.vncflinger.IVncFlinger;

public class VNCServiceController extends BroadcastReceiver {

    private Context mContext;
    private IVncFlinger mService;
    private VNCServiceConnection mServiceConnection;
    private VNCServiceListener mListener;

    public VNCServiceController() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.libremobileos.desktopmode.START"))
            start(context);
    }

    public interface VNCServiceListener {
        public void onServiceEvent(Boolean connected);
    }

    public VNCServiceController(Context context, VNCServiceListener listener) {
        mContext = context;
        mListener = listener;
        mServiceConnection = new VNCServiceConnection();
        Intent intent = new Intent();
        intent.setClassName("org.eu.droid_ng.vncflinger", "org.eu.droid_ng.vncflinger.VncFlinger");
        context.bindService(intent, mServiceConnection, Context.BIND_FOREGROUND_SERVICE);
    }

    class VNCServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = IVncFlinger.Stub.asInterface(binder);
            mListener.onServiceEvent(true);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Intent intent = new Intent();
            intent.setClassName("org.eu.droid_ng.vncflinger", "org.eu.droid_ng.vncflinger.VncFlinger");
            mContext.bindService(intent, this, Context.BIND_FOREGROUND_SERVICE);
            mListener.onServiceEvent(false);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClassName("org.eu.droid_ng.vncflinger", "org.eu.droid_ng.vncflinger.VncFlinger");
        SharedPreferences sharedPreferences = context.getSharedPreferences("PCModeConfigs", MODE_PRIVATE);

        Boolean autoResize = sharedPreferences.getBoolean(KEY_PC_MODE_AUTO_RES, true);
        Integer width = sharedPreferences.getInt(KEY_PC_MODE_RES_WIDTH, 1280);
        Integer height = sharedPreferences.getInt(KEY_PC_MODE_RES_HEIGHT, 720);
        Integer scale = sharedPreferences.getInt(KEY_PC_MODE_SCALING, 100);

        int dpi = 160 * scale / 100;
        if (!autoResize) {
            intent.putExtra("width", width);
            intent.putExtra("height", height);
        }
        intent.putExtra("dpi", dpi);
        intent.putExtra("allowResize", autoResize);
        intent.putExtra("intentEnable", true);
        intent.putExtra("intentPkg", "com.libremobileos.desktopmode");
        intent.putExtra("intentComponent", "com.libremobileos.desktopmode.PCModeConfigActivity");

        context.startForegroundService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setClassName("org.eu.droid_ng.vncflinger", "org.eu.droid_ng.vncflinger.VncFlinger");

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
