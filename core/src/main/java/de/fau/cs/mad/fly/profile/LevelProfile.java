package de.fau.cs.mad.fly.profile;

/**
 * Level properties for read level from json file
 * 
 * @author Fan
 * 
 */
public class LevelProfile {
	/**
	 * Name of the level.
	 */
    public String name;
    
    /**
     * Id of the level.
     */
    public int id;
    
    /**
     * File name of the level.
     */
    public String file;
    
    /**
     * Type of the level.
     * <p>
     * Value of 0 if its a normal level.
     * Value of 1 if its a tutorial level.
     */
    public int type;
    
    /**
     * Checks if the level is a tutorial level.
     * @return true if the level is a tutorial level, false otherwise.
     */
    public boolean isTutorial() {
    	return (type == 1);
    }
    
    /**
     * Checks if the level is an endless level.
     * @return true if the level is an endless level, false otherwise.
     */
    public boolean isEndless() {
        return (name.equals("Endless"));
    }
    
    /**
     * Checks if the level is an endless rails level.
     * @return true if the level is an endless rails level, false otherwise.
     */
    public boolean isEndlessRails() {
        return name.equals("EndlessRails");
    }
}