package de.fau.cs.mad.fly.game;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.EndlessLevelGenerator;
import de.fau.cs.mad.fly.features.game.GateIndicator;
import de.fau.cs.mad.fly.features.overlay.FPSOverlay;
import de.fau.cs.mad.fly.features.overlay.GameFinishedOverlay;
import de.fau.cs.mad.fly.features.overlay.InfoButtonOverlay;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.overlay.PauseGameOverlay;
import de.fau.cs.mad.fly.features.overlay.ScoreOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringResetOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeLeftOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeUpOverlay;
import de.fau.cs.mad.fly.features.overlay.TouchScreenOverlay;
import de.fau.cs.mad.fly.levels.DefaultLevel;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.levels.Level_11;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.EventAdapter;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.GateCircuit;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.ui.BackProcessor;
import de.fau.cs.mad.fly.ui.UI;

/**
 * This class implements the builder pattern to create a GameController with all
 * of its dependent components.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GameControllerBuilder {
    private GameController gc = new GameController();
    private Fly game;
    private Player player;
    private PlayerProfile playerProfile;
    private Stage stage;
    private Level level;
    private ArrayList<IFeatureLoad> optionalFeaturesToLoad;
    private ArrayList<IFeatureInit> optionalFeaturesToInit;
    private ArrayList<IFeatureUpdate> optionalFeaturesToUpdate;
    private ArrayList<IFeatureRender> optionalFeaturesToRender;
    private ArrayList<IFeatureDraw> optionalFeaturesToDraw;
    private ArrayList<IFeatureFinish> optionalFeaturesToFinish;
    private ArrayList<IFeatureDispose> optionalFeaturesToDispose;
    private FlightController flightController;
    private CameraController cameraController;
    private TimeController timeController;
    private ScoreController scoreController;
    private EndlessLevelGenerator generator;
    
    /**
     * Creates a basic {@link GameController} with a certain level, linked to
     * the current player, its settings and the selected level. It interprets
     * the setting of the player and and creates based on the settings optional
     * features.
     * 
     * @param game
     *            needed to get the player for the current settings and the
     *            level
     * @return new GameController with the current selected level and the
     *         selected settings
     */
    public GameControllerBuilder init(final Fly game) {
        // clear everything in the builder from a possible earlier call
        optionalFeaturesToLoad = new ArrayList<IFeatureLoad>();
        optionalFeaturesToInit = new ArrayList<IFeatureInit>();
        optionalFeaturesToUpdate = new ArrayList<IFeatureUpdate>();
        optionalFeaturesToRender = new ArrayList<IFeatureRender>();
        optionalFeaturesToDraw = new ArrayList<IFeatureDraw>();
        optionalFeaturesToFinish = new ArrayList<IFeatureFinish>();
        optionalFeaturesToDispose = new ArrayList<IFeatureDispose>();
        
        this.game = game;
        player = new Player();
        playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        level = playerProfile.getLevel();
        flightController = new FlightController(player, playerProfile);
        cameraController = new CameraController(player, playerProfile);
        
        stage = new Stage();
        timeController = new TimeController();
        TimeUpOverlay timeUpOverlay = new TimeUpOverlay(game.getSkin(), stage);
        timeController.registerTimeIsUpListener(timeUpOverlay);
        
        scoreController = new ScoreController();
        
        float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) Gdx.graphics.getWidth();
        float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) Gdx.graphics.getHeight();
        float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
        Viewport viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
        stage.setViewport(viewport);
        
        GateCircuit gateCircuit = level.getGateCircuit();
        optionalFeaturesToLoad.add(gateCircuit);
        
        addPlayerPlane();
        Bullet.init();
        CollisionDetector.createCollisionDetector();
        CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
        collisionDetector.getCollisionContactListener().addListener(gateCircuit);
        collisionDetector.getCollisionContactListener().addListener(new ICollisionListener<Spaceship, GameObject>() {
            @Override
            public void onCollision(Spaceship ship, GameObject g) {
                if (g.isDummy()) {
                    return;
                }
                Player player = GameController.getInstance().getPlayer();

                if (!player.decreaseLives()) {
                    //Debug.setOverlay(0, "DEAD");
                    game.getGameController().finishGame();
                } else {
                    //Debug.setOverlay(0, player.getLives());
                }
            }
        });
        
        gateCircuit.createGateRigidBodies();
        
        Gdx.app.log("Builder.init", "Registering EventListeners for level.");

        gateCircuit.addEventListener(new EventAdapter() {
            @Override
            public void onGatePassed(Gate passed) {
            	GateCircuit gateCircuit = level.getGateCircuit();
            	
                if (level.head.name.equals("Endless")) {
                    for (Gate g : generator.getGates())
                        g.unmark();
                } else {
                    for (Gate g : gateCircuit.allGates())
                        g.unmark();
                }
                int len = passed.successors.length;
                for (int i = 0; i < len; i++) {
                	gateCircuit.getGateById(passed.successors[i]).mark();
                }
            }
        });
        
        gateCircuit.addEventListener(scoreController);
        
        if (level.head.name.equals("Endless")) {
            generator = new EndlessLevelGenerator(PlayerProfileManager.getInstance().getCurrentPlayerProfile().getLevel());
            
            gateCircuit.addEventListener(new EventAdapter() {
                @Override
                public void onGatePassed(Gate passed) {
                    generator.addRandomGate(passed);
                }
            });
        }
        
        Gdx.app.log("Builder.init", "Final work for level done.");
        
        checkAndAddSettingFeatures();
        
        addLevelFeatures(level);
        
        return this;
    }
    
    /**
     * Checks the preferences if the standard features should be used and adds them to
     * the game controller if necessary.
     */
    private void checkAndAddSettingFeatures() {
        // if needed for debugging: Debug.init(game.getSkin(), stage, 1);
        
        Preferences preferences = playerProfile.getSettingManager().getPreferences();
        addGateIndicator();
        addTimeLeftOverlay();
        addScoreOverlay();
        addInfoOverlays();
        
        if (preferences.getBoolean(SettingManager.SHOW_PAUSE)) {
            addPauseGameOverlay();
        }
        if (preferences.getBoolean(SettingManager.SHOW_FPS)) {
            addFPSOverlay();
        }
        if (preferences.getBoolean(SettingManager.SHOW_STEERING)) {
            addSteeringOverlay();
        }
        if (preferences.getBoolean(SettingManager.USE_TOUCH)) {
            addTouchScreenOverlay();
        } else if (preferences.getBoolean(SettingManager.SHOW_RESET_STEERING)) {
            addSteeringResetOverlay();
        }
        if (preferences.getBoolean(SettingManager.VIBRATE_WHEN_COLLIDE)) {
            CollisionDetector.getInstance().getCollisionContactListener().addListener(new ICollisionListener<Spaceship, GameObject>() {
                @Override
                public void onCollision(Spaceship ship, GameObject g) {
                    if (g.isDummy()) {
                        return;
                    }
                    Gdx.input.vibrate(500);
                }
            });
        }
        addGameFinishedOverlay();
    }
    
    /**
     * Checks the {@link Level.levelClass} value and uses the default class features or the features of a given class if found and invoked correctly.
     * 
     * @param level			The currently used level with a specific level class if stored in the json.
     */
    private void addLevelFeatures(Level level) {
    	if(level.levelClass == null || level.levelClass.equals("") || level.levelClass.equals("DefaultClass")) {
    		addDefaultLevel();
    	} else {    		
    		try {
				Class<?> c = Class.forName("de.fau.cs.mad.fly.levels." + level.levelClass);
				Object obj = c.newInstance();
				
				@SuppressWarnings("rawtypes")
				Class[] parameterTypes = new Class[1];	
				parameterTypes[0] = GameControllerBuilder.class;
				
				Method method = c.getDeclaredMethod("create", parameterTypes);
				method.invoke(obj, this);
				
				Gdx.app.log("Builder", level.levelClass + " used.");
			} catch (Exception e) {
				addDefaultLevel();
			}
    	}
    }
    
    /**
     * Adds the default level features.
     */
    private void addDefaultLevel() {
    	ILevel defaultLevel = new DefaultLevel();
		defaultLevel.create(this);
		Gdx.app.log("Builder", "DefaultLevel used.");
    }
    
    /**
     * Puts the feature in the optional feature lists depending on the
     * interfaces it implements.
     * 
     * @param feature
     *            The feature to put in the lists.
     */
    public void addFeatureToLists(Object feature) {
        if (feature instanceof IFeatureLoad) {
            optionalFeaturesToLoad.add((IFeatureLoad) feature);
        }
        
        if (feature instanceof IFeatureInit) {
            optionalFeaturesToInit.add((IFeatureInit) feature);
        }
        
        if (feature instanceof IFeatureUpdate) {
            optionalFeaturesToUpdate.add((IFeatureUpdate) feature);
        }
        
        if (feature instanceof IFeatureRender) {
            optionalFeaturesToRender.add((IFeatureRender) feature);
        }
        
        if (feature instanceof IFeatureDraw) {
            optionalFeaturesToDraw.add((IFeatureDraw) feature);
        }
        
        if (feature instanceof IFeatureFinish) {
            optionalFeaturesToFinish.add((IFeatureFinish) feature);
        }
        
        if (feature instanceof IFeatureDispose) {
            optionalFeaturesToDispose.add((IFeatureDispose) feature);
        }
        
    	if (feature instanceof ICollisionListener) {
    		CollisionDetector.getInstance().getCollisionContactListener().addListener((ICollisionListener) feature);
    	}
    	
    	if (feature instanceof IntegerTimeListener) {
    		timeController.registerIntegerTimeListener((IntegerTimeListener) feature);
    	}
    }
    
    /**
     * Adds a {@link GateIndicator} to the GameController, that is initialized,
     * updated every frame and updated, when a gate is passed.
     * 
     * @return Builder instance with GateIndicator
     */
    private GameControllerBuilder addGateIndicator() {
        GateIndicator gateIndicator = new GateIndicator(game.getSkin());
        addFeatureToLists(gateIndicator);
        return this;
    }
    
    /**
     * Adds a {@link TimeLeftOverlay} to the GameController.
     * 
     * @return Builder instance with TimeLeftOverlay
     */
    private GameControllerBuilder addTimeLeftOverlay() {
        TimeLeftOverlay timeLeftOverlay = new TimeLeftOverlay(game.getSkin(), stage);
        timeController.registerIntegerTimeListener(timeLeftOverlay);
        return this;
    }
    
    /**
     * Adds a {@link ScoreOverlay} to the GameController.
     * 
     * @return Builder instance with ScoreOverlay
     */
    private GameControllerBuilder addScoreOverlay() {
        ScoreOverlay scoreOverlay = new ScoreOverlay(game.getSkin(), stage);
        scoreController.registerScoreChangeListener(scoreOverlay);
        return this;
    }
    
    /**
     * Adds a {@link InfoOverlay} and a {@link InfoButtonOverlay} to the GameController.
     * 
     * @return Builder instance with InfoOverlay and InfoButtonOverlay
     */
    private GameControllerBuilder addInfoOverlays() {
    	InfoOverlay.createInfoOverlay(game.getSkin(), stage);
    	addFeatureToLists(InfoOverlay.getInstance());
    	InfoButtonOverlay.createInfoButtonOverlay(game.getSkin(), stage);
        return this;
    }
    
    /**
     * Adds a {@link FPSOverlay} to the GameController, that is updated every
     * frame.
     * 
     * @return Builder instance with FPSOverlay
     */
    private GameControllerBuilder addFPSOverlay() {
        FPSOverlay fpsOverlay = new FPSOverlay(game.getSkin(), stage);
        addFeatureToLists(fpsOverlay);
        return this;
    }
    
    /**
     * Adds a {@link SteeringOverlay} to the GameController, that is updated
     * every frame.
     * 
     * @return Builder instance with SteeringOverlay
     */
    private GameControllerBuilder addSteeringOverlay() {
        SteeringOverlay steeringOverlay = new SteeringOverlay(flightController, game.getSkin());
        addFeatureToLists(steeringOverlay);
        return this;
    }
    
    /**
     * Adds a {@link TouchScreenOverlay} to the GameController, that is updated
     * every frame.
     * 
     * @return Builder instance with TouchScreenOverlay
     */
    private GameControllerBuilder addTouchScreenOverlay() {
        TouchScreenOverlay touchScreenOverlay = new TouchScreenOverlay(stage, game.getSkin());
        addFeatureToLists(touchScreenOverlay);
        return this;
    }
    
    /**
     * Adds a {@link GameFinishedOverlay} to the GameController, that is
     * initialized, updated every frame and updated when the game is finished.
     * 
     * @return Builder instance with GameFinishedOverlay
     */
    private GameControllerBuilder addGameFinishedOverlay() {
        GameFinishedOverlay gameFinishedOverlay = new GameFinishedOverlay(game.getSkin(), stage);
        addFeatureToLists(gameFinishedOverlay);
        return this;
    }
    
    /**
     * Adds a {@link PauseGameOverlay} to the GameController, that is
     * initialized and displayed every frame
     * 
     * @return Builder instance with PauseGameOverlay
     */
    private GameControllerBuilder addPauseGameOverlay() {
        PauseGameOverlay pauseGameOverlay = new PauseGameOverlay(game.getSkin(), stage);
        addFeatureToLists(pauseGameOverlay);
        return this;
    }
    
    /**
     * Adds a {@link SteeringResetOverlay} to the GameController, that is
     * initialized and displayed every frame
     * 
     * @return Builder instance with SteeringResetOverlay
     */
    private GameControllerBuilder addSteeringResetOverlay() {
        SteeringResetOverlay steeringResetOverlay = new SteeringResetOverlay(game.getSkin(), flightController, stage);
        addFeatureToLists(steeringResetOverlay);
        return this;
    }
    
    /**
     * Adds a {@link IPlane} to the GameController, that is initialized, updated
     * every frame and updated when the game is finished.
     * 
     * @return Builder instance with IPlane
     */
    private GameControllerBuilder addPlayerPlane() {
        IPlane plane = player.getPlane();
        addFeatureToLists(plane);
        return this;
    }
    
    /**
     * Creates a new GameController out of your defined preferences in the other
     * methods before.
     * 
     * @return new GameController
     */
    public GameController build() {
    	GameController.instance = gc;
        gc.stage = stage;
        gc.optionalFeaturesToLoad = optionalFeaturesToLoad;
        gc.optionalFeaturesToInit = optionalFeaturesToInit;
        gc.optionalFeaturesToUpdate = optionalFeaturesToUpdate;
        gc.optionalFeaturesToRender = optionalFeaturesToRender;
        gc.optionalFeaturesToDraw = optionalFeaturesToDraw;
        gc.optionalFeaturesToFinish = optionalFeaturesToFinish;
        gc.optionalFeaturesToDispose = optionalFeaturesToDispose;
        gc.level = level;
        gc.player = player;
        gc.flightController = flightController;
        gc.cameraController = cameraController;
        gc.batch = new ModelBatch();
        gc.setTimeController(timeController);
        gc.scoreController = scoreController;
        gc.setInputProcessor(new InputMultiplexer(stage, flightController, new BackProcessor()));
        
        level.getGateCircuit().addEventListener(new EventAdapter() {
            @Override
            public void onFinished() {
                gc.finishGame();
            }
        });
        return gc;
    }
}