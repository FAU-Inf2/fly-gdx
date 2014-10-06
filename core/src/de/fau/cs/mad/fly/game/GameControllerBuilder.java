package de.fau.cs.mad.fly.game;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.EndlessLevelGenerator;
import de.fau.cs.mad.fly.features.game.EndlessRailLevelGenerator;
import de.fau.cs.mad.fly.features.overlay.BackButtonOverlay;
import de.fau.cs.mad.fly.features.overlay.FPSOverlay;
import de.fau.cs.mad.fly.features.overlay.GameFinishedOverlay;
import de.fau.cs.mad.fly.features.overlay.GateIndicator;
import de.fau.cs.mad.fly.features.overlay.InfoButtonOverlay;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.overlay.ScoreOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeLeftOverlay;
import de.fau.cs.mad.fly.features.overlay.TouchScreenOverlay;
import de.fau.cs.mad.fly.features.upgrades.ChangePointsUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ChangeSteeringUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ChangeTimeUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.InstantSpeedUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.LinearSpeedUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ResizeGatesUpgradeHandler;
import de.fau.cs.mad.fly.graphics.shaders.FlyShaderProvider;
import de.fau.cs.mad.fly.levels.DefaultLevel;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.GateCircuit;
import de.fau.cs.mad.fly.res.GateCircuitAdapter;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.sound.Playable;
import de.fau.cs.mad.fly.ui.BackProcessor;
import de.fau.cs.mad.fly.ui.MainMenuScreen;
import de.fau.cs.mad.fly.ui.SkinManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * This class implements the builder pattern to create a GameController with all
 * of its dependent components.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GameControllerBuilder {
    private Player player;
    private PlayerProfile playerProfile;
    private Stage stage;
    private Level level;
    private List<IFeatureLoad> optionalFeaturesToLoad;
    private List<IFeatureInit> optionalFeaturesToInit;
    private List<IFeatureUpdate> optionalFeaturesToUpdate;
    private List<IFeatureRender> optionalFeaturesToRender;
    private List<IFeatureDraw> optionalFeaturesToDraw;
    private List<IFeatureFinish> optionalFeaturesToFinish;
    private List<IFeatureDispose> optionalFeaturesToDispose;
    private FlightController flightController;
    private CameraController cameraController;
    private TimeController timeController;
    private ScoreController scoreController;
    private EndlessLevelGenerator generator;
    private AudioManager audioManager;
    
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
        clearFeatureLists();
        
        player = new Player();
        playerProfile = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
        level = Loader.getInstance().getCurrentLevel();
        switch( Gdx.app.getType() ) {
            case iOS:
                try {
                    Constructor c = Class.forName("de.fau.cs.mad.fly.ios.input.IOSFlightController").getConstructor(Player.class, PlayerProfile.class);
                    flightController = (FlightController) c.newInstance(player, playerProfile);
                } catch (Exception e) {
                    throw new GdxRuntimeException("Error instantiating IOSFlightController", e);
                }
                break;
            default:
                flightController = new FlightController(player, playerProfile);
        }
        flightController.init();
        cameraController = new CameraController(player, playerProfile);
        
        stage = new Stage();
        timeController = new TimeController();
        
        scoreController = new ScoreController();
        
        audioManager = new AudioManager();
        
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
        collisionDetector.getCollisionContactListener().addListener(new ICollisionListener() {
            @Override
            public void onCollision(GameObject g1, GameObject g2) {
                GameObject g;
                if (g1 instanceof Spaceship) {
                    g = g2;
                } else if (g2 instanceof Spaceship) {
                    g = g1;
                } else {
                    return;
                }
                
                if (g.isDummy()) {
                    return;
                }
                audioManager.play(AudioManager.Sounds.CRASH);
                Player currentPlayer = GameController.getInstance().getPlayer();

                if (!currentPlayer.decreaseLives()) {
                    // Debug.setOverlay(0, "DEAD");
                    game.getGameController().finishGame(false);
                } else {
                    // Debug.setOverlay(0, player.getLives());
                }
            }
        });
        
        gateCircuit.createGateRigidBodies();
        
        createDecoRigidBodies(collisionDetector, level);
        
        gateCircuit.addListener(new GateCircuitAdapter() {
            @Override
            public void onGatePassed(GateGoal passed) {
                GateCircuit gateCircuit = level.getGateCircuit();
                
                List<GateGoal> gates;
                if (level.head.isEndless()) {
                    gates = generator.getGates();
                } else {
                    gates = gateCircuit.allGateGoals();
                }
                int size = gates.size();
                for (int i = 0; i < size; i++) {
                    gates.get(i).unmark();
                }
                
                int len = passed.successors.length;
                for (int i = 0; i < len; i++) {
                    gateCircuit.getGateGoalById(passed.successors[i]).mark();
                }
            }
        });
        
        gateCircuit.addListener(scoreController);
        
        if (level.head.isEndless()) {
            generator = new EndlessLevelGenerator(Loader.getInstance().getCurrentLevel(), this);
            
            gateCircuit.addListener(new GateCircuitAdapter() {
                @Override
                public void onGatePassed(GateGoal passed) {
                    generator.addRandomGate(passed);
                    int extraTime = generator.getExtraTime();
                    timeController.addBonusTime(extraTime);
                }
            });
        } else if (level.head.isEndlessRails()) {
            generator = new EndlessRailLevelGenerator(Loader.getInstance().getCurrentLevel(), this);
            flightController = new RailFlightController(player, playerProfile, generator, level.start);
            CollisionDetector.getInstance().getCollisionContactListener().addListener((ICollisionListener) flightController);
            
            gateCircuit.addListener(new GateCircuitAdapter() {
                @Override
                public void onGatePassed(GateGoal passed) {
                    int extraTime = generator.getExtraTime();
                    timeController.addBonusTime(extraTime);
                }
            });
        }
        
        checkAndAddSettingFeatures();
        
        checkAndAddUpgradeHandler();
        
        addLevelFeatures(level);
        
        return this;
    }
    
    /**
     * Clears all feature lists, when created, else they are new created.
     * <p>
     * As they are created together it is sufficient to check only one of them.
     */
    private void clearFeatureLists() {
        if (optionalFeaturesToLoad == null) {
            optionalFeaturesToLoad = new ArrayList<IFeatureLoad>();
            optionalFeaturesToInit = new ArrayList<IFeatureInit>();
            optionalFeaturesToUpdate = new ArrayList<IFeatureUpdate>();
            optionalFeaturesToRender = new ArrayList<IFeatureRender>();
            optionalFeaturesToDraw = new ArrayList<IFeatureDraw>();
            optionalFeaturesToFinish = new ArrayList<IFeatureFinish>();
            optionalFeaturesToDispose = new ArrayList<IFeatureDispose>();
        } else {
            optionalFeaturesToLoad.clear();
            optionalFeaturesToInit.clear();
            optionalFeaturesToUpdate.clear();
            optionalFeaturesToRender.clear();
            optionalFeaturesToDraw.clear();
            optionalFeaturesToFinish.clear();
            optionalFeaturesToDispose.clear();
        }
    }
    
    /**
     * Creates the rigid bodies for the static decoration in the level.
     * 
     * @param collisionDetector
     *            The collision detector.
     * @param level
     *            The level with the list of decorations.
     */
    private void createDecoRigidBodies(CollisionDetector collisionDetector, Level level) {
        Gdx.app.log("GameControllerBuilder.createDecoRigidBodies", "Setting up collision for decoration.");
        
        GameObject o;
        List<GameObject> gameObjects = level.components;
        int size = gameObjects.size();
        btCollisionShape displayShape;
        for (int i = 0; i < size; i++) {
            o = gameObjects.get(i);
            if (!o.getId().equals(Level.BORDER_NAME)) {
                displayShape = collisionDetector.getShapeManager().createConvexShape(o.getModelId(), o);
                o.createRigidBody(o.getModelId(), displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
                collisionDetector.addRigidBody(o);
            }
        }
    }
    
    /**
     * Checks the preferences if the standard features should be used and adds
     * them to the game controller if necessary.
     */
    private void checkAndAddSettingFeatures() {
        // if needed for debugging: Debug.init(game.getSkin(), stage, 1);
        
        Preferences preferences = playerProfile.getSettingManager().getPreferences();
        addGateIndicator();
        addTimeLeftOverlay();
        addScoreOverlay();
        addInfoOverlays();
        // do not show back button on Android, because there the softkey back
        // button is available
        if (!ApplicationType.Android.equals(Gdx.app.getType())) {
            addBackButtonOverlay();
        }
        if (preferences.getBoolean(SettingManager.SHOW_FPS)) {
            addFPSOverlay();
        }
        if (preferences.getBoolean(SettingManager.USE_TOUCH)) {
            addTouchScreenOverlay();
        }
        if (preferences.getBoolean(SettingManager.VIBRATE_WHEN_COLLIDE)) {
            CollisionDetector.getInstance().getCollisionContactListener().addListener(new ICollisionListener() {
                @Override
                public void onCollision(GameObject g1, GameObject g2) {
                    GameObject g;
                    if (g1 instanceof Spaceship) {
                        g = g2;
                    } else if (g2 instanceof Spaceship) {
                        g = g1;
                    } else {
                        return;
                    }
                    
                    if (g.isDummy()) {
                        return;
                    }
                    Gdx.input.vibrate(500);
                }
            });
        }
        if(preferences.getBoolean(SettingManager.DISABLE_SOUND)) {
            audioManager.mute();
        }
        addGameFinishedOverlay();
    }
    
    /**
     * Checks the level upgrades and adds the corresponding handler to the game
     * controller.
     */
    private void checkAndAddUpgradeHandler() {
        if (checkUpgrade("InstantSpeedUpgrade")) {
            addFeatureToLists(new InstantSpeedUpgradeHandler());
        }
        if (checkUpgrade("ChangeTimeUpgrade")) {
            addFeatureToLists(new ChangeTimeUpgradeHandler());
        }
        if (checkUpgrade("ChangePointsUpgrade")) {
            addFeatureToLists(new ChangePointsUpgradeHandler());
        }
        if (checkUpgrade("ResizeGatesUpgrade")) {
            addFeatureToLists(new ResizeGatesUpgradeHandler());
        }
        if (checkUpgrade("ChangeSteeringUpgrade")) {
            addFeatureToLists(new ChangeSteeringUpgradeHandler());
        }
        if (checkUpgrade("LinearSpeedUpgrade")) {
            addFeatureToLists(new LinearSpeedUpgradeHandler());
        }
    }
    
    /**
     * Checks in the upgrade list if it contains an upgrade with the specified
     * type.
     * 
     * @param type
     *            The type to check for.
     * @return true, if it contains an upgrade, false otherwise.
     */
    private boolean checkUpgrade(String type) {
        int size = level.getCollectibleManager().getCollectibles().size();
        for (int i = 0; i < size; i++) {
            if (level.getCollectibleManager().getCollectibles().get(i).getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks the {@link Level. levelClass} value and uses the default class
     * features or the features of a given class if found and invoked correctly.
     * 
     * @param level
     *            The currently used level with a specific level class if stored
     *            in the json.
     */
    private void addLevelFeatures(Level level) {
        if (level.levelClass == null || "".equals(level.levelClass) || level.levelClass.equals("DefaultClass")) {
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
                Gdx.app.log("GameController.addFeatures", "unknown level: " + level.levelClass);
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
        
        if (feature instanceof GateCircuitListener) {
            level.getGateCircuit().addListener((GateCircuitListener) feature);
        }
    }
    
    /**
     * Adds a {@link GateIndicator} to the GameController, that is initialized,
     * updated every frame and updated, when a gate is passed.
     * 
     * @return Builder instance with GateIndicator
     */
    private GameControllerBuilder addGateIndicator() {
        TextureRegion region = SkinManager.getInstance().getSkin().getRegion("arrow");
        region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        GateIndicator gateIndicator = new GateIndicator(region);
        addFeatureToLists(gateIndicator);
        return this;
    }
    
    /**
     * Adds a {@link TimeLeftOverlay} to the GameController.
     * 
     * @return Builder instance with TimeLeftOverlay
     */
    private GameControllerBuilder addTimeLeftOverlay() {
        TimeLeftOverlay timeLeftOverlay = new TimeLeftOverlay(stage, level.getLeftTime());
        timeController.registerIntegerTimeListener(timeLeftOverlay);
        return this;
    }
    
    /**
     * Adds a {@link ScoreOverlay} to the GameController.
     * 
     * @return Builder instance with ScoreOverlay
     */
    private GameControllerBuilder addScoreOverlay() {
        ScoreOverlay scoreOverlay = new ScoreOverlay(stage);
        scoreController.registerScoreChangeListener(scoreOverlay);
        return this;
    }
    
    /**
     * Adds a {@link InfoOverlay} and a {@link InfoButtonOverlay} to the
     * GameController.
     * 
     * @return Builder instance with InfoOverlay and InfoButtonOverlay
     */
    private GameControllerBuilder addInfoOverlays() {
        InfoOverlay.createInfoOverlay(stage);
        addFeatureToLists(InfoOverlay.getInstance());
        InfoButtonOverlay.createInfoButtonOverlay(stage);
        return this;
    }
    
    /**
     * Adds a {@link FPSOverlay} to the {@link GameController}, that is updated
     * every frame.
     * 
     * @return Builder instance with {@link FPSOverlay}
     */
    private GameControllerBuilder addFPSOverlay() {
        FPSOverlay fpsOverlay = new FPSOverlay(stage);
        addFeatureToLists(fpsOverlay);
        return this;
    }
    
    /**
     * Adds {@link BackButtonOverlay} to show a button to return to
     * {@link MainMenuScreen}.
     * 
     * @return Builder instance with {@link BackButtonOverlay}
     */
    private GameControllerBuilder addBackButtonOverlay() {
        BackButtonOverlay backButtonOverlay = new BackButtonOverlay(stage);
        addFeatureToLists(backButtonOverlay);
        return this;
    }
    
    /**
     * Adds a {@link TouchScreenOverlay} to the GameController, that is updated
     * every frame.
     * 
     * @return Builder instance with TouchScreenOverlay
     */
    private GameControllerBuilder addTouchScreenOverlay() {
        TouchScreenOverlay touchScreenOverlay = new TouchScreenOverlay(stage);
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
        GameFinishedOverlay gameFinishedOverlay = new GameFinishedOverlay(stage);
        addFeatureToLists(gameFinishedOverlay);
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
        GameController gc = GameController.getInstance();
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
        // gc.batch = new ModelBatch();
        gc.batch = new ModelBatch(null, new FlyShaderProvider(), null);
        gc.setTimeController(timeController);
        gc.scoreController = scoreController;
        gc.setInputProcessor(new InputMultiplexer(stage, flightController, new BackProcessor()));
        gc.audioManager = audioManager;

        level.getGateCircuit().addListener(new GateCircuitAdapter() {
            @Override
            public void onFinished() {
                GameController.getInstance().finishGame(true);
            }
        });
        return gc;
    }
}