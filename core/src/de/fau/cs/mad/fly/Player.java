package de.fau.cs.mad.fly;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.res.ResourceManager;
import de.fau.cs.mad.fly.ui.SettingManager;

/**
 * Stores all player-specific information.
 * @author Lukas Hahmann
 *
 */
public class Player {
	
	/** The plane the player is currently steering */
	private IPlane plane;
	
	private Level lastLevel;
	//TODO: Player name
	
	private SettingManager settingManager;
	
	public Player() {
		this.lastLevel = ResourceManager.getLevelList().get(0);
		this.plane = new Spaceship();
	}

	public Level getLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(Level lastLevel) {
		this.lastLevel = lastLevel;
	}

	public IPlane getPlane() {
		return plane;
	}

	public void setPlane(IPlane plane) {
		this.plane = plane;
	}
	
	/**
	 * Creates the SettingManager and all the Settings.
	 */
	public void createSettings(Skin skin) {
		settingManager = new SettingManager("fly_preferences", skin);

		settingManager.addSetting("name", "Playername:", "Test");
		String[] selection = { "Red", "Blue", "Green", "Yellow" };
		settingManager.addSetting("color", "Color:", 0, selection);
		settingManager.addSetting("useTouch", "Use TouchScreen:", false);
		settingManager.addSetting("useRoll", "Use Rolling:", false);
		settingManager.addSetting("useLowPass", "Use LowPassFilter:", false);
		settingManager.addSetting("showGateIndicator", "Show next Gate:", true);
		settingManager.addSetting("showLevelInfo", "Show Level Info:", false);
		settingManager.addSetting("showSteering", "Show Steering:", false);
		settingManager.addSetting("showTime", "Show Time:", false);
		settingManager.addSetting("showFPS", "Show FPS:", false);
		settingManager.addSetting("sliderTest", "Slider:", 10.0f, 0.0f, 100.0f, 1.0f);
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
}
