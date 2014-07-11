package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
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
 * Manages the Player, the Level, the UI, the CameraController and all the
 * optional Features and calls the load(), init(), render(), finish() and
 * dispose() methods of those.
 * <p>
 * Optional Feature Interfaces:
 *		load():		- called before the game starts while the loading screen is shown
 *					- should be stuff like loading models, creating instances, which takes a while
 * 		init():		- called the moment the game starts after switching to the game screen
 * 					- should be stuff like setting values, resetting counter
 * 		update(): 	- called every frame while the game is running and not paused
 * 					- should be stuff like calculating and updating values
 * 		render(): 	- called every frame while the game is running or paused, in pause the delta time is 0
 * 					- should be stuff like rendering models, showing overlays
 * 		finish(): 	- called at the moment the game is over, still in game screen
 * 					- should be stuff like showing points, saving the highscore
 * 		dispose(): 	- called when the game screen is left
 * 					- should be stuff like disposing models
 * 
 * @author Lukas Hahmann
 */
public class GameController implements TimeIsUpListener{
	public enum GameState {
		RUNNING, PAUSED, FINISHED
	}

	private Stage stage;
	private List<IFeatureLoad> optionalFeaturesToLoad;
	private List<IFeatureInit> optionalFeaturesToInit;
	private List<IFeatureUpdate> optionalFeaturesToUpdate;
	private List<IFeatureRender> optionalFeaturesToRender;
	private List<IFeatureDispose> optionalFeaturesToDispose;
	private List<IFeatureFinish> optionalFeaturesToFinish;
	private FlightController flightController;
	private PerspectiveCamera camera;
	private ModelBatch batch;
	private Level level;
	private GameState gameState;
	private TimeController timeController;

	/** Use Builder to initiate GameController */
	protected GameController() {
	}

	/**
	 * Getter for the model batch used to draw the 3d game.
	 * 
	 * @return ModelBatch
	 */
	public ModelBatch getBatch() {
		return batch;
	}

	/**
	 * Getter for the stage.
	 * 
	 * @return {@link #stage}
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Getter for the camera controller.
	 * 
	 * @return {@link #flightController}
	 */
	public FlightController getCameraController() {
		return flightController;
	}

	/**
	 * Getter for the camera.
	 * 
	 * @return {@link #camera}
	 */
	public PerspectiveCamera getCamera() {
		return camera;
	}

	/**
	 * Getter for the level.
	 * 
	 * @return {@link #level}
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Setter for the level.
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * This method is called, while the level is loading. It loads everything
	 * the default functions need. Furthermore all optional features in
	 * {@link #optionalFeaturesToLoad} are loaded.
	 */
	public void loadGame() {
		// loads all optional features
		for (IFeatureLoad optionalFeature : optionalFeaturesToLoad) {
			optionalFeature.load(this);
		}
	}

	/**
	 * This method is called, when the level is initialized. It initializes all
	 * default functions that are needed in all levels, like render the level.
	 * Furthermore all optional features in {@link #optionalFeaturesToInit} are
	 * initialized.
	 */
	public void initGame() {
		camera = flightController.getCamera();

		// initializes all optional features
		for (IFeatureInit optionalFeature : optionalFeaturesToInit) {
			optionalFeature.init(this);
		}

		//TODO: number of lifes should not be defined statically
		PlayerManager.getInstance().getCurrentPlayer().setLives(10);
		Debug.setOverlay(0, PlayerManager.getInstance().getCurrentPlayer().getLives());

		startGame();
		Gdx.app.log("GameController.initGame", "OK HAVE FUN!");
	}

	/**
	 * Sets the game state to running.
	 */
	public void startGame() {
		gameState = GameState.RUNNING;
		timeController.initAndStartTimer(level.getLeftTime());
	}

	/**
	 * Sets the game state to paused.
	 */
	public void pauseGame() {
		gameState = GameState.PAUSED;
		timeController.pause();
	}

	/**
	 * Sets the game state to finished and ends the game.
	 */
	public void finishGame() {
		System.out.println("FINISHED");
		gameState = GameState.FINISHED;
		endGame();
	}
	
	/**
	 * Sets the game from paused to running
	 */
	public void resumeGame() {
		gameState = GameState.RUNNING;
		timeController.resume();
	}

	/**
	 * Getter for the game state.
	 * 
	 * @return GameState
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Checks if the game is running.
	 * 
	 * @return true if the game is running, otherwise false.
	 */
	public boolean isRunning() {
		if (gameState == GameState.RUNNING)
			return true;
		return false;
	}

	/**
	 * Checks if the game is paused.
	 * 
	 * @return true if the game is paused, otherwise false.
	 */
	public boolean isPaused() {
		if (gameState == GameState.PAUSED)
			return true;
		return false;
	}

	/**
	 * This method is called every frame. Furthermore all optional features in
	 * {@link #optionalFeaturesToRender} are updated and rendered.
	 * 
	 * @param delta
	 *            Time after the last call.
	 */
	public void renderGame(float delta) {
		stage.act(delta);

		if (gameState == GameState.RUNNING) {
			camera = flightController.recomputeCamera(delta);

			level.update(delta, camera);

			// update optional features if the game is not paused
			for (IFeatureUpdate optionalFeature : optionalFeaturesToUpdate) {
				optionalFeature.update(delta);
			}

			CollisionDetector.getInstance().perform(delta);
			timeController.checkTime();
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin(camera);
		level.render(delta, batch, camera);
		batch.end();
		// TODO: care about begin()/end() from Batch / Stage / ShapeRenderer
		// etc., split render up?

		// render optional features
		for (IFeatureRender optionalFeature : optionalFeaturesToRender) {
			optionalFeature.render(delta);
		}

		stage.draw();
	}

	/**
	 * This method is called when the game is over. Furthermore all optional
	 * features in {@link #optionalFeaturesToFinish} are finished.
	 */
	public void endGame() {
	    pauseGame();
		for (IFeatureFinish optionalFeature : optionalFeaturesToFinish) {
			optionalFeature.finish();
		}
	}

	/**
	 * This method is called when the game is over. Furthermore all optional
	 * features in {@link #optionalFeaturesToDispose} are disposed.
	 */
	public void disposeGame() {
		for (IFeatureDispose optionalFeature : optionalFeaturesToDispose) {
			//Gdx.app.log("GameController.disposeGame", "dispose: " + optionalFeature.getClass().getSimpleName());
			optionalFeature.dispose();
		}
		CollisionDetector.getInstance().dispose();
		optionalFeaturesToUpdate.clear();
		optionalFeaturesToRender.clear();
	}
	
	public void setTimeController(TimeController timeController) {
		this.timeController = timeController;
		timeController.registerTimeIsUpListener(this);

	}
	
	public TimeController getTimeController() {
		return timeController;
	}
	
	@Override
	public void timeIsUp() {
	    pauseGame();
	}
	
	

	/**
	 * This class implements the builder pattern to create a GameController with
	 * all of its dependent components.
	 * 
	 * @author Lukas Hahmann
	 * 
	 */
	public static class Builder {
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
		public Builder init(final Fly game) {
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
			} else {
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
		private Builder addGateIndicator() {
			GateIndicator gateIndicator = new GateIndicator();
			addFeatureToLists(gateIndicator);
			return this;
		}

		/**
		 * Adds a {@link AsteroidBelt} to the GameController, that is loaded,
		 * initialized, updated every frame and disposed.
		 * 
		 * @return Builder instance with GateIndicator
		 */
		private Builder addAsteroidBelt() {
			AsteroidBelt asteroidBelt = new AsteroidBelt(10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
			addFeatureToLists(asteroidBelt);
			return this;
		}

		/**
		 * Adds a {@link TimeOverlay} to the GameController, that is initialized
		 * and updated every frame.
		 * 
		 * @return Builder instance with TimeOverlay
		 */
		private Builder addTimeLeftOverlay() {
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
		private Builder addFPSOverlay() {
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
		private Builder addSteeringOverlay() {
			SteeringOverlay steeringOverlay = new SteeringOverlay(flightController, game.getShapeRenderer(), stage);
			addFeatureToLists(steeringOverlay);
			return this;
		}

		/**
		 * Adds a {@link TouchScreenOverlay} to the GameController, that is
		 * updated every frame.
		 * 
		 * @return Builder instance with TouchScreenOverlay
		 */
		private Builder addTouchScreenOverlay() {
			TouchScreenOverlay touchScreenOverlay = new TouchScreenOverlay(game.getShapeRenderer(), stage, game.getSkin());
			addFeatureToLists(touchScreenOverlay);
			return this;
		}

		/**
		 * Adds a {@link GameFinishedOverlay} to the GameController, that is
		 * initialized, updated every frame and updated when the game is
		 * finished.
		 * 
		 * @return Builder instance with SteeringOverlay
		 */
		private Builder addGameFinishedOverlay() {
			GameFinishedOverlay gameFinishedOverlay = new GameFinishedOverlay(game.getSkin(), stage);
			optionalFeaturesToInit.add(gameFinishedOverlay);
			optionalFeaturesToFinish.add(gameFinishedOverlay);
			return this;
		}

		/**
		 * Adds a {@link PauseGameOverlay} to the GameController, that is
		 * initialized and displayed every frame
		 * 
		 * @return Builder instance with PauseGameOverlay
		 */
		private Builder addPauseGameOverlay() {
			PauseGameOverlay pauseGameOverlay = new PauseGameOverlay(game.getSkin(), stage);
			optionalFeaturesToInit.add(pauseGameOverlay);
			optionalFeaturesToDispose.add(pauseGameOverlay);
			return this;
		}

		/**
		 * Adds a {@link SteeringResetOverlay} to the GameController, that is
		 * initialized and displayed every frame
		 * 
		 * @return Builder instance with SteeringResetOverlay
		 */
		private Builder addSteeringResetOverlay() {
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
		private Builder addPlayerPlane() {
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
		private Builder addSpeedUpgrade() {
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
}
