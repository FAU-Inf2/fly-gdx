package de.fau.cs.mad.fly.profile;

import java.util.List;

import com.badlogic.gdx.sql.DatabaseCursor;

import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Profile for a player on the device.
 * 
 * @author Tobi
 * 
 */
public class PlayerProfile {
    /**
     * default passed level group id for a new user
     */
    private static final int DEFAULT_PASSED_LEVELGROUP_ID = 1;
    
    /**
     * default passed level id for a new user
     */
    private static final int DEFAULT_PASSED_LEVEL_ID = 1;
    
    /**
     * default chosen level group id for a new user
     */
    private static final int DEFAULT_CHOSEN_LEVELGROUP_ID = 1;
    
    /**
     * default chosen level id for a new user
     */
    private static final int DEFAULT_CHOSEN_LEVEL_ID = 1;
    
    /**
     * maximum length of the name
     */
    public static final int MAX_NAME_LENGTH = 9;
    
    /**
     * The info. read from json of the current level the player is playing or
     * just finished.
     */
    private LevelProfile currentLevelProfile;
    
    /**
     * The plane the player is currently flying.
     */
    private IPlane.Head plane;
    
    /**
     * The amount of money the player currently has. It is the total score of
     * every single play.
     */
    private int money;
    
    /**
     * Sum of all highest scores per level.
     */
    private int totalScoreOfAll = 0;
    
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
     * The current chosen level group
     */
    private LevelGroup currentLevelGroup;
    
    /**
     * the max level group id that user already passed
     */
    private int passedLevelgroupID = DEFAULT_PASSED_LEVELGROUP_ID;
    
    /**
     * /** the max level id of the passedLevelgroup that user already reached
     */
    private int passedLevelID = DEFAULT_PASSED_LEVEL_ID;
    
    private boolean nameChangedSinceLastUpload;
    
    /**
     * Creates a new profile without any more information.
     */
    public PlayerProfile() {
        this.settingManager = new SettingManager("fly_user_preferences_" + getId());
        nameChangedSinceLastUpload = false;
    }
    
    /**
     * Creates a new profile.
     * 
     * @param name
     *            Name of the profile.
     * @param id
     *            ID of the profile
     */
    public PlayerProfile(String name, int id) {
        this();
        setName(name);
        setId(id);
    }
    
    /**
     * @return the totalScoreOfAll, solution A
     */
    public int getTotalScoreOfAll() {
        String selectSQL = "select sum(score) from score where player_id=" + this.getId();
        DatabaseCursor cursor = FlyDBManager.getInstance().selectData(selectSQL);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.next();
            totalScoreOfAll = cursor.getInt(0);
            cursor.close();
        }
        return totalScoreOfAll;
    }
    
    /**
     * get max passed level group id, default is the beginner group
     * 
     * @return
     */
    public int getPassedLevelgroupID() {
        return passedLevelgroupID;
    }
    
    /**
     * set max passed level group id
     * 
     * @param passedLevelgroup
     */
    public void setPassedLevelgroupID(int passedLevelgroup) {
        this.passedLevelgroupID = passedLevelgroup;
    }
    
    /**
     * save max passed level group id to database
     */
    public void savePassedLevelgroupID() {
        PlayerProfileManager.getInstance().updateIntColumn(this, "passed_levelgroup_id", passedLevelgroupID);
    }
    
    /**
     * get the max passed level id, default is the first level of
     * PassedLevelgroup
     * 
     * @return
     */
    public int getPassedLevelID() {
        return passedLevelID;
    }
    
    /**
     * set max passed level id
     * 
     * @param passedLevel
     */
    public void setPassedLevelID(int passedLevel) {
        this.passedLevelID = passedLevel;
    }
    
    /**
     * Checks if the levels after the currently passed level are tutorial levels
     * and increases the passed level id until the first non-tutorial level is
     * found.
     */
    public void checkPassedLevelForTutorials() {
        List<LevelProfile> allLevels = getCurrentLevelGroup().getLevels();
        for (int level = passedLevelID; level < allLevels.size() && allLevels.get(level - 1).isTutorial(); level++) {
            passedLevelID++;
        }
    }
    
    /**
     * save max passed level id to database
     */
    public void savePassedLevelID() {
        PlayerProfileManager.getInstance().updateIntColumn(this, "passed_level_id", passedLevelID);
    }
    
    /**
     * Getter for the fly id.
     * 
     * @return flyID
     */
    public int getFlyID() {
        return flyID;
    }
    
    /**
     * Setter for the fly id.
     * 
     * @param flyID
     */
    public void setFlyID(int flyID) {
        this.flyID = flyID;
    }
    
    /**
     * Getter for the name.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter for the name. Truncates the name if it is longer than
     * MAX_NAME_LENGTH
     * 
     * @param name
     */
    public void setName(String name) {
        if (name.length() <= MAX_NAME_LENGTH) {
            this.name = name;
        } else {
            this.name = name.substring(0, MAX_NAME_LENGTH - 1);
        }
    }
    
    public boolean nameChangedSinceLastUpload() {
        return nameChangedSinceLastUpload;
    }
    
    public void setNameChangedSinceLastUpload(boolean nameChangedSinceLastUpload) {
        this.nameChangedSinceLastUpload = nameChangedSinceLastUpload;
    }
    
    /**
     * Getter for the ID.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Setter for the ID.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Getter for the current money
     * 
     * @return
     */
    public int getMoney() {
        return money;
    }
    
    /**
     * Setter for the current money
     * 
     * @return
     */
    public void setMoney(int money) {
        this.money = money;
    }
    
    /**
     * Adds a certain amount of money to the current money the player has
     * 
     * @param money
     *            The Amount of money to add, may be positive or negative
     * @return false if the new amount of money would be negative, the current
     *         money then remains unchanged
     */
    public boolean addMoney(int money) {
        int newMoney = this.money + money;
        
        if (newMoney < 0) {
            return false;
        }
        
        this.money = newMoney;
        PlayerProfileManager.getInstance().updateIntColumn(this, "total_score", newMoney);
        return true;
    }
    
    /**
     * Adds a certain amount of score to the total scores of the highest score
     * of all levels
     * 
     * @param score
     *            The Amount of score to add, may be positive or negative
     * @return false if the new amount of score would be negative, the total
     *         scores of all then remains unchanged
     */
    public boolean addScore(int score) {
        int newScore = this.totalScoreOfAll + score;
        
        if (newScore < 0) {
            return false;
        }
        
        this.totalScoreOfAll = newScore;
        PlayerProfileManager.getInstance().updateIntColumn(this, "total_geld", totalScoreOfAll);
        return true;
    }
    
    /**
     * Getter for the current chosen level for a new player, return the first
     * level of the current chosen level group
     * 
     * @return currentLevel
     */
    public LevelProfile getCurrentLevelProfile() {
        if (currentLevelProfile == null) {
            LevelGroup group = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelGroup();
            currentLevelProfile = group.getLevelProfile(DEFAULT_CHOSEN_LEVEL_ID);
        }
        return currentLevelProfile;
    }
    
    /**
     * Setter for the current chosen level.
     * 
     * @param currentLevel
     */
    public void setCurrentLevelProfile(LevelProfile currentLevel) {
        if (this.currentLevelProfile != currentLevel) {
            this.currentLevelProfile = currentLevel;
        }
    }
    
    /**
     * Setter for the current chosen level
     * 
     * @param levelID
     */
    public void setCurrentLevelProfile(int levelID) {
        this.currentLevelProfile = this.getCurrentLevelGroup().getLevelProfile(levelID);
    }
    
    /**
     * save current chosen level profile to database
     */
    public void saveCurrentLevelProfile() {
        PlayerProfileManager.getInstance().updateIntColumn(this, "current_level_id", currentLevelProfile.id);
    }
    
    /**
     * get current chosen level group, if it is a new user, return the fist
     * group of all
     * 
     * @return
     */
    public LevelGroup getCurrentLevelGroup() {
        if (currentLevelGroup == null) {
            for (LevelGroup group : LevelGroupManager.getInstance().getLevelGroups()) {
                if (group.id == DEFAULT_CHOSEN_LEVELGROUP_ID) {
                    setCurrentLevelGroup(group);
                }
            }
            if (currentLevelGroup == null) {
                setCurrentLevelGroup(LevelGroupManager.getInstance().getLevelGroups().get(0));
            }
        }
        return currentLevelGroup;
    }
    
    /**
     * set current chosen level group
     * 
     * @param levelGroup
     */
    public void setCurrentLevelGroup(LevelGroup levelGroup) {
        currentLevelGroup = levelGroup;
    }
    
    /**
     * save current level group to database
     */
    public void saveCurrentLevelGroup() {
        PlayerProfileManager.getInstance().updateIntColumn(this, "current_levelgroup_id", currentLevelGroup.id);
    }
    
    /**
     * Is current level the last level of group
     * 
     * @return
     */
    public boolean isLastLevel() {
        List<LevelProfile> allLevels = getCurrentLevelGroup().getLevels();
        if (getCurrentLevelProfile() == allLevels.get(allLevels.size() - 1)) {
            return true;
        }
        return false;
    }
    
    /**
     * Is current level group the last group of all
     * 
     * @return
     */
    public boolean isLastLevelGroup() {
        List<LevelGroup> allGroups = LevelGroupManager.getInstance().getLevelGroups();
        if (this.getCurrentLevelGroup() == allGroups.get(allGroups.size() - 1)) {
            return true;
        }
        return false;
    }
    
    /**
     * If possible currentLevelProfile is set to the next level.
     */
    public boolean setToNextLevel() {
        List<LevelProfile> allLevels = getCurrentLevelGroup().getLevels();
        int nextLevelIndex = allLevels.indexOf(getCurrentLevelProfile()) + 1;
        nextLevelIndex = jumpOverTutorials(allLevels, nextLevelIndex);
        
        if (nextLevelIndex > 0 && nextLevelIndex < allLevels.size()) {
            setCurrentLevelProfile(allLevels.get(nextLevelIndex));
            return true;
        }
        return false;
    }
    
    /**
     * If possible return the next level profile of currentLevelProfile
     */
    public LevelProfile getNextLevel() {
        List<LevelProfile> allLevels = getCurrentLevelGroup().getLevels();
        int nextLevelIndex = allLevels.indexOf(getCurrentLevelProfile()) + 1;
        nextLevelIndex = jumpOverTutorials(allLevels, nextLevelIndex);
        
        if (nextLevelIndex > 0 && nextLevelIndex < allLevels.size()) {
            return allLevels.get(nextLevelIndex);
        }
        return null;
    }
    
    /**
     * Checks if disable tutorial setting is active and if the current next
     * level is a tutorial level and then jumps over all the tutorial levels
     * until it reaches a non-tutorial level.
     * 
     * @param allLevels
     *            All the levels from the current level group.
     * @param nextLevel
     *            The index of the current next level.
     * @return index of the next level which is not a tutorial.
     */
    private int jumpOverTutorials(List<LevelProfile> allLevels, int nextLevel) {
        if (!getSettingManager().getBoolean(SettingManager.DISABLE_TUTORIALS)) {
            return nextLevel;
        }
        int nextLevelAfterTutorials = nextLevel;
        while (nextLevelAfterTutorials < allLevels.size() && allLevels.get(nextLevelAfterTutorials).isTutorial()) {
            nextLevelAfterTutorials++;
        }
        return nextLevelAfterTutorials;
    }
    
    /**
     * If possible currentLevelGroup is set to the next level.
     */
    public void setToNextLevelGroup() {
        if (this.isLastLevel() && !this.isLastLevelGroup()) {
            int currentGroup = 0;
            List<LevelGroup> allGroups = LevelGroupManager.getInstance().getLevelGroups();
            
            while(allGroups.get(currentGroup) != this.getCurrentLevelGroup()) {
                currentGroup++;
            }
            
            if (currentGroup < (allGroups.size() - 1)) {
                this.setCurrentLevelGroup(allGroups.get(currentGroup + 1));
                this.setCurrentLevelProfile(this.getCurrentLevelGroup().getFirstLevel());
            }
        }
    }
    
    /**
     * If possible, get the next level group of current level group
     * 
     * @return
     */
    public LevelGroup getnextLevelGroup() {
        int currentGroup = 0;
        List<LevelGroup> allGroups = LevelGroupManager.getInstance().getLevelGroups();
        for (int i = 0; i < allGroups.size(); i++) {
            if (allGroups.get(i) == this.getCurrentLevelGroup()) {
                currentGroup = i;
                break;
            }
        }
        if (currentGroup < (allGroups.size() - 1)) {
            return allGroups.get(currentGroup + 1);
        }
        return null;
    }
    
    /**
     * Setter for the current plane the player is flying.
     * 
     * @param p
     */
    public void setPlane(IPlane.Head p) {
        this.plane = p;
    }
    
    /**
     * Getter for the current plane the player is flying.
     * 
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
    
    /**
     * Clear the SettingManager.
     */
    public void clearSettingManager() {
        settingManager.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerProfile) {
            return this.getName().equals(((PlayerProfile) o).getName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 7 + getName().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}