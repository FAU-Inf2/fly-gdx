package de.fau.cs.mad.fly.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Manage all the app level settings in a HashMap, like default user and so on.
 * 
 * @author Qufang Fan
 */
public class AppSettingsManager {

	private String appPrefName = "flySettings";

	private AppSettingsManager() {

	}

	public static AppSettingsManager Instance = new AppSettingsManager();
	public static String CHOSEN_USER = "chosenUser";
	public static String DATABASE_VERSION = "dbVersion";

	private Preferences appPrefs = Gdx.app.getPreferences(appPrefName);

	public String getStringSetting(String settingName, String defaultValue) {
		String value;
		if (!appPrefs.contains(settingName)) {
			appPrefs.putString(settingName, defaultValue);
			appPrefs.flush();
		}

		value = appPrefs.getString(settingName);
		return value;
	}

	public void setStringSetting(String settingName, String newValue) {
		// if (!appPrefs.contains(settingName))
		{
			appPrefs.putString(settingName, newValue);
			appPrefs.flush();
		}

	}

	public int getIntegerSetting(String settingName, int defaultValue) {
		int value;
		if (!appPrefs.contains(settingName)) {
			appPrefs.putInteger(settingName, defaultValue);
			appPrefs.flush();
		}

		value = appPrefs.getInteger(settingName);
		return value;
	}

	public void setIntegerSetting(String settingName, int newValue) {
		// if (!appPrefs.contains(settingName))
		{
			appPrefs.putInteger(settingName, newValue);
			appPrefs.flush();
		}

	}

}
