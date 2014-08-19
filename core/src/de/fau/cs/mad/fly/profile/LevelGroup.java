package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Level group with a specific name and a list of levels which belong to the
 * level group.
 * 
 * @author Tobi
 * 
 */
public class LevelGroup {
	public int id;
	public String name;
	private List<LevelProfile> levels;
	public FileHandle groupFileHandle;

	/**
	 * Comparator for the level heads.
	 */
	private Comparator<LevelProfile> levelComparator = new Comparator<LevelProfile>() {
		@Override
		public int compare(LevelProfile first, LevelProfile second) {
			if (first.id < second.id) {
				return -1;
			} else if (first.id > second.id) {
				return 1;
			}
			return 0;
		}
	};
	
	public List<LevelProfile> getLevels()
	{
		if (levels == null ){			
			readLevelGroup();
		}
		return levels;
	}

	public LevelProfile getFirstLevel() {
		
		return getLevels().get(0);
	}

	private void readLevelGroup() {
		levels = new ArrayList<LevelProfile>();
		// check for all the levels
		JsonReader reader = new JsonReader();
		for (FileHandle handle : groupFileHandle.list()) {
			if (!handle.isDirectory()) {
				if (!handle.name().equals("group.json")) {
					JsonValue json = reader.parse(handle);
					LevelProfile levelprofile = new LevelProfile();
					levelprofile.name = json.getString("name");
					levelprofile.id = json.getInt("id");// todo ? + group.id;
					levelprofile.file = handle;
					levels.add(levelprofile);
				}
			}
		}
		Collections.sort(levels, levelComparator);
	}

	public String getLevelName(int levelID) {
		for (LevelProfile level : getLevels()) {
			if (level.id == levelID) {
				return level.name;
			}
		}
		return Integer.toString(levelID);
	}
}