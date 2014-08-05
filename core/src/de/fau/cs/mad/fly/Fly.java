package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.GlobalHighScoreScreen;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LevelGroupScreen;
import de.fau.cs.mad.fly.ui.SettingScreen;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.SplashScreen;
import de.fau.cs.mad.fly.ui.StatisticsScreen;
import de.fau.cs.mad.fly.ui.mainMenu.MainMenuScreen;

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
    private LevelGroupScreen levelGroupScreen;
    private LevelChooserScreen levelChooserScreen;
    private MainMenuScreen mainMenuScreen;
    private SettingScreen settingScreen;
    private StatisticsScreen statisticsScreen;
    private GameScreen gameScreen;
    private GlobalHighScoreScreen globalHighScoreScreen;
    
    private GameController gameController;
    
    private SkinManager skinManager;
    
    @Override
    public void create() {
        Assets.init();
        skinManager = new SkinManager("uiskin.json");
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				PlayerProfileManager.getInstance().getCurrentPlayerProfile();
			}
		}).start();
        
        LevelManager.createLevelManager();
        
        setMainMenuScreen();

        // disabled for debugging reasons
        // setSplashScreen();
    }
    
	@Override
	public void resume () {
		super.resume();
		
		if(skinManager == null) {
			skinManager = new SkinManager("uiskin.json");
		}
		
		
	}
    
    @Override
    public void dispose() {
        Gdx.app.log("Fly", "dispose game");
        
        FlyDBManager.getInstance().dispose();
        
        disposeScreen(splashScreen);
        disposeScreen(levelGroupScreen);
        disposeScreen(levelChooserScreen);
        disposeScreen(mainMenuScreen);
        disposeScreen(settingScreen);
        disposeScreen(statisticsScreen);
        disposeScreen(gameScreen);
        disposeScreen(globalHighScoreScreen);
        
        // TODO: enable after the bug with disappearing widgets after restarting the app is fixed
        //skinManager.dispose();
        //skinManager = null;
    }
    
    public void disposeScreen(Screen screen) {
        if (screen != null) {
            screen.dispose();
            screen = null;
        }
    }
    
    public GameController getGameController() {
        return gameController;
    }
    
    /**
     * Getter for the Skin which is stored in the skin manager.
     */
    public Skin getSkin() {
        return skinManager.getSkin();
    }
    
    /**
     * Lazy loading of screen to choose level group.
     */
    public void setLevelGroupScreen() {
        if (levelGroupScreen == null) {
            levelGroupScreen = new LevelGroupScreen();
        }
        setScreen(levelGroupScreen);
    }
    
    /**
     * Lazy loading of screen to choose level.
     */
    public void setLevelChooserScreen(LevelManager.LevelGroup group) {
        if (levelChooserScreen == null) {
            levelChooserScreen = new LevelChooserScreen();
        }
        levelChooserScreen.setGroup(group);
        setScreen(levelChooserScreen);
    }
    
    /**
     * Switches the current Screen to the SplashScreen.
     */
    public void setSplashScreen() {
        if (splashScreen == null) {
            splashScreen = new SplashScreen();
        }
        setScreen(splashScreen);
    }
    
    /**
     * set game screen as current screen.
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
        if (settingScreen == null) {
            settingScreen = new SettingScreen();
        }
        setScreen(settingScreen);
    }
    
    /**
     * Initializes the GameController for the current level
     */
    public void initGameController() {
        GameControllerBuilder builder = new GameControllerBuilder();
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