package de.fau.cs.mad.fly;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import de.fau.cs.mad.fly.db.FlyDBManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.ParticleController;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.screens.GameScreen;
import de.fau.cs.mad.fly.ui.screens.LoadingScreen;
import de.fau.cs.mad.fly.ui.screens.MainMenuScreen;

/**
 * Manages the different game screens.
 * <p>
 * Includes screens for SplashScreen, LoadingScreen, GameScreen, MainMenuScreen,
 * OptionScreen, HelpScreen and LevelChooserScreen.
 * <p>
 * All screens should be loaded only when they are needed.
 * 
 * @author Tobias Zangl
 */
public class Fly extends Game implements Loadable<Fly> {
    
    /**
     * The version of the app. (works only on android)
     */
    public static String VERSION = "1.1.0";
    
    /**
     * True if debug mode is enabled, false otherwise.
     * <p>
     * Currently debug mode only disables the level dependencies.
     */
    public static boolean DEBUG_MODE = true;
    
    private LoadingScreen<Fly> splashScreen;
    
    private GameScreen gameScreen;
    private MainMenuScreen mainMenuScreen;
    
    private GameController gameController;
    
    private List<ProgressListener<Fly>> listeners = new ArrayList<ProgressListener<Fly>>();
    
    private int progress = 0;
    
    @Override
    public void create() {
        
        // init Assets, has to be done in the main Tread because it needs the
        // OpenGl context that is only offered by the main Thread.
        Assets.init();
        
        // load SkinManager, has to be done in the main Tread because it needs
        // the OpenGl context that is only offered by the main Thread.
        SkinManager.getInstance();
        
        addProgressListener(new ProgressListener.ProgressAdapter<Fly>() {
            @Override
            public void progressFinished(Fly fly) {
                if (mainMenuScreen == null) {
                    mainMenuScreen = new MainMenuScreen();
                }
                mainMenuScreen.set();
            }
        });
        
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                init();
            }
        };
        
        Thread loadingThread = new Thread(runnable);
        loadingThread.start();
        
        setSplashScreen();
    }
    
    protected void init() {
        LevelGroupManager.createLevelManager();
        progress = 10;
        PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        progress = 90;
        ParticleController.createParticleController();
        progress = 100;
    }
    
    /**
     * Allows to add a listener, to listen for the loading progress of the app.
     */
    public void addProgressListener(ProgressListener<Fly> listener) {
        this.listeners.add(listener);
    }
    
    /**
     * Method that is called by by the {@link LoadingScreen} during the loading
     * progress to get the current loading progress.
     */
    public void update() {
        int size = listeners.size();
        int i;
        for (i = 0; i < size; i++) {
            listeners.get(i).progressUpdated(progress);
        }
        if (progress >= 100) {
            for (i = 0; i < size; i++) {
                listeners.get(i).progressFinished(this);
            }
            progress = 0;
        }
    }
    
    @Override
    public void resume() {
        super.resume();
    }
    
    @Override
    public void dispose() {
        mainMenuScreen.dispose();
        mainMenuScreen = null;
        FlyDBManager.getInstance().dispose();
        Loader.getInstance().dispose();
        SkinManager.getInstance().dispose();
        Assets.dispose();
    }
    
    public GameController getGameController() {
        return gameController;
    }
    
    /**
     * Switches the current screen to the {@link LoadingScreen}.
     */
    public void setSplashScreen() {
        if (splashScreen == null) {
            splashScreen = new LoadingScreen<Fly>(this, null);
        }
        setScreen(splashScreen);
    }
    
    /**
     * Switches the current screen to the {@link GameScreen}.
     */
    public void setGameScreen() {
        if (gameScreen == null) {
            gameScreen = new GameScreen();
        }
        setScreen(gameScreen);
    }
    
    /**
     * set screen. Add new check if it is switching between 2d and 3d screen
     * 
     * @param screen
     *            may be {@code null}
     */
    @Override
    public void setScreen(Screen screen) {
        int newMode = getScreenMode(screen);
        if (getScreenMode(screen) != current3d2dMode) {
            onMode3d2dChanged(newMode);
        }
        super.setScreen(screen);
    }
    
    public MainMenuScreen getMainMenuScreen() {
        return mainMenuScreen;
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
    
    protected ArrayList<EventListener> mode3d2dChangedListeners = new ArrayList<EventListener>();
    
    protected int current3d2dMode = Mode3d2dChangedEvent.MODE_2D;
    
    private OrientationProvider orientationProvider;
    
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
     * if @param is instance of GameScreen or LoadingScreen, then return turn.
     * 
     * @param screen
     * @return
     */
    private int getScreenMode(Screen screen) {
        if (screen == null) {
            return 0;
        }
        if (screen instanceof GameScreen) {
            return Mode3d2dChangedEvent.MODE_3D;
        }
        return Mode3d2dChangedEvent.MODE_2D;
    }
    
    /**
     * Event happens when user switches between 2d UI and 3dUI
     * 
     * @author Lenovo
     * 
     */
    public class Mode3d2dChangedEvent extends Event {
        public static final int MODE_3D = 3;
        public static final int MODE_2D = 2;
        public int mode = 2;
        
        public Mode3d2dChangedEvent(int mode) {
            this.mode = mode;
        }
    }
    
    /**
     * This method is called by a component that controls weather the screen may
     * be swapped or not. It is necessary for {@link #orientationSwapped}.
     * 
     * @param orientationProvider
     */
    public void addOrientationProvider(OrientationProvider orientationProvider) {
        this.orientationProvider = orientationProvider;
    }
    
    /** Method to check weather the screen is swapped or not */
    public boolean orientationSwapped() {
        if (orientationProvider == null) {
            return false;
        } else {
            return orientationProvider.orientationSwapped();
        }
    }
}