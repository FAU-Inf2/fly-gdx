package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.SettingManager;

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
	//TODO: Player name

	private SettingManager settingManager;
	
	public Player() {
		this.plane = new Spaceship();
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
		settingManager = new SettingManager("fly_preferences", skin);

		//settingManager.addSetting("name", "Playername:", "Test");
		//String[] selection = { "Red", "Blue", "Green", "Yellow" };
		//settingManager.addSetting("color", "Color:", 0, selection);
		settingManager.addSetting("use.touch", false);
		settingManager.addSetting("use.rolling", false);
		settingManager.addSetting("use.lowpassfilter", false);
		settingManager.addSetting("use.avg.of.sensor", false);
		settingManager.addSetting("show.next.gate", true);
		settingManager.addSetting("show.game.finished", false);
		settingManager.addSetting("show.steering", false);
		settingManager.addSetting("show.time", false);
		settingManager.addSetting("show.fps", false);
		settingManager.addSetting("alpha", 15.0f, 0.0f, 100.0f, 1.0f);
		settingManager.addSetting("buffersize", 30.0f, 0.0f, 100.0f, 1.0f);
		settingManager.addSetting("camera.distance", 30.0f, 0.0f, 100.0f, 1.0f);
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
}
