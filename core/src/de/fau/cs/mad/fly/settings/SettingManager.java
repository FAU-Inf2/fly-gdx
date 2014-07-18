package de.fau.cs.mad.fly.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import de.fau.cs.mad.fly.I18n;

/**
 * Stores all the settings in a HashMap
 * 
 * @author Tobias Zangl
 */
public class SettingManager {

	public static final String USE_TOUCH = "useTouch";
	public static final String USE_ROLL_STEERING = "useRolling";
	public static final String USE_LOW_PASS_FILTER = "useLowPass";
	public static final String SHOW_GATE_INDICATOR = "showGateIndicator";
	public static final String SHOW_PAUSE = "showPause";
	public static final String SHOW_STEERING = "showSteering";
	public static final String SHOW_FPS = "showFPS";
	public static final String FIRST_PERSON = "firstPerson";
	public static final String SHOW_RESET_STEERING = "showResetSteering";
	public static final String VIBRATE_WHEN_COLLIDE = "vibrateWhenCollide";

	public static final String ALPHA_SLIDER = "alphaSlider";
	public static final String BUFFER_SLIDER = "bufferSlider";
	public static final String CAMERA_OFFSET = "cameraOffset";

	private Preferences prefs;
	private Map<String, ISetting> settingMap;
	private List<String> settingList;

	public SettingManager(String preferenceFileName) {
		prefs = Gdx.app.getPreferences(preferenceFileName);
		settingMap = new HashMap<String, ISetting>();
		settingList = new ArrayList<String>();
		createSettings();
	}

	/**
	 * Adds a new setting with a CheckBox and a boolean value.
	 * 
	 * @param id
	 *            the id of the Setting. Also used to find the description in the I18N files.
	 * @param defaultValue
	 *            the default value of the Setting
	 */
	public void addBooleanSetting(String id, boolean defaultValue) {
	    boolean value = defaultValue;
		if (prefs.contains(id)) {
		    value = prefs.getBoolean(id);
		} else {
		    prefs.putBoolean(id, defaultValue);
            prefs.flush();
		}
		ISetting newBooleanSetting = new BooleanSetting(this, id, I18n.t(id), value);
		settingMap.put(id, newBooleanSetting);
		settingList.add(id);
	}
	

	/**
	 * Adds a new Setting with a Slider and a float value.
	 * 
	 * @param id
	 *            the id of the Setting
	 * @param description
	 *            the description of the Setting
	 * @param value
	 *            the default value of the Setting
	 * @param min
	 *            the minimum value of the Slider
	 * @param max
	 *            the maximum value of the Slider
	 * @param stepSize
	 *            the step size of the Slider
	 */
	public void addFloatSetting(String id, String description, float value, float min, float max, float stepSize) {
		if (!prefs.contains(id)) {
			prefs.putFloat(id, value);
			prefs.flush();
		} else {
			value = prefs.getFloat(id);
		}

		ISetting newFloatSetting = new FloatSetting(this, id, description, value, min, max, stepSize);
		settingMap.put(id, newFloatSetting);
		settingList.add(id);
	}

	/**
	 * Getter for the {@link #prefs}.
	 */
	public Preferences getPreferences() {
		return prefs;
	}

	/**
	 * Getter for the {@link #settingMap}
	 */
	public Map<String, ISetting> getSettingMap() {
		return settingMap;
	}

	/**
	 * Sorted list of all Preferences.
	 */
	public List<String> getSettingList() {
		return settingList;
	}
	
    /**
     * Creates all settings that should be displayed in the {@link ui#SettingScreen}.
     */
    private void createSettings() {
        addBooleanSetting(USE_TOUCH, false);
        addBooleanSetting(VIBRATE_WHEN_COLLIDE, true);
        addBooleanSetting(USE_ROLL_STEERING, false);
        // removed for release: addBooleanSetting(USE_LOW_PASS_FILTER, "Use LowPassFilter:", false);
        // removed for release: addBooleanSetting(SHOW_GATE_INDICATOR, "Show next Gate:", true);
        addBooleanSetting(SHOW_PAUSE, false);
        addBooleanSetting(SHOW_STEERING, false);
        addBooleanSetting(SHOW_FPS, false);
        addBooleanSetting(SHOW_RESET_STEERING, false);
        // removed for release: addBooleanSetting(FIRST_PERSON, "First Person", false);
        
        // removed for release: addFloatSetting(ALPHA_SLIDER, "Alpha:", 15.0f, 0.0f, 100.0f, 1.0f);
        // removed for release: addFloatSetting(BUFFER_SLIDER, "Buffersize:", 30.0f, 0.0f, 100.0f, 1.0f);
        // removed for release: addFloatSetting(CAMERA_OFFSET, "Camera Distance:", 50.0f, 0.0f, 100.0f, 1.0f);
    }
}
