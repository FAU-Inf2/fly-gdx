package de.fau.cs.mad.fly.levelLoader;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

/**
 * This class is used to load levels.
 * 
 * @author Lukas Hahmann
 * 
 */
public class LevelManager {
	/**
	 * path to the level directory in the assets directory which contains the
	 * levels
	 */
	public final String LEVEL_DIRECTORY = "levels/";

	/**
	 * ending of each level-file
	 */
	public final String LEVEL_ENDING = ".json";

	/**
	 * Gson object which is necessary to load a level as a GSON file
	 * 
	 * @see LevelManager#loadLevel
	 */
	private Gson gson = new Gson();

	/**
	 * Parameter to save a level, that is loaded from a GSON file
	 */
	private Level level;

	/**
	 * Load a level out of a file.
	 * 
	 * The level is searched in the {@link #LEVEL_DIRECTORY}. The ending
	 * {@link #LEVEL_ENDING} is added automatically. As a result only a
	 * {@link RawLevel} is loaded to {@link #level}. It has to be converted
	 * afterwards in {@link #convertLevel()}.
	 * 
	 * @param levelName
	 * @throws FileNotFoundException
	 */
	public void loadLevel(String levelName) throws FileNotFoundException {
		String levelPath = LEVEL_DIRECTORY + levelName + LEVEL_ENDING;
		if (Gdx.files.internal(levelPath).exists()) {
			FileHandle levelFile;
			levelFile = Gdx.files.internal(levelPath);
			if (levelFile.length() > 0) {
				Gdx.app.log("loadLevel", "level loaded");
				level = gson.fromJson(levelFile.readString(), Level.class);
			} else {
				Gdx.app.log("LevelLoader", "failed to load level: " + levelPath);
			}
		}
		else {
			Gdx.app.log("LevelLoader", "level does not exist: " + levelPath);
		}
	}

	/**
	 * Converts {@link #level} to a {@link Level} where all information is
	 * generated to create the 3D world.
	 * 
	 * @see #convertGatePositions()
	 * 
	 * @return {@link #level}
	 */
	public Level convertLevel() {
		convertGatePositions();
		level.calculateRadius();
		return level;
	}

	private void convertGatePositions() {
	}

}
