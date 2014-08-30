package de.fau.cs.mad.fly;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;

import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.LoadingScreen;

/**
 * Created by Jakob Falke on 18.06.14.
 * 
 * Controller of the loading screen. Only used to load levels.
 */
public class Loader {
    
    private Collection<ProgressListener<Level>> listeners = new ArrayList<ProgressListener<Level>>();
    private float progress = 0;
    private AssetDescriptor<Level> target;
    private static Loader instance;
    
    /**
	 * The 3D info of the current level the player is playing or just finished.
	 */
	private Level currentLevel;
	
    /**
	 * Setter for the current level the player is playing.
	 * @param l
	 */
    public void setCurrentLevel(Level level) {
		this.currentLevel = level;
	}
    /**
	 * Getter for the current level the player is playing.
	 * @return level
	 */
	public Level getCurrentLevel() {
		return currentLevel;
	}
    
	private Loader(){
		
	}
	
	public static Loader getInstance()
	{
		 if(instance == null) {
	            instance = new Loader();
	        }
		 return instance;
	}
	

	
    public void setTarget(AssetDescriptor<Level> target) {
        this.target = target;
    }
    
    public void addProgressListener(ProgressListener<Level> listener) {
        this.listeners.add(listener);
    }
    
    public void initiate() {
        Assets.manager.load(target);
        for (ProgressListener<Level> l : listeners)
            l.progressStarted();
    }
    
    public void update() {
        if (!Assets.manager.update()) {
            float currentProgress = ((float) Assets.manager.getLoadedAssets() / (float) (Assets.manager.getLoadedAssets() + Assets.manager.getQueuedAssets())) * 100f;
            if (currentProgress > progress) {
                progress = currentProgress;
                for (ProgressListener<Level> l : listeners)
                    l.progressUpdated(currentProgress);
            }
        } else {
            for (ProgressListener<Level> l : listeners) {
                l.progressUpdated(100f);
                l.progressFinished(Assets.manager.get(target));
            }
            // clean loader
            progress = 0;
            listeners.clear();
        }
    }
    
    public static Loader create(String target) {
       
        Gdx.app.log("Loader.create", target);
        instance.setTarget(new AssetDescriptor<Level>(target, Level.class));
        return instance;
    }
    
    /**
     * Switches the current screen to the LoadingScreen and starts the loading
     * process.
     * <p>
     * Loads the given level and makes it the current level of the current
     * player.
     */
    public void loadLevel(final LevelProfile levelProfile) {
		if (getCurrentLevel() != null
				&& (!getCurrentLevel().head.file.equals(levelProfile.file))) {

			String levelPath = getCurrentLevel().head.file;
			Gdx.app.log("Gamescreen.hide", "dispose level: " + levelPath);
			Assets.unload(levelPath);
			setCurrentLevel(null);
		}
			
        final LoadingScreen loadingScreen = new LoadingScreen();
        Loader loader = Loader.create(levelProfile.file);
        loadingScreen.initiate(loader);
        loader.initiate();
        ((Fly) Gdx.app.getApplicationListener()).setScreen(loadingScreen);
        PlayerProfileManager.getInstance().getCurrentPlayerProfile().setCurrentLevelProfile(levelProfile);
        loader.addProgressListener(new ProgressListener.ProgressAdapter<Level>() {
            @Override
            public void progressFinished(Level level) {
				level.getGateCircuit().reset();
				level.head.file = levelProfile.file;
				setCurrentLevel(level);
				((Fly) Gdx.app.getApplicationListener()).initGameController();
				
				loadingScreen.addButton();
            }
        });
    }
}
