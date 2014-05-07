package de.fau.cs.mad.fly.levelLoader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

/**
 * This class is used to load levels.
 * 
 * @author Lukas Hahmann
 * 
 */
public class LevelManager {

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
	 * Load a level out of a GSON file. As a result only a {@link RawLevel} is
	 * loaded to {@link #level}. It has to be converted afterwards in
	 * {@link #convertLevel()}.
	 * 
	 * @param pathToGsonFile
	 * @throws FileNotFoundException
	 */
	public void loadLevel(String pathToGsonFile) throws FileNotFoundException {
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(pathToGsonFile));
		level = gson.fromJson(reader, Level.class);
		closeQuietly(reader);
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

	/**
	 * Closes a reader and catches potential Exceptions
	 * @param closeable
	 */
	private void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
