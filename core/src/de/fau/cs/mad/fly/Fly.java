package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import de.fau.cs.mad.fly.levelLoader.Level;

/**
 * Manages the different game screens.
 * <p>
 * Includes screens for SplashScreen, LoadingScreen, GameScreen, MainMenuScreen,
 * OptionScreen, HelpScreen.
 * 
 * @author Tobias Zangl
 */
public class Fly extends Game {
	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private LevelChooserScreen levelChooserScreen;
	private GameScreen gameScreen;
	private MainMenuScreen mainMenuScreen;
	private OptionScreen optionScreen;
	private HelpScreen helpScreen;
	private Level level;

	@Override
	public void create() {
		setMainMenuScreen();
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return this.level;
	}

	public void setLevelChoosingScreen() {
		if (levelChooserScreen == null) {
			levelChooserScreen = new LevelChooserScreen(this);
		}
		setScreen(levelChooserScreen);
	}

	public void setSplashScreen() {
		if (splashScreen == null) {
			splashScreen = new SplashScreen(this);
		}
		setScreen(splashScreen);
	}

	public void setLoadingScreen() {
		if( loadingScreen == null) {
			loadingScreen = new LoadingScreen(this);
		}
		setScreen(loadingScreen);
	}

	public void setGameScreen() {
		if(gameScreen == null) {
			gameScreen = new GameScreen(this);
		}
		setScreen(gameScreen);
	}

	public void setMainMenuScreen() {
		if(mainMenuScreen == null) {
			mainMenuScreen = new MainMenuScreen(this);
		}
		setScreen(mainMenuScreen);
	}

	public void setOptionScreen() {
		if(optionScreen == null) {
			optionScreen = new OptionScreen(this);
		}
		setScreen(optionScreen);
	}

	public void setHelpScreen() {
		if(helpScreen == null) {
			helpScreen = new HelpScreen(this);
		}
		setScreen(helpScreen);
	}
}