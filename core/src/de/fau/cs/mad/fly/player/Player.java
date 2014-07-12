package de.fau.cs.mad.fly.player;

import java.util.List;

import de.fau.cs.mad.fly.profile.LevelManager;
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
	private int flyID;
	/** The lives the player has at the moment. If lives is lower or equal zero the player is dead. */
    private int lives;
    private SettingManager settingManager;
	
    
    /**
     * Creates a new player without any more information.
     */
    public Player() {       
        this.plane = new Spaceship("spaceship");
        this.settingManager = new SettingManager("fly_user_preferences_" + getId());
    }
    
    /**
     * Creates a new player.
     * @param name      Name of the player.
     * @param id        ID of the player.
     */
    public Player(String name, int id) {
        this();
        setName(name);
        setId(id);
        setLives(1);
    }

	public int getFlyID() {
		return flyID;
	}

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
	 * Getter for the lives of the player.
	 * @return lives
	 */
	public int getLives() {
		return lives;
	}
	
	/**
	 * Decreases the live by 1 if the player has enough lives, otherwise he has 0 lives left.
	 * @return true, if lifes can be decreased (>1) false otherise
	 */
	public boolean decreaseLives() {
		if(lives > 1) {
			lives--;
			return true;
		} else {
			lives = 0;
			return false;
		}
	}
	
	/**
	 * Returns if the player is dead or alive.
	 * @return true if the player is dead because he has 0 lives left, false otherwise.
	 */
	public boolean isDead() {
		if(lives > 0)
			return true;
		return false;
	}
	
	/**
	 * Setter for the lives.
	 * @param lives
	 */
	public void setLives(int lives) {
		this.lives = lives;
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
	 * If possible lastLevel is set to the next level
	 */
	public void nextLevel() {
	    int nextLevelIndex = 0;
	    List<Level.Head> allLevels = LevelManager.getInstance().getLevelList();
	    for(int level = 0; level < allLevels.size(); level++) {
	        if(allLevels.get(level) == lastLevel) {
	            nextLevelIndex = level+1;
	            level = allLevels.size();
	        }
	    }
	    if(nextLevelIndex < allLevels.size()) {
	        lastLevel = allLevels.get(nextLevelIndex);
	    }
	}

	/**
	 * Setter for the current level the player is playing.
	 * @param l
	 */
	public void setLevel(Level l) { this.level = l; }

	/**
	 * Getter for the current level the player is playing.
	 * @return level
	 */
	public Level getLevel() { return level; }

	/**
	 * Getter for the plane of the player.
	 * @return plane
	 */
	public IPlane getPlane() {
		return plane;
	}

	/**
	 * Setter for the plane of the player.
	 * @param plane
	 */
	public void setPlane(IPlane plane) {
		this.plane = plane;
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
}
