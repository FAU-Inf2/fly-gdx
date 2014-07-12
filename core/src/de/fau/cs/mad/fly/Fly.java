package de.fau.cs.mad.fly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.GlobalHighScoreScreen;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SettingScreen;
import de.fau.cs.mad.fly.ui.SkinManager;
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
        // FlyEngine.get();
        Assets.init();
        skinManager = new SkinManager("uiskin.json");
        setMainMenuScreen();
        // disabled for debugging reasons
        // setSplashScreen();
    }
    
    @Override
    public void dispose() {
        Gdx.app.log("Fly", "dispose game");
        
        disposeScreen(splashScreen);
        disposeScreen(levelChooserScreen);
        disposeScreen(mainMenuScreen);
        disposeScreen(settingScreen);
        disposeScreen(statisticsScreen);
        disposeScreen(gameScreen);
        disposeScreen(globalHighScoreScreen);
        //skinManager.dispose();
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