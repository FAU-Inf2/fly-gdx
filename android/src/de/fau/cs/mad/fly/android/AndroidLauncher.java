package de.fau.cs.mad.fly.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.fau.cs.mad.fly.Fly;

public class AndroidLauncher extends AndroidApplication {

	final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		addShortcut();
		initialize(new Fly(), config);
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
}