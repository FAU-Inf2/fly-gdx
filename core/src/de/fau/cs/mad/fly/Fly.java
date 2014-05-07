package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;

/**
 * Manages the different game screens:
 * - SplashScreen
 * - GameScreen
 * - MainMenuScreen
 * - OptionScreen
 * - HelpScreen
 * 
 * @author Tobias Zangl
 */
public class Fly extends Game {
	SplashScreen splashScreen;
	GameScreen gameScreen;
	MainMenuScreen mainMenuScreen;
	OptionScreen optionScreen;
	HelpScreen helpScreen;
	
	@Override
	public void create() {
		splashScreen = new SplashScreen(this);
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