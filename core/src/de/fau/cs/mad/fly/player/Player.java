package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Stores all player-specific information.
 * 
 * @author Lukas Hahmann
 *
 */
public class Player {
	
	/** The plane the player is currently steering */
	private IPlane plane;
	
	private Level.Head lastLevel;
	private Level level;
	private String name;
	private int id;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	

	private SettingManager settingManager;
	
	public Player() {
		this.plane = new Spaceship("spaceship");
	}

	public Level.Head getLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(Level.Head lastLevel) {
		this.lastLevel = lastLevel;
	}

	public void setLevel(Level l) { this.level = l; }

	public Level getLevel() { return level; }

	public IPlane getPlane() {
		return plane;
	}

	public void setPlane(IPlane plane) {
		this.plane = plane;
	}
	
	/**
	 * Creates the SettingManager and all the Settings.
	 */
	public void createSettings(final Skin skin) {
		settingManager = new SettingManager("fly_user_preferences_" + getId());

		//settingManager.addSetting("name", "Playername:", "Test");
		//String[] selection = { "Red", "Blue", "Green", "Yellow" };
		//settingManager.addSetting("color", "Color:", 0, selection);
		settingManager.addBooleanSetting(SettingManager.USE_TOUCH, "Use TouchScreen:", false);
		settingManager.addBooleanSetting(SettingManager.USE_ROLL_STEERING, "Use Rolling:", false);
		settingManager.addBooleanSetting(SettingManager.USE_LOW_PASS_FILTER, "Use LowPassFilter:", false);
		settingManager.addBooleanSetting(SettingManager.USE_AVERAGING, "Use Avg. of Sensor:", false);
		settingManager.addBooleanSetting(SettingManager.SHOW_GATE_INDICATOR, "Show next Gate:", true);
		//settingManager.addBooleanSetting("showGameFinished", "Show Game finished:", false);
		settingManager.addBooleanSetting(SettingManager.SHOW_STEERING, "Show Steering:", false);
		//settingManager.addBooleanSetting("showTime", "Show Time:", false);
		settingManager.addBooleanSetting(SettingManager.SHOW_FPS, "Show FPS:", false);
		settingManager.addBooleanSetting(SettingManager.FIRST_PERSON, "First Person", false);
		
		settingManager.addFloatSetting(SettingManager.ALPHA_SLIDER, "Alpha:", 15.0f, 0.0f, 100.0f, 1.0f);
		settingManager.addFloatSetting(SettingManager.BUFFER_SLIDER, "Buffersize:", 30.0f, 0.0f, 100.0f, 1.0f);
		settingManager.addFloatSetting(SettingManager.CAMERA_OFFSET, "Camera Distance:", 30.0f, 0.0f, 100.0f, 1.0f);
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
}
