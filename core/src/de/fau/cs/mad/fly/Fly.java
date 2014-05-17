package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;

import de.fau.cs.mad.fly.levelLoader.Level;

/**
 * Manages the different game screens.
 * <p>
 * Includes screens for SplashScreen, LoadingScreen, GameScreen, MainMenuScreen, OptionScreen, HelpScreen.
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
		levelChooserScreen = new LevelChooserScreen(this);
		splashScreen = new SplashScreen(this);
		loadingScreen = new LoadingScreen(this);
		gameScreen = new GameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		optionScreen = new OptionScreen(this);
		helpScreen = new HelpScreen(this);

		setMainMenuScreen();
		// disabled for debugging reasons
		//setSplashScreen();
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public void setLevelChoosingScreen() {
		setScreen(levelChooserScreen);
	}
	
	public void setSplashScreen() {
		setScreen(splashScreen);
	}
	
	public void setLoadingScreen() {
		setScreen(loadingScreen);
	}
	
	public void setGameScreen() {
		setScreen(gameScreen);
	}
	
	public void setMainMenuScreen() {
		setScreen(mainMenuScreen);
	}
	
	public void setOptionScreen() {
		setScreen(optionScreen);
	}
	
	public void setHelpScreen() {
		setScreen(helpScreen);
	}
}