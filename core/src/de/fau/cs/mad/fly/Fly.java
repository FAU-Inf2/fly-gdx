package de.fau.cs.mad.fly;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.ParticleController;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.GlobalHighScoreScreen;
import de.fau.cs.mad.fly.ui.LevelChooserScreen;
import de.fau.cs.mad.fly.ui.LevelGroupScreen;
import de.fau.cs.mad.fly.ui.LevelsStatisScreen;
import de.fau.cs.mad.fly.ui.LoadingScreen;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.PlaneChooserScreen;
import de.fau.cs.mad.fly.ui.PlaneUpgradeScreen;
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
	
	/**
	 * The version of the app. (works only on android)
	 */
	public static String VERSION = "1.0.0";
	
	/**
	 * True if debug mode is enabled, false otherwise.
	 * <p>
	 * Currently debug mode only disables the level dependencies.
	 */
	public static boolean DEBUG_MODE = true;

    private SplashScreen splashScreen;
    private LevelGroupScreen levelGroupScreen;
    private LevelChooserScreen levelChooserScreen;
    private PlaneChooserScreen planeChooserScreen;
    private PlaneUpgradeScreen planeUpgradeScreen;
    private MainMenuScreen mainMenuScreen;
    private SettingScreen settingScreen;
    private StatisticsScreen statisticsScreen;
    private GameScreen gameScreen;
    private GlobalHighScoreScreen globalHighScoreScreen;
    
    private GameController gameController;
    
    private SkinManager skinManager;
    
    @Override
    public void create() {
        // for nex5 this message comes directly after pressing the icon
        Gdx.app.log("timing", "Fly.create enter");
        
        long time = System.currentTimeMillis();
        Assets.init(); //nex5: 10 ms
        Gdx.app.log("timing", "Fly.create assets init: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        skinManager = new SkinManager("uiskin.json"); // nex5: 150 ms
        Gdx.app.log("timing", "Fly.create creating skin manager: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        new Thread(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				PlayerProfileManager.getInstance().getCurrentPlayerProfile();
				 Gdx.app.log("timing", "Fly.create creating db and getCurrentPlayerProfile " + String.valueOf(System.currentTimeMillis()-time));
			}
		}).start(); //nex5: 1 ms
        Gdx.app.log("timing", "Fly.create starting player profile thread: " + String.valueOf(System.currentTimeMillis()-time));
        
        time = System.currentTimeMillis();
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				LevelGroupManager.createLevelManager(); //nex5: 1500 ms
				Gdx.app.log("timing", "Fly.create create level manager: " + String.valueOf(System.currentTimeMillis()-time));
			}
        }).start();
        Gdx.app.log("timing", "Fly.create start level manager thread: " + String.valueOf(System.currentTimeMillis()-time));
        
        ParticleController.createParticleController();
        
        time = System.currentTimeMillis();
        setMainMenuScreen(); // nex5: 125 ms
        Gdx.app.log("timing", "Fly.create set main menu screen: " + String.valueOf(System.currentTimeMillis()-time));

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
        Loader.getInstance().dispose();
        
        disposeScreen(splashScreen);
        disposeScreen(levelGroupScreen);
        disposeScreen(levelChooserScreen);
        disposeScreen(planeChooserScreen);
        disposeScreen(planeUpgradeScreen);
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
    public void setLevelChooserScreen(LevelGroup group) {
        if (levelChooserScreen == null) {
            levelChooserScreen = new LevelChooserScreen();
        }
        levelChooserScreen.setGroup(group);
        setScreen(levelChooserScreen);
    }
    
    /**
     * Lazy loading of screen to choose plane.
     */
    public void setPlaneChoosingScreen() {
        if (planeChooserScreen == null) {
        	planeChooserScreen = new PlaneChooserScreen();
        }
        setScreen(planeChooserScreen);
    }
    
    /**
     * Lazy loading of screen to upgrade the planes.
     */
    public void setPlaneUpgradeScreen() {
        if (planeUpgradeScreen == null) {
        	planeUpgradeScreen = new PlaneUpgradeScreen();
        }
        setScreen(planeUpgradeScreen);
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
    public void setGlobalHighScoreScreen(LevelGroup levelGroup) {
        if (globalHighScoreScreen == null) {
            globalHighScoreScreen = new GlobalHighScoreScreen(levelGroup);
        }
        setScreen(globalHighScoreScreen);
    }
    
    /**
     * Switches the current Screen to the level StatisticsScreen.
     */
    public void setLevelsStatisScreen(LevelGroup group) {
    	LevelsStatisScreen levelsStatisScreen = new LevelsStatisScreen(group);        
        setScreen(levelsStatisScreen);
    }
    
	protected ArrayList<EventListener> mode3d2dChangedListeners = new ArrayList<EventListener>();
	
	protected int current3d2dMode = Mode3d2dChangedEvent.MODE_2D;

	public void add3d2dChangedListeners(EventListener listener) {
		if (listener != null) {
			mode3d2dChangedListeners.add(listener);
		}
	}

	public void remove3d2dChangedListeners(EventListener listener) {
		mode3d2dChangedListeners.remove(listener);
	}

	/**
	 * To be called when UI change between 2d and 3d
	 * 
	 * @param newMode
	 */
	public void onMode3d2dChanged(int newMode) {
		if (mode3d2dChangedListeners != null) {
			for (EventListener listener : mode3d2dChangedListeners) {
				listener.handle(new Mode3d2dChangedEvent(newMode));
			}
		}
		current3d2dMode = newMode;
	}
    
    
    /** 
     * set screen. Add new check if it is switching between 2d and 3d screen
	 * @param screen may be {@code null} */
    @Override
	public void setScreen(Screen screen) {
    	int newMode = getScreenMode(screen);
		if (getScreenMode(screen) != current3d2dMode) {
			onMode3d2dChanged(newMode);
		}
		super.setScreen(screen);
	}
    
    /**
     * if @param is instance of  GameScreen or LoadingScreen, then return turn.
     * @param screen
     * @return
     */
    private int getScreenMode(Screen screen) {
    	if(screen == null )
    		return 0;
    	if(screen instanceof GameScreen || screen instanceof LoadingScreen )
    		return Mode3d2dChangedEvent.MODE_3D;
    	return Mode3d2dChangedEvent.MODE_2D;
    }
    
    /**
     * Event happens when user switches between 2d UI and 3dUI
     * @author Lenovo
     *
     */
    public class Mode3d2dChangedEvent extends Event {
    	public static final int MODE_3D = 3;
    	public static final int MODE_2D = 2;
    	public int mode = 2;
    	
    	public Mode3d2dChangedEvent(int mode)
    	{
    		this.mode = mode;
    	}    	
    }    
}