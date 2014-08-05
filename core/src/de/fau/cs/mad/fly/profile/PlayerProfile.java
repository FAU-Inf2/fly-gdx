package de.fau.cs.mad.fly.profile;

import java.util.List;

import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Profile for a player on the device.
 * 
 * @author Tobi
 *
 */
public class PlayerProfile {
	/**
	 * The last level the player has played.
	 */
	private Level.Head lastLevel;

	/**
	 * The last level the player has played.
	 */
	private Level.Head currentLevel;
	
	/**
	 * The level the player is currently playing.
	 */
	private Level level;

	/**
	 * The plane the player is currently flying.
	 */
	private IPlane.Head plane;
	
	/**
	 * The name of the player profile.
	 */
	private String name;
	
	/**
	 * The id of the player profile.
	 */
	private int id;
	
	/**
	 * The fly id of the player profile.
	 */
	private int flyID;
	
	/**
	 * The setting manager with all the settings set by this profile.
	 */
    private SettingManager settingManager;

    /**
     * Creates a new profile without any more information.
     */
    public PlayerProfile() {
        this.settingManager = new SettingManager("fly_user_preferences_" + getId());
    }
    
    /**
     * Creates a new profile.
     * @param name      Name of the profile.
     * @param id        ID of the profile
     */
    public PlayerProfile(String name, int id) {
        this();
        setName(name);
        setId(id);
    }

    /**
     * Getter for the fly id.
     * @return flyID
     */
	public int getFlyID() {
		return flyID;
	}

	/**
	 * Setter for the fly id.
	 * @param flyID
	 */
	public void setFlyID(int flyID) {
		this.flyID = flyID;
	}
	
	/**
	 * Getter for the name.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the ID.
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter for the ID.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter for the last level the player has played.
	 * @return lastLevel
	 */
	public Level.Head getLastLevel() {
		return lastLevel;
	}

	/**
	 * Setter for the last level the player has played.
	 * @param lastLevel
	 */
	public void setLastLevel(Level.Head lastLevel) {
		this.lastLevel = lastLevel;
	}

	/**
	 * Getter for the level the player is playing.
	 * @return currentLevel
	 */
	public Level.Head getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Setter for the level the player is playing.
	 * @param currentLevel
	 */
	public void setCurrentLevel(Level.Head currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	/**
	 * If possible lastLevel is set to the next level.
	 */
	public boolean nextLevel() {
	    int nextLevelIndex = 0;
	    List<Level.Head> allLevels = LevelManager.getInstance().getLevelList();
	    for(int level = 0; level < allLevels.size(); level++) {
	        if(allLevels.get(level) == lastLevel) {
	            nextLevelIndex = level+1;
	            level = allLevels.size();
	        }
	    }
	    if(nextLevelIndex < allLevels.size()) {
	        currentLevel = allLevels.get(nextLevelIndex);
	        return true;
	    }
	    return false;
	}

	/**
	 * Setter for the current level the player is playing.
	 * @param l
	 */
	public void setLevel(Level l) {
		this.level = l;
	}

	/**
	 * Getter for the current level the player is playing.
	 * @return level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Setter for the current plane the player is flying.
	 * @param p
	 */
	public void setPlane(IPlane.Head p) {
		this.plane = p;
	}

	/**
	 * Getter for the current plane the player is flying.
	 * @return plane
	 */
	public IPlane.Head getPlane() {
		return plane;
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
}