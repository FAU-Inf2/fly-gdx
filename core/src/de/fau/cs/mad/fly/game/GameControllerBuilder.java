package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Debug;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.AsteroidBelt;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.game.EndlessLevelGenerator;
import de.fau.cs.mad.fly.features.game.GateIndicator;
import de.fau.cs.mad.fly.features.overlay.FPSOverlay;
import de.fau.cs.mad.fly.features.overlay.GameFinishedOverlay;
import de.fau.cs.mad.fly.features.overlay.PauseGameOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringResetOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeLeftOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeUpOverlay;
import de.fau.cs.mad.fly.features.overlay.TouchScreenOverlay;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.settings.SettingManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * This class implements the builder pattern to create a GameController with
 * all of its dependent components.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GameControllerBuilder {
    private GameController gc = new GameController();
	private Fly game;
	private Player player;
	private Stage stage;
	private Level level;
	private ArrayList<IFeatureLoad> optionalFeaturesToLoad;
	private ArrayList<IFeatureInit> optionalFeaturesToInit;
	private ArrayList<IFeatureUpdate> optionalFeaturesToUpdate;
	private ArrayList<IFeatureRender> optionalFeaturesToRender;
	private ArrayList<IFeatureFinish> optionalFeaturesToFinish;
	private ArrayList<IFeatureDispose> optionalFeaturesToDispose;
	private FlightController flightController;
	private CameraController cameraController;
	private TimeController timeController;
	private EndlessLevelGenerator generator;

	/**
	 * Creates a basic {@link GameController} with a certain level, linked
	 * to the current player, its settings and the selected level. It
	 * interprets the setting of the player and and creates based on the
	 * settings optional features.
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
		optionalFeaturesToFinish = new ArrayList<IFeatureFinish>();
		optionalFeaturesToDispose = new ArrayList<IFeatureDispose>();
		
		this.game = game;
		player = PlayerManager.getInstance().getCurrentPlayer();
		level = player.getLevel();
		flightController = new FlightController(player);
		cameraController = new CameraController(player);

		stage = new Stage();
		timeController = new TimeController();
        TimeUpOverlay timeUpOverlay = new TimeUpOverlay(game.getSkin(), stage);
        timeController.registerTimeIsUpListener(timeUpOverlay);
        
		float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float) Gdx.graphics.getWidth();
		float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float) Gdx.graphics.getHeight();
		float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
		Viewport viewport = new FillViewport(Gdx.graphics.getWidth() * scalingFactor, Gdx.graphics.getHeight() * scalingFactor, stage.getCamera());
		stage.setViewport(viewport);
		optionalFeaturesToLoad.add(level);

		// FlyEngine engine = FlyEngine.get();
		// engine.setLevel(level);
		// level.addEventListener(engine);
		// for ( String s : level.scripts )
		// engine.load(Gdx.files.internal( "scripts/app/" + s ));

		addPlayerPlane();
		Bullet.init();
		CollisionDetector.createCollisionDetector();
		CollisionDetector collisionDetector = CollisionDetector.getInstance();

		collisionDetector.getCollisionContactListener().addListener(level);
		collisionDetector.getCollisionContactListener().addListener(new ICollisionListener<Spaceship, GameObject>() {
			@Override
			public void onCollision(Spaceship ship, GameObject g) {
				if(g.isDummy()) {
					return;
				}
				
				Gdx.input.vibrate(500);
				Player player = PlayerManager.getInstance().getCurrentPlayer();
				if (player.decreaseLives()) {
					Debug.setOverlay(0, "DEAD");
					game.getGameController().finishGame();
				} else {
					Debug.setOverlay(0, player.getLives());
				}
			}
		});

		level.createGateRigidBodies();

		Gdx.app.log("Builder.init", "Registering EventListeners for level.");

		level.addEventListener(new Level.EventAdapter() {
			@Override
			public void onGatePassed(Level.Gate passed, Iterable<Level.Gate> current) {
				if(level.head.name.equals("Endless")) {
					for (Level.Gate g : generator.getGates())
						g.unmark();
				} else {
					for (Level.Gate g : level.allGates())
						g.unmark();
				}
				for (Level.Gate g : passed.successors)
					g.mark();
			}
		});

		if(level.head.name.equals("Endless")) {
			generator = new EndlessLevelGenerator(PlayerManager.getInstance().getCurrentPlayer().getLevel());
			
			PlayerManager.getInstance().getCurrentPlayer().getLevel().addEventListener(new Level.EventAdapter() {
				@Override
				public void onGatePassed(Level.Gate passed, Iterable<Level.Gate> current) {
					
					generator.addRandomGate(passed);
				}
			});
		
		}
		
		Gdx.app.log("Builder.init", "Final work for level done.");

		checkAndAddFeatures();

		return this;
	}

	/**
	 * Checks the preferences if the features should be used and adds them
	 * to the game controller if necessary.
	 */
	private void checkAndAddFeatures() {
		// if needed for debugging: Debug.init(game.getSkin(), stage, 1);

		addAsteroidBelt();
		
		//addSpeedUpgrade();

		Preferences preferences = player.getSettingManager().getPreferences();
		addGateIndicator();
		addTimeLeftOverlay();
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
		} else if(preferences.getBoolean(SettingManager.SHOW_RESET_STEERING)){
			addSteeringResetOverlay();
		}
		addGameFinishedOverlay();
	}
	
	/**
	 * Puts the feature in the optional feature lists depending on the interfaces it implements.
	 * 
	 * @param feature		The feature to put in the lists.
	 */
	private void addFeatureToLists(Object feature) {
		if(feature instanceof IFeatureLoad) {
			optionalFeaturesToLoad.add((IFeatureLoad) feature);
		}
		
		if(feature instanceof IFeatureInit) {
			optionalFeaturesToInit.add((IFeatureInit) feature);
		}
		
		if(feature instanceof IFeatureUpdate) {
			optionalFeaturesToUpdate.add((IFeatureUpdate) feature);
		}
		
		if(feature instanceof IFeatureRender) {
			optionalFeaturesToRender.add((IFeatureRender) feature);
		}
		
		if(feature instanceof IFeatureFinish) {
			optionalFeaturesToFinish.add((IFeatureFinish) feature);
		}
		
		if(feature instanceof IFeatureDispose) {
			optionalFeaturesToDispose.add((IFeatureDispose) feature);
		}
	}

	/**
	 * Adds a {@link GateIndicator} to the GameController, that is
	 * initialized, updated every frame and updated, when a gate is passed.
	 * 
	 * @return Builder instance with GateIndicator
	 */
	private GameControllerBuilder addGateIndicator() {
		GateIndicator gateIndicator = new GateIndicator();
		addFeatureToLists(gateIndicator);
		return this;
	}

	/**
	 * Adds a {@link AsteroidBelt} to the GameController, that is loaded,
	 * initialized, updated every frame and disposed.
	 * 
	 * @return Builder instance with AsteroidBelt
	 */
	private GameControllerBuilder addAsteroidBelt() {
		AsteroidBelt asteroidBelt = new AsteroidBelt(10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
		addFeatureToLists(asteroidBelt);
		return this;
	}

	/**
	 * Adds a {@link TimeLeftOverlay} to the GameController, that is initialized
	 * and updated every frame.
	 * 
	 * @return Builder instance with TimeLeftOverlay
	 */
	private GameControllerBuilder addTimeLeftOverlay() {
		TimeLeftOverlay timeLeftOverlay = new TimeLeftOverlay(game.getSkin(), stage);
		timeController.registerIntegerTimeListener(timeLeftOverlay);
		return this;
	}

	/**
	 * Adds a {@link FPSOverlay} to the GameController, that is updated
	 * every frame.
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
	 * Adds a {@link TouchScreenOverlay} to the GameController, that is
	 * updated every frame.
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
	 * initialized, updated every frame and updated when the game is
	 * finished.
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
	 * Adds a {@link IPlane} to the GameController, that is initialized,
	 * updated every frame and updated when the game is finished.
	 * 
	 * @return Builder instance with IPlane
	 */
	private GameControllerBuilder addPlayerPlane() {
		IPlane plane = player.getPlane();
		addFeatureToLists(plane);
		return this;
	}
	
	/**
	 * Adds a {@link CollectibleObjects} to the GameController, that is initialized,
	 * updated every frame and updated when the game is finished.
	 * 
	 * @return Builder instance with CollectibleObjects
	 */
	private GameControllerBuilder addSpeedUpgrade() {
		CollectibleObjects collectibleObjects = new CollectibleObjects("speedUpgrade", "speedUpgrade");
		addFeatureToLists(collectibleObjects);
		CollisionDetector.getInstance().getCollisionContactListener().addListener(collectibleObjects);
		
		return this;
	}

	/**
	 * Creates a new GameController out of your defined preferences in the
	 * other methods before.
	 * 
	 * @return new GameController
	 */
	public GameController build() {
		gc.stage = stage;
		gc.optionalFeaturesToLoad = optionalFeaturesToLoad;
		gc.optionalFeaturesToInit = optionalFeaturesToInit;
		gc.optionalFeaturesToUpdate = optionalFeaturesToUpdate;
		gc.optionalFeaturesToRender = optionalFeaturesToRender;
		gc.optionalFeaturesToFinish = optionalFeaturesToFinish;
		gc.optionalFeaturesToDispose = optionalFeaturesToDispose;
		gc.level = level;
		gc.flightController = flightController;
		gc.cameraController = cameraController;
		gc.batch = new ModelBatch();
		gc.setTimeController(timeController);

		level.addEventListener(new Level.EventAdapter() {
			@Override
			public void onFinished() {
				gc.finishGame();
			}
		});
		return gc;
	}
}