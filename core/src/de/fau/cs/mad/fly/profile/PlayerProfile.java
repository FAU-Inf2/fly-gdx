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
	 * The results of the last level the player has played.
	 */
	private LevelProfile lastLevelProfile;

	/**
	 * The last level the player has played.
	 */
	private LevelProfile currentLevel;
	
	/**
	 * The level the player is currently playing.
	 */
	private Level level;

	/**
	 * The plane the player is currently flying.
	 */
	private IPlane.Head plane;
	
	/**
	 * The amount of money the player currently has
	 */
	private int money;
	
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
     * The chosen level group
     */
	private LevelGroup chosenLevelGroup;

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
	 * Getter for the current money
	 * @return
	 */
	public int getMoney() {
		return money;
	}
	
	/**
	 * Adds a certain amount of money to the current money the player has
	 * @param money       The Amount of money to add, may be positive or negative
	 * @return            false if the new amount of money would be negative, the current money then remains unchanged
	 */
	public boolean addMoney(int money) {
		int newMoney = this.money + money;
		
		if(newMoney < 0) {
			return false;
		}
		
		this.money = newMoney;
		return true;
	}

	/**
	 * Getter for the last level the player has played.
	 * @return lastLevel
	 */
	public LevelProfile getLastLevel() {
		return lastLevelProfile;
	}

	/**
	 * Setter for the last level the player has played.
	 * @param lastLevel
	 */
	public void setLastLevel(LevelProfile lastLevel) {
		this.lastLevelProfile = lastLevel;
	}

	/**
	 * Getter for the level the player is playing.
	 * @return currentLevel
	 */
	public LevelProfile getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Setter for the level the player is playing.
	 * @param currentLevel
	 */
	public void setCurrentLevel(LevelProfile currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	public LevelGroup getChosenLevelGroup() {
		if( chosenLevelGroup == null )
		{
			chosenLevelGroup = LevelGroupManager.getInstance().getLevelGroups().get(0);
		}
		return chosenLevelGroup;
	}

	public void setChosenLevelGroup(LevelGroup levelGroup) {
		chosenLevelGroup = levelGroup;
	}
	
	/**
	 * If possible lastLevel is set to the next level.
	 */
	public boolean nextLevel() {
	    int nextLevelIndex = 0;
	    List<LevelProfile> allLevels = getChosenLevelGroup().getLevels();
	    for(int level = 0; level < allLevels.size(); level++) {
	        if(allLevels.get(level) == lastLevelProfile) {
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