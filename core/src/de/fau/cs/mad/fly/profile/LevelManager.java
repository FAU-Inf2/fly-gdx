package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import de.fau.cs.mad.fly.res.Level;

/**
 * Manage all levels and current level.
 * 
 * 
 * @author Qufang Fan
 */
public class LevelManager {
	
	/**
	 * Level group with a specific name and a list of levels which belong to the level group.
	 * 
	 * @author Tobi
	 *
	 */
	public class LevelGroup {
		public int id;
		public String name;
		public List<Level.Head> levels;
	}

	private LevelManager() {
		loadLevelGroups("levels/");
	}
	
	/**
	 * Comparator for the level heads.
	 */
	private Comparator<Level.Head> levelComparator = new Comparator<Level.Head>() {
		@Override
		public int compare(Level.Head first, Level.Head second){
		    if(first.id < second.id) {
		    	return -1;
		    } else if(first.id > second.id) {
		    	return 1;
		    }
		    return 0;
		}
    };
    
	/**
	 * Comparator for the level groups.
	 */
	private Comparator<LevelGroup> levelGroupComparator = new Comparator<LevelGroup>() {
		@Override
		public int compare(LevelGroup first, LevelGroup second){
		    if(first.id < second.id) {
		    	return -1;
		    } else if(first.id > second.id) {
		    	return 1;
		    }
		    return 0;
		}
    };

	private Level.Head chosenLevel;

	private static LevelManager instance;

	public static LevelManager getInstance() {
		return instance;
	}
	
	/**
	 * Creates the level manager singleton instance.
	 */
	public static void createLevelManager() {
		instance = new LevelManager();
	}

	private JsonReader reader = new JsonReader();

	/**
	 * List of level groups.
	 */
	private List<LevelGroup> levelGroups = null;
	
	/**
	 * List of all the levels of all level groups.
	 */
	// TODO: use levelGroups instead for everything.
	private List<Level.Head> levels = null;
	
	/**
	 * Reads the main folder for the level and opens the sub directories to create level groups.
	 * 
	 * @param folder		The main folder for the levels.
	 */
	private void loadLevelGroups(String folder) {
		levelGroups = new ArrayList<LevelGroup>();
		levels = new ArrayList<Level.Head>();
		
		FileHandle dirHandle = Gdx.files.internal(folder);
		for (FileHandle handle : dirHandle.list()) {
			if(handle.isDirectory()) {
				readLevels(handle);
			}
		}
		
		Collections.sort(levels, levelComparator);
		Collections.sort(levelGroups, levelGroupComparator);
	}
	
	/**
	 * Reads all levels of the current directory and puts them in one level group.
	 * 
	 * @param dirHandle		The current directory.
	 */
	private void readLevels(FileHandle dirHandle) {
		LevelGroup group = new LevelGroup();		
		group.name = dirHandle.name();
		group.id = 0;
		group.levels = new ArrayList<Level.Head>();
		
		for (FileHandle handle : dirHandle.list()) {
			if(!handle.isDirectory()) {
				JsonValue json = reader.parse(handle);
				if(handle.name().equals("group.json")) {
					group.name = json.getString("name");
					group.id = json.getInt("id");
				} else {
					Level.Head levelHead = new Level.Head();
					levelHead.name = json.getString("name");
					levelHead.id = json.getInt("id");
					levelHead.file = handle;
					group.levels.add(levelHead);
					levels.add(levelHead);
				}
			}
		}
		
		Collections.sort(group.levels, levelComparator);
		levelGroups.add(group);
	}
	
	/**
	 * Getter) for the level group list.
	 * 
	 * @return list of level groups.
	 */
	public List<LevelGroup> getLevelGroups() {
		return levelGroups;
	}
	
	/**
	 * Getter for the level list of a specific level group.
	 * 
	 * @return list of levels.
	 */
	public List<Level.Head> getLevelList() {
		return levels;
	}

	public String getLevelName(int levelID) {
		for (Level.Head level : getLevelList()) {
			if (level.id == levelID) {
				return level.name;
			}
		}
		return Integer.toString(levelID);
	}

	public Level.Head getChosenLevel() {
		if (chosenLevel == null) {
			chosenLevel = getLevelList().get(0);
		}
		return chosenLevel;
	}

	public void setChosenLevel(Level.Head levelHead) {
		chosenLevel = levelHead;
	}

}
