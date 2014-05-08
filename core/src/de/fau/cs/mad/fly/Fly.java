package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;

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
	private GameScreen gameScreen;
	private MainMenuScreen mainMenuScreen;
	private OptionScreen optionScreen;
	private HelpScreen helpScreen;
	
	@Override
	public void create() {
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