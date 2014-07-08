package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.GlobalHighScoreScreen;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SettingScreen;
import de.fau.cs.mad.fly.ui.SplashScreen;
import de.fau.cs.mad.fly.ui.StatisticsScreen;
import de.fau.cs.mad.fly.ui.UI;

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
	private GlobalHighScoreScreen globalHighScoreScreen;

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

		//here start to init the databse staff, by fan
		PlayerManager.getInstance().getCurrentPlayer().getName();
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
		disposeScreen(globalHighScoreScreen);
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
		fontParameter.size = UI.Buttons.FONT_SIZE;
		BitmapFont buttonFont = fontGenerator.generateFont(fontParameter);

		Assets.load(Assets.textureAtlas);
		skin = new Skin(Assets.manager.get(Assets.textureAtlas));
		skin.add("default-font", buttonFont);

		skin.load(Gdx.files.internal("uiskin.json"));
	}

	public GameController getGameController() {
		return gameController;
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
	public void loadLevel(Level.Head head) {
		if (loadingScreen == null) {
			loadingScreen = new LoadingScreen();
		}
		Loader<Level> loader = Loader.create(Assets.manager, head.file.path(), Level.class);
		loader.addProgressListener(new ProgressListener.ProgressAdapter<Level>() {
			@Override
			public void progressFinished(Level level) {
				level.reset();
				PlayerManager.getInstance().getCurrentPlayer().setLevel(level);
				initGameController();
				setGameScreen();
			}
		});
		loadingScreen.initiate(loader);
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
	 * 
	 * It is recreated when the player has switched.
	 */
	public void setSettingScreen() {
		if(settingScreen == null) {
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

	
		
	/**
	 * Switches the current Screen to the StatisticsScreen.
	 */
	public void setStatisticsScreen() {
		if (statisticsScreen == null) {
			statisticsScreen = new StatisticsScreen();
		}
		setScreen(statisticsScreen);
    }
	
	/**
	 * Switches the current Screen to the StatisticsScreen.
	 */
	public void setGlobalHighScoreScreen() {
		if (globalHighScoreScreen == null) {
			globalHighScoreScreen = new GlobalHighScoreScreen();
		}
		setScreen(globalHighScoreScreen);
    }
}