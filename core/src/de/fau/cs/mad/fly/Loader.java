package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;

import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;

/**
 * Created by Jakob Falke on 18.06.14.
 */
public class Loader {

	private Fly game;
	private String currentLevelPath;
	private boolean firstLoad = true;

	public Loader(Fly game) {
		this.game = game;
	}

	/**
	 * Initiates the loading of a level and sets the game's current screen to LoadingScreen
	 * @param levelHead the Level.Head of the level to be loaded
	 */
	public void startLoading(Level.Head levelHead) {
		currentLevelPath = levelHead.file.path();
		Gdx.app.log("Loader.startLoading", "Queuing level for loading.");
		Assets.manager.load(currentLevelPath, Level.class);
		Gdx.app.log("Loader.startLoading", "Setting LoadingScreen...");
		game.setLoadingScreen();
	}

	/**
	 * Finishes the loading of a level. Sets the player's current level, initializes the gameController and sets the current screen to GameScreen.
	 */
	public void finishLoading() {
		Gdx.app.log("Loader.finishLoading", "Assets loaded.");
		Level level = Assets.manager.get(currentLevelPath, Level.class);
		level.reset();
		game.getPlayer().setLevel(level);
		game.initGameController();
		game.setGameScreen();
		Gdx.app.log("Loader.finishLoading", "Level loaded.");
	}

	/**
	 * Continues the player's last played level. If no last level is specified defaults to the first level.
	 */
	public void continueLevel() {
		Level.Head levelHead = game.getPlayer().getLastLevel();
		if(levelHead == null) {
			Gdx.app.log("Loader.continueLevel", "No last level set for player. Defaulting to first level..");
			levelHead = LevelChooserScreen.getLevelList().get(0);
		}
		Gdx.app.log("Loader.continueLevel", "Initializing load...");
		startLoading(levelHead);
	}
}
