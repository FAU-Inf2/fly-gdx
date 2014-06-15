package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.fau.cs.mad.fly.Debug;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.*;
import de.fau.cs.mad.fly.features.game.GateIndicator;
import de.fau.cs.mad.fly.features.overlay.*;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.res.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Player, the Level, the UI, the CameraController and all the optional Features
 * and calls the load(), init(), render(), finish() and dispose() methods of those.
 * <p>
 * Optional Feature Interfaces:
 * 		load(): 	- called before the game starts while the loading screen is shown
 * 					- should be stuff like loading models, creating instances, which takes a while
 * 		init():		- called the moment the game starts after switchting to the game screen
 * 					- should be stuff like setting values, resetting counter
 * 		render():	- called every frame while the game is running or paused, in pause the delta time is 0
 * 					- should be stuff like rendering models, showing overlays, calculating and updating values
 * 		finish():	- called at the moment the game is over, still in game screen
 * 					- should be stuff like showing points, saving the highscore
 * 		dispose():	- called when the game screen is left
 * 					- should be stuff like disposing models
 * 
 * @author Lukas Hahmann
 */
public class GameController {
	public enum GameState {
	    RUNNING, PAUSED, FINISHED
	}
	
	private Fly game;
	private Stage stage;
	private CollisionDetector collisionDetector;
	private List<IFeatureLoad> optionalFeaturesToLoad;
	private List<IFeatureInit> optionalFeaturesToInit;
	private List<IFeatureRender> optionalFeaturesToRender;
	private List<IFeatureDispose> optionalFeaturesToDispose;
	private List<IFeatureFinish> optionalFeaturesToFinish;
	private FlightController flightController;
	PerspectiveCamera camera;

	private ModelBatch batch;

	public ModelBatch getBatch() {
		return batch;
	}

	private Level level;

	private float time;

	private GameState gameState;

	public GameController() {}
	
	/**
	 * Getter for the game.
	 * 
	 * @return {@link #game}
	 */
	public Fly getGame() {
		return game;
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
	 * Getter for the collision detector.
	 * 
	 * @return {@link #collisionDetector}
	 */
	public CollisionDetector getCollisionDetector() {
		return collisionDetector;
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
	 * This method is called, while the level is loading. It loads everything the
	 * default functions need.
	 * Furthermore all optional features in {@link #optionalFeaturesToLoad} are
	 * loaded.
	 */
	public void loadGame() {
		// currently an optional feature
		//player.getPlane().load(this);
		
		// level = new Level("Level XYZ");
		// Level-Constructor includes:
		// load level from file
		// load models, textures, ... needed for this level from files
		// create 3D objects for the level
		// stores List of Rings, Lists of other stuff in the level

		// player = new Player();
		// Player-Constructor includes:
		// mix/connect with camera controller ?
		// create 3D objects for the player
		// stores position and other attributes of player
		
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

		time = 0.0f;

		startGame();
		Gdx.app.log("GameController.initGame", "OK HAVE FUN!");
	}

	public void startGame() {
		gameState = GameState.RUNNING;
	}
	
	public void pauseGame() {
		gameState = GameState.PAUSED;
	}

	public void finishGame() {
		System.out.println("FINISHED");
		gameState = GameState.FINISHED;

		endGame();
	}

	public void setGameState(GameState state) {
		gameState = state;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * This method is called every frame.
	 * Furthermore all optional features in {@link #optionalFeaturesToRender} are
	 * updated and rendered.
	 */
	public void renderGame(float delta) {

		//if (gameState != GameState.RUNNING)
		//	return;

		stage.act(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera = flightController.recomputeCamera(delta);

		collisionDetector.perform();

		batch.begin(camera);
		level.render(delta, batch, camera);
		batch.end();
		// TODO: care about begin()/end() from Batch / Stage / ShapeRenderer
		// etc., split render up?

		// render optional features, for example game overlay
		for (IFeatureRender optionalFeature : optionalFeaturesToRender) {
			optionalFeature.render(delta);
		}
		
		stage.draw();

		time += delta;
	}

	/**
	 * This method is called when the game is over.
	 * Furthermore all optional features in {@link #optionalFeaturesToFinish} are
	 * finished.
	 */
	public void endGame() {
		for (IFeatureFinish optionalFeature : optionalFeaturesToFinish) {
			optionalFeature.finish();
		}
	}

	/**
	 * This method is called when the game is over.
	 * Furthermore all optional features in {@link #optionalFeaturesToDispose} are
	 * disposed.
	 */
	public void disposeGame() {
		for (IFeatureDispose optionalFeature : optionalFeaturesToDispose) {
			optionalFeature.dispose();
		}
		
		collisionDetector.dispose();

		stage.dispose();
		// level.dispose();

		optionalFeaturesToRender.clear();
	}

	/**
	 * This class implements the builder pattern to create a GameController with
	 * all of its dependent components.
	 * 
	 * @author Lukas Hahmann
	 * 
	 */
	public static class Builder {
		private Fly game;
		private Player player;
		private Stage stage;
		private Level level;
		private CollisionDetector collisionDetector;
		private ArrayList<IFeatureLoad> optionalFeaturesToLoad;
		private ArrayList<IFeatureInit> optionalFeaturesToInit;
		private ArrayList<IFeatureRender> optionalFeaturesToRender;
		private ArrayList<IFeatureFinish> optionalFeaturesToFinish;
		private ArrayList<IFeatureDispose> optionalFeaturesToDispose;
		private FlightController flightController;

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
		public Builder init(Fly game) {
			// clear everything in the builder from a possible earlier call
			optionalFeaturesToLoad = new ArrayList<IFeatureLoad>();
			optionalFeaturesToInit = new ArrayList<IFeatureInit>();
			optionalFeaturesToRender = new ArrayList<IFeatureRender>();
			optionalFeaturesToFinish = new ArrayList<IFeatureFinish>();
			optionalFeaturesToDispose = new ArrayList<IFeatureDispose>();

			this.game = game;
			this.player = game.getPlayer();
			this.flightController = new FlightController(player);
			this.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
			this.level = player.getLastLevel();
			optionalFeaturesToLoad.add(level);

			addPlayerPlane();
			collisionDetector = new CollisionDetector();

			collisionDetector.getCollisionContactListener().addListener(level);
			collisionDetector.getCollisionContactListener().addListener(new ICollisionListener<Spaceship, GameObject>() {
				@Override
				public void onCollision(Spaceship ship, GameObject g) {
					Gdx.input.vibrate(500);
					Debug.setOverlay(0, "DEAD!");
				}
			});

			Gdx.app.log("Builder.init", "Setting up collision for level gates...");

			for ( Level.Gate g : level.allGates() ) {
				if ( g.display.getCollisionObject() == null ) {
					Gdx.app.log("Builder.init", "Display CollisionObject == null");
					btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(g.display.modelId, g.display);
					g.display.filterGroup = CollisionDetector.DUMMY_FLAG;
					g.display.filterMask = CollisionDetector.ALL_FLAG;
					g.display.setCollisionObject(displayShape);
				}
				collisionDetector.addCollisionObject(g.display);
				
				if ( g.goal.getCollisionObject() == null ) {
					Gdx.app.log("Builder.init", "Goal CollisionObject == null");
					btCollisionShape goalShape = collisionDetector.getShapeManager().createBoxShape(g.goal.modelId + ".goal", new Vector3(1.0f, 0.05f, 1.0f));
					g.goal.hide();
					g.goal.userData = g;
					g.goal.filterGroup = CollisionDetector.DUMMY_FLAG;
					g.goal.filterMask = CollisionDetector.PLAYER_FLAG;
					g.goal.setCollisionObject(goalShape);
				}
				collisionDetector.addCollisionObject(g.goal);
			}

			Gdx.app.log("Builder.init", "Registering EventListeners for level.");

			level.addEventListener(new Level.EventAdapter() {
				@Override
				public void onGatePassed(Level.Gate passed, Iterable<Level.Gate> current) {
					for (Level.Gate g : level.allGates())
						g.unmark();
					for (Level.Gate g : passed.successors)
						g.mark();
				}

				@Override
				public void onRender() {
					for (Level.Gate g : level.allGates())
						g.display.transform.rotate(new Vector3(0f, 0f, 1f), 0.5f);
				}
			});

			Gdx.app.log("Builder.init", "Final work for level done.");
			
			Debug.init(game, stage, 1);
			Debug.setOverlay(0, "Alive");

			if (player.getSettingManager().getCheckBoxValue("showGateIndicator")) {
				addGateIndicator();
			}
			if (player.getSettingManager().getCheckBoxValue("showTime")) {
				//addTimeOverlay();
				addTimeLeftOverlay(60);
			}
			if (player.getSettingManager().getCheckBoxValue("showFPS")) {
				addFPSOverlay();
			}
			if (player.getSettingManager().getCheckBoxValue("showSteering")) {
				addSteeringOverlay();
			}
			if (player.getSettingManager().getCheckBoxValue("useTouch")) {
				addTouchScreenOverlay();
			}
			if (player.getSettingManager().getCheckBoxValue("showGameFinished")) {
				addGameFinishedOverlay();
			}
			if (!player.getSettingManager().getCheckBoxValue("useTouch")) {
				addSteeringResetOverlay();
			}

			return this;
		}

		/**
		 * Adds a {@link GateIndicator} to the GameController, that is
		 * initialized, updated every frame and updated, when a gate is passed.
		 * 
		 * @return Builder instance with GateIndicator
		 */
		private Builder addGateIndicator() {
			GateIndicator gateIndicator = new GateIndicator();
			optionalFeaturesToInit.add(gateIndicator);
			optionalFeaturesToRender.add(gateIndicator);
			optionalFeaturesToDispose.add(gateIndicator);
			return this;
		}

		/**
		 * Adds a {@link TimeOverlay} to the GameController, that is initialized
		 * and updated every frame.
		 * 
		 * @return Builder instance with TimeOverlay
		 */
		private Builder addTimeOverlay() {
			TimeOverlay timeOverlay = new TimeOverlay(game, stage);
			optionalFeaturesToInit.add(timeOverlay);
			optionalFeaturesToRender.add(timeOverlay);
			return this;
		}
		
		/**
		 * Adds a {@link TimeOverlay} to the GameController, that is initialized
		 * and updated every frame.
		 * 
		 * @return Builder instance with TimeOverlay
		 */
		private Builder addTimeLeftOverlay(float time) {
			TimeLeftOverlay timeLeftOverlay = new TimeLeftOverlay(game, stage, time);
			optionalFeaturesToInit.add(timeLeftOverlay);
			optionalFeaturesToRender.add(timeLeftOverlay);
			return this;
		}

		/**
		 * Adds a {@link FPSOverlay} to the GameController, that is updated
		 * every frame.
		 * 
		 * @return Builder instance with FPSOverlay
		 */
		private Builder addFPSOverlay() {
			FPSOverlay fpsOverlay = new FPSOverlay(game, stage);
			optionalFeaturesToRender.add(fpsOverlay);
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
			optionalFeaturesToRender.add(steeringOverlay);
			optionalFeaturesToDispose.add(steeringOverlay);
			return this;
		}
		
		/**
		 * Adds a {@link TouchScreenOverlay} to the GameController, that is updated
		 * every frame.
		 * 
		 * @return Builder instance with TouchScreenOverlay
		 */
		private Builder addTouchScreenOverlay() {
			TouchScreenOverlay touchScreenOverlay = new TouchScreenOverlay(flightController, game.getShapeRenderer(), stage);
			optionalFeaturesToRender.add(touchScreenOverlay);
			optionalFeaturesToDispose.add(touchScreenOverlay);
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
			GameFinishedOverlay gameFinishedOverlay = new GameFinishedOverlay(game,
					stage);
			optionalFeaturesToInit.add(gameFinishedOverlay);
			optionalFeaturesToRender.add(gameFinishedOverlay);
			optionalFeaturesToFinish.add(gameFinishedOverlay);
			return this;
		}

		/**
		 * Adds a {@link GameFinishedOverlay} to the GameController, that is
		 * initialized, updated every frame and updated when the game is
		 * finished.
		 * 
		 * @return Builder instance with SteeringOverlay
		 */
		private Builder addSteeringResetOverlay() {
			SteeringResetOverlay steeringResetOverlay = new SteeringResetOverlay(game, flightController, stage);
			optionalFeaturesToInit.add(steeringResetOverlay);
			optionalFeaturesToRender.add(steeringResetOverlay);
			optionalFeaturesToDispose.add(steeringResetOverlay);
			return this;
		}
		
		/**
		 * Adds a {@link IPlane} to the GameController, that is
		 * initialized, updated every frame and updated when the game is
		 * finished.
		 * 
		 * @return Builder instance with collisionDetector
		 */
		private Builder addPlayerPlane() {
			IPlane plane = player.getPlane();			
			optionalFeaturesToLoad.add(plane);
			optionalFeaturesToRender.add(plane);
			optionalFeaturesToDispose.add(plane);
			return this;
		}

		/**
		 * Creates a new GameController out of your defined preferences in the
		 * other methods before.
		 * 
		 * @return new GameController
		 */
		public GameController build() {
			final GameController gc = new GameController();
			gc.game = game;
			gc.stage = stage;
			gc.collisionDetector = collisionDetector;
			gc.optionalFeaturesToLoad = optionalFeaturesToLoad;
			gc.optionalFeaturesToInit = optionalFeaturesToInit;
			gc.optionalFeaturesToRender = optionalFeaturesToRender;
			gc.optionalFeaturesToFinish = optionalFeaturesToFinish;
			gc.optionalFeaturesToDispose = optionalFeaturesToDispose;
			gc.level = level;
			gc.flightController = flightController;
			gc.batch = new ModelBatch();

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
