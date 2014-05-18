package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.levelLoader.Level;
import de.fau.cs.mad.fly.ui.HelpScreen;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.OptionScreen;
import de.fau.cs.mad.fly.ui.SplashScreen;

/**
 * Manages the different game screens.
 * <p>
 * Includes screens for SplashScreen, LoadingScreen, GameScreen, MainMenuScreen,
 * OptionScreen, HelpScreen and LevelChooserScreen.
 * <p>
 * All screens should be loaded only when they are needed. To create an instance
 * of all of them takes about 4 seconds on a Nex5.
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
	
	private Skin skin;
	
	@Override
	public void create() {
		createSkin();	
		setMainMenuScreen();
		// disabled for debugging reasons
		//setSplashScreen();
	}
	
	public void createSkin() {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = 28;
		BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();

		skin = new Skin();
		skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		skin.add("default-font", bitmapFont);

		skin.load(Gdx.files.internal("uiskin.json"));
	}
	
	public Skin getSkin() {
		return skin;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return this.level;
	}

	/**
	 * Lazy loading of screen to choose level.
	 */
	public void setLevelChoosingScreen() {
		if (levelChooserScreen == null) {
			levelChooserScreen = new LevelChooserScreen();
		}
		setScreen(levelChooserScreen);
	}

	/**
	 * Lazy loading of splash screen with the Fly logo.
	 */
	public void setSplashScreen() {
		if (splashScreen == null) {
			splashScreen = new SplashScreen(this);
		}
		setScreen(splashScreen);
	}

	/**
	 * Lazy loading of loading screen.
	 */
	public void setLoadingScreen() {
		if (loadingScreen == null) {
			loadingScreen = new LoadingScreen(this);
		}
		setScreen(loadingScreen);
	}

	/**
	 * Lazy loading of game screen.
	 */
	public void setGameScreen() {
		if (gameScreen == null) {
			gameScreen = new GameScreen(this);
		}
		setScreen(gameScreen);
	}

	/**
	 * Lazy loading of main menu screen.
	 */
	public void setMainMenuScreen() {
		if (mainMenuScreen == null) {
			mainMenuScreen = new MainMenuScreen(this);
		}
		setScreen(mainMenuScreen);
	}

	/**
	 * Lazy loading of option screen.
	 */
	public void setOptionScreen() {
		if (optionScreen == null) {
			optionScreen = new OptionScreen(this);
		}
		setScreen(optionScreen);
	}

	/**
	 * lazy loading of help screen.
	 */
	public void setHelpScreen() {
		if (helpScreen == null) {
			helpScreen = new HelpScreen(this);
		}
		setScreen(helpScreen);
	}
}