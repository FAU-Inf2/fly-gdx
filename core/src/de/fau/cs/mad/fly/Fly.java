package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SettingManager;
import de.fau.cs.mad.fly.ui.SettingScreen;
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
	private SettingScreen settingScreen;
	private Player player;
	public GameController gameController;

	private SettingManager settingManager;

	private Skin skin;
	
	private float screenWidth, screenHeight;

	@Override
	public void create() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		Assets.init();
		createSkin();

		createSettings();
		player = new Player();

		setMainMenuScreen();
		// disabled for debugging reasons
		// setSplashScreen();
	}

	/**
	 * Creates the SettingManager and all the Settings.
	 */
	public void createSettings() {
		settingManager = new SettingManager(this, "fly_preferences", skin);

		settingManager.addSetting("name", "Playername:", "Test");
		String[] selection = { "Red", "Blue", "Green", "Yellow" };
		settingManager.addSetting("color", "Color:", 0, selection);
		settingManager.addSetting("useTouch", "Use TouchScreen:", false);
		settingManager.addSetting("useRoll", "Use Rolling:", false);
		settingManager.addSetting("showOverlay", "Show Overlay:", false);
		settingManager.addSetting("showTime", "Show Time:", false);
		settingManager.addSetting("showFPS", "Show FPS:", false);
		settingManager.addSetting("sliderTest", "Slider:", 10.0f, 0.0f, 100.0f, 1.0f);
	}

	/**
	 * Creates the Skin for the UI.
	 */
	public void createSkin() {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
				Gdx.files.internal("OpenSans-Regular.ttf"));
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = (int) (screenWidth * 0.04);
		BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();

		skin = new Skin();
		skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		skin.add("default-font", bitmapFont);

		skin.load(Gdx.files.internal("uiskin.json"));
	}
	
	/**
	 * Returns the absolute x-position calculated with the percent and the current screen width.
	 * 
	 * @param percentX the x-position on the screen in percent from 0-100.
	 * @return the absolute x-position on the screen.
	 */
	public int getAbsoluteX(float percentX) {
		return (int) (screenWidth * percentX);
	}
	
	/**
	 * Returns the absolute y-position calculated with the percent and the current screen height.
	 * 
	 * @param percentY the y-position on the screen in percent from 0-100.
	 * @return the absolute y-position on the screen.
	 */
	public int getAbsoluteY(float percentY) {
		return (int) (screenHeight * percentY);
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
	 * Lazy loading of screen to choose level.
	 */
	public void setLevelChoosingScreen() {
		if (levelChooserScreen == null) {
			levelChooserScreen = new LevelChooserScreen();
		}
		setScreen(levelChooserScreen);
	}

	/**
	 * Switches the current Screen to the SplashScreen.
	 */
	public void setSplashScreen() {
		if (splashScreen == null) {
			splashScreen = new SplashScreen(this);
		}
		setScreen(splashScreen);
	}

	/**
	 * Switches the current Screen to the LoadingScreen.
	 */
	public void setLoadingScreen() {
		// TODO: Wrong Way ! we have to find a better solution than creating new loading screens everytime
		
		//if (loadingScreen == null) {
			loadingScreen = new LoadingScreen(this);
		//}
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
	 * Switches the current Screen to the MainMenuScreen.
	 */
	public void setMainMenuScreen() {
		if (mainMenuScreen == null) {
			mainMenuScreen = new MainMenuScreen(this);
		}
		setScreen(mainMenuScreen);
	}

	/**
	 * Switches the current Screen to the SettingScreen.
	 */
	public void setSettingScreen() {
		if (settingScreen == null) {
			settingScreen = new SettingScreen(this);
		}
		setScreen(settingScreen);
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}