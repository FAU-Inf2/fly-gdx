package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Manage read all levels and level group from json files, nothing more.
 * 
 * 
 * @author Qufang Fan
 */
public class LevelGroupManager {	

	private LevelGroupManager() {
		loadLevelGroups("levels/");
	}	
    
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

	private static LevelGroupManager instance;

	public static LevelGroupManager getInstance() {
		return instance;
	}
	
	/**
	 * Creates the level manager singleton instance.
	 */
	public static void createLevelManager() {
		instance = new LevelGroupManager();
	}

	private JsonReader reader = new JsonReader();

	/**
	 * List of level groups.
	 */
	private List<LevelGroup> levelGroups = null;
	
	/**
	 * Reads the main folder for the level and opens the sub directories to create level groups.
	 * 
	 * @param folder		The main folder for the levels.
	 */
	private void loadLevelGroups(String folder) {
		levelGroups = new ArrayList<LevelGroup>();
		//levels = new ArrayList<LevelProfile>();
		
		FileHandle dirHandle = Gdx.files.internal(folder);
		for (FileHandle handle : dirHandle.list()) {
			if(handle.isDirectory()) {
				readLevelGroup(handle);
			}
		}
		
		Collections.sort(levelGroups, levelGroupComparator);
	}
	
	/**
	 * Reads all levels of the current directory and puts them in one level group.
	 * 
	 * @param dirHandle		The current directory.
	 */
	private void readLevelGroup(FileHandle dirHandle) {
		LevelGroup group = new LevelGroup();		
		group.name = dirHandle.name();
		group.id = 0;
		group.groupFileHandle = dirHandle;

		// check for group.json in the directory
		for (FileHandle handle : dirHandle.list()) {
			if(!handle.isDirectory()) {
				JsonValue json = reader.parse(handle);
				if(handle.name().equals("group.json")) {
					group.name = json.getString("name");
					group.id = json.getInt("id") * 100;
				}
			}
		}		
		
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
}
