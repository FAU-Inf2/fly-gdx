package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.HelpScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SettingManager;
import de.fau.cs.mad.fly.ui.SettingScreen;
import de.fau.cs.mad.fly.ui.SplashScreen;

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
	private SettingScreen settingScreen;
	private HelpScreen helpScreen;
	private Level level;
	
	private SettingManager settingManager;
	
	private Skin skin;
	
	@Override
	public void create() {
		createSkin();
		
		createSettings();
		
		splashScreen = new SplashScreen(this);
		loadingScreen = new LoadingScreen(this);
		gameScreen = new GameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		settingScreen = new SettingScreen(this);
		helpScreen = new HelpScreen(this);

		setMainMenuScreen();
		// disabled for debugging reasons
		//setSplashScreen();
	}
	
	/**
	 * Creates the SettingManager and all the Settings.
	 */
	public void createSettings() {
		settingManager = new SettingManager("fly_preferences", skin);
		
		settingManager.addTextSetting("name", "Playername:", "Test");
		String[] selection = { "Red", "Blue", "Green", "Yellow" };
		settingManager.addSelectionSetting("color", "Color:", 0, selection);
		settingManager.addCheckBoxSetting("useTouch", "Use TouchScreen:", false);
	}
	
	/**
	 * Creates the Skin for the UI.
	 */
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
	
	/**
	 * Getter for the Skin.
	 */
	public Skin getSkin() {
		return skin;
	}
	
	/**
	 * Getter for the SettingManager.
	 */
	public SettingManager getSettingManager() {
		return settingManager;
	}
	
	/**
	 * Setter for the Level.
	 */
	public void setLevel(Level level) {
		this.level = level;
	}
	
	/**
	 * Getter for the Level.
	 */
	public Level getLevel() {
		return this.level;
	}
	
	/**
	 * Switches the current Screen to the SplashScreen.
	 */
	public void setSplashScreen() {
		setScreen(splashScreen);
	}
	
	/**
	 * Switches the current Screen to the LoadingScreen.
	 */
	public void setLoadingScreen() {
		setScreen(loadingScreen);
	}
	
	/**
	 * Switches the current Screen to the GameScreen.
	 */
	public void setGameScreen() {
		setScreen(gameScreen);
	}
	
	/**
	 * Switches the current Screen to the MainMenuScreen.
	 */
	public void setMainMenuScreen() {
		setScreen(mainMenuScreen);
	}
	
	/**
	 * Switches the current Screen to the SettingScreen.
	 */
	public void setSettingScreen() {
		setScreen(settingScreen);
	}
	
	/**
	 * Switches the current Screen to the HelpScreen.
	 */
	public void setHelpScreen() {
		setScreen(helpScreen);
	}
}