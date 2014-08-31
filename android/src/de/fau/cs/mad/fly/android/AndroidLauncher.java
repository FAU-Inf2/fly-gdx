package de.fau.cs.mad.fly.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class AndroidLauncher extends AndroidApplication implements EventListener {

	final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";
	static Handler mode3d2dChangedHandler = null;  
	final AndroidApplication app = this;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		addShortcut();
		
		Looper looper = Looper.getMainLooper();
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
		initialize(fly, config);
		
		Context context = getApplicationContext();
		try {
			String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			Fly.VERSION = versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** adds a shortcut to the launcher when the app is started. Only done if no
	 * shortcut already exists.
	 * */
	private void addShortcut() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		boolean shortcutExists = sharedPreferences.getBoolean(PREF_KEY_SHORTCUT_ADDED, false);
		if(shortcutExists) {
			return;
		}

		Context context = getApplicationContext();
		Intent shortcutIntent = new Intent(context, AndroidLauncher.class);
		Intent addIntent = new Intent();
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PREF_KEY_SHORTCUT_ADDED, true);
		editor.commit();
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
           if(mode3d2dChangedHandler!=null)
        	   mode3d2dChangedHandler.sendMessage(message);
			return true;
		}
		return false;
	}
}