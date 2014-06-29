package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
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
	
	public List<Level.Head> getLevelList() {
		List<Level.Head> levels = new ArrayList<Level.Head>();
		FileHandle dirHandle = Gdx.files.internal("levels/");
		for( FileHandle file : dirHandle.list() ) {
			JsonValue json = reader.parse(file);
			Level.Head levelHead = new Level.Head();
			levelHead.name = json.getString("name");
			levelHead.id = json.getInt("id");
			levelHead.file = file;
			levels.add(levelHead);
		}
		return levels;
	}
	
	public Level.Head getChosedLevel()
	{
		if( chosenLevel == null)
		{
			chosenLevel = getLevelList().get(0);
		}
		return chosenLevel;
	}

	public void SetChosedLevel(Level.Head levelHead)
	{
		chosenLevel = levelHead;
	}

}
