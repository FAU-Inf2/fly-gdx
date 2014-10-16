package de.fau.cs.mad.fly.android;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Fly.Mode3d2dChangedEvent;
import de.fau.cs.mad.fly.OrientationProvider;

public class AndroidLauncher extends AndroidApplication implements EventListener {

    static Handler mode3d2dChangedHandler = null;
    final AndroidApplication app = this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Looper looper = Looper.getMainLooper();
        // this handler cares about switching the screen off after some seconds
        // of inactivity in 2d mode
        mode3d2dChangedHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                int mode = Integer.valueOf(msg.obj.toString());
                if (mode == Mode3d2dChangedEvent.MODE_3D) {
                    app.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    app.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        };
        
        Fly fly = new Fly();
        fly.add3d2dChangedListeners(this);
        final Context context = getApplicationContext();
        
        fly.addOrientationProvider(new OrientationProvider() {
            @Override
            public boolean orientationSwapped() {
                return landscapeOrientationSwapped();
            }
            
        });
        
        initialize(fly, config);
        
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Fly.VERSION = versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public boolean landscapeOrientationSwapped() {
        return ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation() == 3;
    }
    
    /**
     * handle Mode3d2dChangedEvent
     */
    @Override
    public boolean handle(Event event) {
        if (event instanceof Mode3d2dChangedEvent) {
            Mode3d2dChangedEvent e = (Mode3d2dChangedEvent) event;
            int mode = e.mode;
            Message message = Message.obtain();
            message.obj = mode;
            if (mode3d2dChangedHandler != null) {
                mode3d2dChangedHandler.sendMessage(message);
            }
            // in 2d mode screen should be dependent on sensor, in 3d mode
            // locked
            if (mode == 2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                // lock it to the current orientation. Use defined orientations,
                // because SCREEN_ORIENTATION_LOCKED needs api level 18
                if (landscapeOrientationSwapped()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
}