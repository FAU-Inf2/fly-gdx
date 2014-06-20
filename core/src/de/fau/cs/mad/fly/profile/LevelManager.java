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
	
	public static LevelManager Instance = new LevelManager();
	
	private JsonReader reader = new JsonReader();
	
	public List<Level.Head> getLevelList() {
		List<Level.Head> hs = new ArrayList<Level.Head>();
		FileHandle dirHandle = Gdx.files.internal("levels/");
		for( FileHandle f : dirHandle.list() ) {
			JsonValue j = reader.parse(f);
			Level.Head h = new Level.Head();
			h.name = j.getString("name");
			h.file = f;
			hs.add(h);
		}
		return hs;
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
