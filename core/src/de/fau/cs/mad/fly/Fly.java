package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.game.CameraController;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
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

	private Skin skin;
	private ShapeRenderer shapeRenderer;
	
	private float screenWidth, screenHeight;

	@Override
	public void create() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		Bullet.init();
		Assets.init();
		createSkin();
		shapeRenderer = new ShapeRenderer();

		player = new Player();
		player.createSettings(skin);

		setMainMenuScreen();
		// disabled for debugging reasons
		// setSplashScreen();
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
	 * Getter for the Player.
	 */
	public Player getPlayer() {
		return player;
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
	 * Switches the current Screen to the LoadingScreen.
	 */
	public void loadLevel() {		
		if (loadingScreen == null) {
			loadingScreen = new LoadingScreen(this);
		}
		setScreen(loadingScreen);
		// TODO: this should be level dependent in the future
		Assets.load();

		GameController.Builder builder = new GameController.Builder();
		builder.init(this);
		
		gameController = builder.build();
		
		gameController.loadGame();
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
}