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
import de.fau.cs.mad.fly.Player;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureGatePassed;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.overlay.FPSOverlay;
import de.fau.cs.mad.fly.features.overlay.LevelInfoOverlay;
import de.fau.cs.mad.fly.features.overlay.SteeringOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeLeftOverlay;
import de.fau.cs.mad.fly.features.overlay.TimeOverlay;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.Level;

public class GameController {
	private Fly game;
	private Player player;
	private Stage stage;
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

	private boolean isRunning;

	public GameController(Builder gameControllerBuilder) {
		this.game = Builder.game;
		this.player = Builder.player;
		this.stage = Builder.stage;
		this.useSensorData = Builder.useSensorData;
		this.optionalFeaturesToLoad = Builder.optionalFeaturesToLoad;
		this.optionalFeaturesToInit = Builder.optionalFeaturesToInit;
		this.optionalFeaturesToRender = Builder.optionalFeaturesToRender;
		this.optionalFeaturesToFinish = Builder.optionalFeaturesToFinish;
		this.optionalFeaturesToDispose = Builder.optionalFeaturesToDispose;
		this.levelProgress = Builder.levelProgress;
		this.level = Builder.level;

		this.camController = game.getCameraController();

		this.batch = new ModelBatch();
	}

	public Stage getStage() {
		return stage;
	}

	/**
	 * Getter of the level Progress.
	 * 
	 * @return {@link #levelProgress}
	 */
	public LevelProgress getLevelProgress() {
		return levelProgress;
	}

	public CameraController getCameraController() {
		return camController;
	}

	public Level getLevel() {
		return level;
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * This method is called, when the level is loaded. It loads everything the
	 * default functions need and calls all the optional feature loading
	 * methods.
	 */
	public void loadGame() {
		player.getPlane().load(this);
		
		for (IFeatureLoad optionalFeature : optionalFeaturesToLoad) {
			optionalFeature.load(this);
		}
	}

	/**
	 * This method is called, when the level is loaded. It initializes all
	 * default functions that are needed in all levels, like render the level.
	 * Furthermore all optional features in {@link #optionalFeaturesToInit} are
	 * initialized.
	 */
	public void initGame() {

		useSensorData = !player.getSettingManager()
				.getCheckBoxValue("useTouch");
		camController.setUseSensorData(useSensorData);

		boolean useRolling = player.getSettingManager().getCheckBoxValue(
				"useRoll");
		camController.setUseRolling(useRolling);

		boolean useLowPass = player.getSettingManager().getCheckBoxValue(
				"useLowPass");
		camController.setUseLowPass(useLowPass);

		camController.setUpCamera();
		camera = camController.getCamera();

		levelProgress.init(this);

		time = 0.0f;
		
		player.getLastLevel().initLevel(this);

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

		// gameOverlay.initOverlay()

		// initializes all optional features
		for (IFeatureInit optionalFeature : optionalFeaturesToInit) {
			optionalFeature.init(this);
		}
		startGame();
	}

	public void startGame() {
		isRunning = true;
	}

	public void stopGame() {
		isRunning = false;

		endGame();
		disposeGame();
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public void render(float delta) {

		if (!isRunning)
			return;

		stage.act(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera = camController.recomputeCamera(delta);

		checkCollision();

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
	 * This method is called, when the Game is over. It calls the .finish()
	 * method of all {@link #optionalFeaturesToFinish}.
	 */
	public void endGame() {
		for (IFeatureFinish optionalFeature : optionalFeaturesToFinish) {
			optionalFeature.finish();
		}
	}

	/**
	 * This method is called, when the GameScreen is left. It disposes
	 * everything the default functions needed and calls all the optional
	 * feature dispose methods.
	 */
	public void disposeGame() {
		for (IFeatureDispose optionalFeature : optionalFeaturesToDispose) {
			optionalFeature.dispose();
		}

		stage.dispose();
		// level.dispose();

		optionalFeaturesToRender.clear();
	}

	/**
	 * Simple version of collision testing.
	 */
	private boolean checkCollision() {
		// TODO: own class? feature?

		for (Gate g : level.gates) {
			if (camera.position.dst(g.transformMatrix[12],
					g.transformMatrix[13], g.transformMatrix[14]) < 2.0f) {
				// System.out.println("GATE: " + g.id);
				// level.gateModels.get(0).materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
				return true;
			}
		}

		return false;
	}

	/**
	 * This class implements the builder patter to create a GameController with
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
		private static ArrayList<IFeatureLoad> optionalFeaturesToLoad;
		private static ArrayList<IFeatureInit> optionalFeaturesToInit;
		private static ArrayList<IFeatureRender> optionalFeaturesToRender;
		private static ArrayList<IFeatureFinish> optionalFeaturesToFinish;
		private static ArrayList<IFeatureDispose> optionalFeaturesToDispose;
		private static ArrayList<IFeatureGatePassed> optionalFeaturesGatePassed;
		private static LevelProgress levelProgress;

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
			Builder.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight()));
			Builder.level = player.getLastLevel();
			
			addCollisionDetector();

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
			if (player.getSettingManager().getCheckBoxValue("showLevelInfo")) {
				//addLevelInfoOverlay();
				// not working yet
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
			SteeringOverlay steeringOverlay = new SteeringOverlay(game, stage);
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
		private Builder addLevelInfoOverlay() {
			LevelInfoOverlay levelInfoOverlay = new LevelInfoOverlay(game,
					stage);
			optionalFeaturesToInit.add(levelInfoOverlay);
			optionalFeaturesToRender.add(levelInfoOverlay);
			optionalFeaturesToFinish.add(levelInfoOverlay);
			return this;
		}

		/**
		 * Adds a {@link CollisionDetector} to the GameController, that is
		 * initialized, checked collision every frame and disposed when the game
		 * is finished.
		 * 
		 * @return Builder instance with collisionDetector
		 */
		private Builder addCollisionDetector() {
			CollisionDetector collisionDetector = new CollisionDetector(game, 
					stage);
			optionalFeaturesToInit.add(collisionDetector);
			optionalFeaturesToRender.add(collisionDetector);
			optionalFeaturesToDispose.add(collisionDetector);
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
