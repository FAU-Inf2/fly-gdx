package de.fau.cs.mad.fly;

import java.util.ArrayList;

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
	
	public void createSettings() {
		settingManager = new SettingManager("fly_preferences", skin);
		
		settingManager.addTextSetting("name", "Playername:", "Test");
		String[] selection = { "Red", "Blue", "Green", "Yellow" };
		settingManager.addSelectionSetting("color", "Color:", 0, selection);
		settingManager.addCheckBoxSetting("useTouch", "Use TouchScreen:", false);
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
	
	public SettingManager getSettingManager() {
		return settingManager;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return this.level;
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
	
	public void setSettingScreen() {
		setScreen(settingScreen);
	}
	
	public void setHelpScreen() {
		setScreen(helpScreen);
	}
}