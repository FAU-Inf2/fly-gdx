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

/*
 * Manage all levels and current level.
 * not used yet
 * 
 * 
 * @author Qufang Fan
 */
public class LevelManager {

	private LevelManager() {
	}

	private Level.Head chosenLevel;

	private static LevelManager Instance = new LevelManager();

	public static LevelManager getInstance() {
		return Instance;
	}

	private JsonReader reader = new JsonReader();

	private List<Level.Head> levels = null;

	/**
	 * Creates the level list by parsing the level directory if its not already created.
	 * 
	 * @return sorted list of levels by id.
	 */
	public List<Level.Head> getLevelList() {
		if (levels == null) {
			levels = new ArrayList<Level.Head>();
			FileHandle dirHandle = Gdx.files.internal("levels/");
			readLevelDirectories(levels, dirHandle);
			Collections.sort(levels, new Comparator<Level.Head>() {
				@Override
				public int compare(Level.Head first, Level.Head second){
				    if(first.id < second.id) {
				    	return -1;
				    } else if(first.id > second.id) {
				    	return 1;
				    }
				    return 0;
				}
		    });
		}
		return levels;
	}
	
	/**
	 * Recursively parses the level directory and stores the levels in the list.
	 * 
	 * @param levels		The list to store the levels.
	 * @param dirHandle		The handle to the current directory.
	 */
	private void readLevelDirectories(List<Level.Head> levels, FileHandle dirHandle) {
		for (FileHandle handle : dirHandle.list()) {
			if(handle.isDirectory()) {
				readLevelDirectories(levels, handle);
			} else {
				JsonValue json = reader.parse(handle);
				Level.Head levelHead = new Level.Head();
				levelHead.name = json.getString("name");
				levelHead.id = json.getInt("id");
				levelHead.file = handle;
				levels.add(levelHead);
			}
		}
	}

	public String getLevelName(int levelID) {
		for (Level.Head level : getLevelList()) {
			if (level.id == levelID) {
				return level.name;
			}
		}
		return Integer.toString(levelID);
	}

	public Level.Head getChosedLevel() {
		if (chosenLevel == null) {
			chosenLevel = getLevelList().get(0);
		}
		return chosenLevel;
	}

	public void setChosedLevel(Level.Head levelHead) {
		chosenLevel = levelHead;
	}

}
