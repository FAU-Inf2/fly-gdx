package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SettingScreen;
import de.fau.cs.mad.fly.ui.SplashScreen;
import de.fau.cs.mad.fly.ui.StatisticsScreen;

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
	private MainMenuScreen mainMenuScreen;
	private SettingScreen settingScreen;
	private StatisticsScreen statisticsScreen;
	private GameScreen gameScreen;

	private Loader loader;
	
	private Player player;
	private GameController gameController;

	private Skin skin;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		Bullet.init();
//		FlyEngine.get();
		Assets.init();
		createSkin();
		shapeRenderer = new ShapeRenderer();

		player = PlayerManager.Instance.getCurrentPlayer();
		player.createSettings(skin);

		loader = new Loader(this);

		setMainMenuScreen();
		// disabled for debugging reasons
		// setSplashScreen();
	}
	
	@Override
	public void dispose() {
		Gdx.app.log("Fly", "dispose game");
		
		disposeScreen(splashScreen);
		disposeScreen(loadingScreen);
		disposeScreen(levelChooserScreen);
		disposeScreen(mainMenuScreen);
		disposeScreen(settingScreen);
		disposeScreen(statisticsScreen);
		disposeScreen(gameScreen);
		
		skin.dispose();
	}
	
	public void disposeScreen(Screen screen) {
		if(screen != null) {
			screen.dispose();
			screen = null;
		}
	}

	/**
	 * Creates the Skin for the UI.
	 */
	public void createSkin() {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = (int) (Gdx.graphics.getWidth() * 0.04);
		BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();

		skin = new Skin();
		skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		skin.add("default-font", bitmapFont);

		skin.load(Gdx.files.internal("uiskin.json"));
	}
	
	/**
	 * Getter for the Player.
	 */
	public Player getPlayer() {
		return player;
	}
	
	public GameController getGameController() {
		return gameController;
	}

	/**
	 * Setter for the Player.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Getter for the Skin.
	 */
	public Skin getSkin() {
		return skin;
	}
	
	/**
	 * Getter for the ShapeRenderer.
	 */
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
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
	 * Switches the current screen to the LoadingScreen
	 */
	public void setLoadingScreen() {
		if (loadingScreen == null) {
			loadingScreen = new LoadingScreen(this);
		}
		setScreen(loadingScreen);
	}

	/**
	 * set game screen as current screen.
	 */
	public void setGameScreen() {
		if (gameScreen == null) {
			gameScreen = new GameScreen(this);
		}
		Gdx.app.log("Fly.setGameScreen", "Just a little bit more...");
		setScreen(gameScreen);
	}

	/**
	 * Switches the current Screen to the MainMenuScreen.
	 */
	public void setMainMenuScreen() {
		if (mainMenuScreen == null) {
			mainMenuScreen = new MainMenuScreen();
		}
		setScreen(mainMenuScreen);
	}

	/**
	 * Switches the current Screen to the SettingScreen.
	 */
	public void setSettingScreen() {
		if (settingScreen == null) {
			settingScreen = new SettingScreen();
		}
		setScreen(settingScreen);
	}

	/**
	 * Initializes the GameController for the current level
	 */
	public void initGameController() {
		GameController.Builder builder = new GameController.Builder();
		builder.init(this);

		gameController = builder.build();
		Gdx.app.log("Fly.initGameController", "Controller built.");
		gameController.loadGame();
	}

	public Loader getLoader() {
		return loader;
	}

	
		
	/**
	 * Switches the current Screen to the StatisticsScreen.
	 */
	public void setStatisticsScreen() {
		if (statisticsScreen == null) {
			statisticsScreen = new StatisticsScreen();
		}
		setScreen(statisticsScreen);
    }
}