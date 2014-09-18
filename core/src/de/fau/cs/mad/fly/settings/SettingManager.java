package de.fau.cs.mad.fly.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import de.fau.cs.mad.fly.I18n;

/**
 * Stores all the settings in a HashMap
 * 
 * @author Tobias Zangl, Lukas Hahmann <lukas.hahmann@gmail.com>
 */
public class SettingManager {
    
    public static final String USE_TOUCH = "useTouch";
    public static final String SHOW_PAUSE = "showPause";
    public static final String SHOW_FPS = "showFPS";
    public static final String VIBRATE_WHEN_COLLIDE = "vibrateWhenCollide";
    public static final String INVERT_PITCH = "invertPitch";
    
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
     *            the id of the Setting. Also used to find the description in
     *            the I18N files.
     * @param defaultValue
     *            the default value of the Setting
     * @param helpingText
     *            String that identifies the helping text in the I18N
     */
    public void addBooleanSetting(String id, boolean defaultValue, String helpingText, boolean hide) {
        boolean value = defaultValue;
        if (prefs.contains(id) && id != USE_TOUCH && !Application.ApplicationType.Desktop.equals(Gdx.app.getType())) {
            value = prefs.getBoolean(id);
        } else {
            prefs.putBoolean(id, defaultValue);
            prefs.flush();
        }
        
        ISetting newBooleanSetting = new BooleanSetting(this, id, I18n.t(id), value, helpingText);
        settingMap.put(id, newBooleanSetting);
        if (!hide) {
            settingList.add(id);
        }
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
     * @param helpingText
     *            String that identifies the helping text in the I18N
     */
    public void addFloatSetting(String id, String description, float value, float min, float max, float stepSize, String helpingText) {
        if (!prefs.contains(id)) {
            prefs.putFloat(id, value);
            prefs.flush();
        } else {
            value = prefs.getFloat(id);
        }
        
        ISetting newFloatSetting = new FloatSetting(this, id, description, value, min, max, stepSize, helpingText);
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
     * Creates all settings that should be displayed in the
     * {@link ui#SettingScreen}.
     */
    private void createSettings() {
        ApplicationType type = Gdx.app.getType();
        if (Application.ApplicationType.Android.equals(type) || Application.ApplicationType.iOS.equals(type)) {
            addBooleanSetting(VIBRATE_WHEN_COLLIDE, true, "helpVibrate", false);
            addBooleanSetting(USE_TOUCH, false, "helpTouch", false);
            addBooleanSetting(INVERT_PITCH, false, "helpPitch", false);
        } else if (Application.ApplicationType.Desktop.equals(type)) {
            Gdx.app.log("Setting", "desktop");
            addBooleanSetting(USE_TOUCH, true, "helpTouch", true);
            addBooleanSetting(INVERT_PITCH, false, "helpPitch", false);
        }
        // addBooleanSetting(SHOW_PAUSE, false);
        addBooleanSetting(SHOW_FPS, false, "helpShowFPS", false);
    }
}
