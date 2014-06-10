package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.GameScreen;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureGatePassed;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.game.GateIndicator;
import de.fau.cs.mad.fly.features.overlay.FPSOverlay;
import de.fau.cs.mad.fly.features.overlay.GameFinishedOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeLeftOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeOverlay;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.res.Level;

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
	private Player player;
	private Stage stage;
	private CollisionDetector collisionDetector;
	private ArrayList<IFeatureLoad> optionalFeaturesToLoad;
	private ArrayList<IFeatureInit> optionalFeaturesToInit;
	private ArrayList<IFeatureRender> optionalFeaturesToRender;
	private ArrayList<IFeatureDispose> optionalFeaturesToDispose;
	private ArrayList<IFeatureFinish> optionalFeaturesToFinish;
	private CameraController camController;
	private boolean useSensorData;
	PerspectiveCamera camera;
	private LevelProgress levelProgress;

	public Environment environment;
	public ModelBatch batch;

	private Level level;

	private float time;

	private GameState gameState;

	public GameController(Builder gameControllerBuilder) {
		this.game = Builder.game;
		this.player = Builder.player;
		this.stage = Builder.stage;
		this.collisionDetector = Builder.collisionDetector;
		this.useSensorData = Builder.useSensorData;
		this.optionalFeaturesToLoad = Builder.optionalFeaturesToLoad;
		this.optionalFeaturesToInit = Builder.optionalFeaturesToInit;
		this.optionalFeaturesToRender = Builder.optionalFeaturesToRender;
		this.optionalFeaturesToFinish = Builder.optionalFeaturesToFinish;
		this.optionalFeaturesToDispose = Builder.optionalFeaturesToDispose;
		this.levelProgress = Builder.levelProgress;
		this.level = Builder.level;

		this.camController = Builder.cameraController;

		this.batch = new ModelBatch();
	}
	
	/**
	 * Getter for the game.
	 * 
	 * @return {@link #fly}
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
	 * Getter for the level Progress.
	 * 
	 * @return {@link #levelProgress}
	 */
	public LevelProgress getLevelProgress() {
		return levelProgress;
	}

	/**
	 * Getter for the camera controller.
	 * 
	 * @return {@link #camController}
	 */
	public CameraController getCameraController() {
		return camController;
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
		collisionDetector.load(this);
		
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

		// TODO: put in cameraController.init()
		useSensorData = !player.getSettingManager()
				.getCheckBoxValue("useTouch");
		camController.setUseSensorData(useSensorData);

		boolean useRolling = player.getSettingManager().getCheckBoxValue(
				"useRoll");
		camController.setUseRolling(useRolling);

		boolean useLowPass = player.getSettingManager().getCheckBoxValue(
				"useLowPass");
		camController.setUseLowPass(useLowPass);
		
		boolean useAveraging = player.getSettingManager().getCheckBoxValue("useAveraging");
		camController.setUseAveraging(useAveraging);
		
		int buffersize = (int) player.getSettingManager().getSliderValue("bufferSlider");
		camController.setBufferSize(buffersize);
		
		float alpha = player.getSettingManager().getSliderValue("alphaSlider") / 100.f;
		camController.setAlpha(alpha);

		camController.setUpCamera();
		camera = camController.getCamera();

		player.getLastLevel().initLevel(this);
		levelProgress.init(this);

		// initializes all optional features
		for (IFeatureInit optionalFeature : optionalFeaturesToInit) {
			optionalFeature.init(this);
		}
		

		time = 0.0f;
		
		startGame();
	}

	public void startGame() {
		gameState = GameState.RUNNING;
	}
	
	public void pauseGame() {
		gameState = GameState.PAUSED;
	}

	public void finishGame() {
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

		camera = camController.recomputeCamera(delta);

		collisionDetector.perform();

		batch.begin(camera);
		level.render(camera);
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
		private static Fly game;
		private static Player player;
		private static Stage stage;
		private static Level level;
		private static boolean useSensorData;
		private static CollisionDetector collisionDetector;
		private static ArrayList<IFeatureLoad> optionalFeaturesToLoad;
		private static ArrayList<IFeatureInit> optionalFeaturesToInit;
		private static ArrayList<IFeatureRender> optionalFeaturesToRender;
		private static ArrayList<IFeatureFinish> optionalFeaturesToFinish;
		private static ArrayList<IFeatureDispose> optionalFeaturesToDispose;
		private static ArrayList<IFeatureGatePassed> optionalFeaturesGatePassed;
		private static LevelProgress levelProgress;
		private static CameraController cameraController;

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
			optionalFeaturesGatePassed = new ArrayList<IFeatureGatePassed>();
			levelProgress = new LevelProgress();

			Builder.game = game;
			Builder.player = game.getPlayer();
			Builder.cameraController = new CameraController(true, player);
			Builder.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight()));
			Builder.level = player.getLastLevel();
			
			addPlayerPlane();
			collisionDetector = new CollisionDetector(game);
			
			collisionDetector.getCollisionContactListener().addListener(levelProgress);

			if (player.getSettingManager()
					.getCheckBoxValue("showGateIndicator")) {
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
			if (player.getSettingManager().getCheckBoxValue("showGameFinished")) {
				addGameFinishedOverlay();
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
			optionalFeaturesGatePassed.add(gateIndicator);
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
			SteeringOverlay steeringOverlay = new SteeringOverlay(cameraController, game.getShapeRenderer(), stage);
			optionalFeaturesToRender.add(steeringOverlay);
			optionalFeaturesToDispose.add(steeringOverlay);
			return this;
		}

		/**
		 * Adds a {@link LevelInfoOverlay} to the GameController, that is
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
			return new GameController(this);
		}
	}
}
