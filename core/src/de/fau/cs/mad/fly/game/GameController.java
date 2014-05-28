package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

import de.fau.cs.mad.fly.Player;
import de.fau.cs.mad.fly.res.Level;

/**
 * Controller that handles all components that are related to the 3D game
 * itself. You can create an instance of {@link GameController} by the
 * {@link Builder}.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GameController {
	private Player player;
	// private GameOverlay gameOverlay; will be added as an optional feature
	private ArrayList<IFeatureInit> optionalFeaturesToInit;
	private ArrayList<IRenderableFeature> optionalFeaturesToRender;
	private CameraController camController;
	private boolean useSensorData;
	PerspectiveCamera camera;
	private LevelProgress levelProgress;

	private Level level;

	private boolean isRunning;
	
	private Environment environment;

	private ModelBatch batch;

	/**
	 * You can only create an instance of GameController by the Builder.
	 * 
	 * @param gameControllerBuilder
	 */
	private GameController(Builder gameControllerBuilder) {
		setUpEnvironment();
		batch = new ModelBatch();
		
		this.player = Builder.player;
		this.useSensorData = Builder.useSensorData;
		this.camController = Builder.cameraController;
		this.optionalFeaturesToInit = Builder.optionalFeaturesToInit;
		this.optionalFeaturesToRender = Builder.optionalFeaturesToRender;
		this.levelProgress = Builder.levelProgress;
		this.level = Builder.level;
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

		camController.setUpCamera();
		camera = camController.getCamera();

		levelProgress.init(this);

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

		// level.dispose();
		// player.dispose();
		// ...
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public void render(float delta) {
		if (!isRunning)
			return;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera = camController.recomputeCamera(delta);

		

		// check if game is finished
		// stopGame();

		// fetch input from camera controller

		// calculate new positions, camera etc.

		// do collision stuff, level internal and with player
		// level.checkCollision(player);

		// render level (static + dynamic -> split render method?)
		// level.render();

		// render player
		// player.render();

		// update time, points, fuel, whatever.. (here, in level or in player
		// class?)
		batch.begin(camera);
		player.getLastLevel().render(environment, camera, batch, delta);
		
		// render optional features, for example game overlay
		for (IRenderableFeature optionalFeature : optionalFeaturesToRender) {
			optionalFeature.render(batch, environment, delta);
		}
		batch.end();
	}

	public void endGame() {

	}

	/**
	 * This class implements the builder patter to create a GameController with
	 * all of its dependent components.
	 * 
	 * @author Lukas Hahmann
	 * 
	 */
	public static class Builder {

		private static Player player;
		private static Level level;
		private static boolean useSensorData;
		private static CameraController cameraController;
		private static ArrayList<IFeatureInit> optionalFeaturesToInit = new ArrayList<IFeatureInit>();
		private static ArrayList<IRenderableFeature> optionalFeaturesToRender = new ArrayList<IRenderableFeature>();
		private static ArrayList<IFeatureGatePassed> optionalFeaturesGatePassed = new ArrayList<IFeatureGatePassed>();
		private static LevelProgress levelProgress = new LevelProgress();

		/**
		 * Creates a basic {@link GameController} with a certain player, its
		 * last level and the selected level of the player.
		 * 
		 * @param player
		 *            needed to get the current settings and the level
		 * @return new GameController with the current selected level and the
		 *         selected settings
		 */
		public Builder basicGameController(Player player) {
			Builder.player = player;
			Builder.level = player.getLastLevel();
			useSensorData = !player.getSettingManager().getCheckBoxValue(
					"useTouch");
			Builder.cameraController = new CameraController(useSensorData,
					player);
			return this;
		}

		/**
		 * Adds a {@link GateIndicator} to the GameController, that is
		 * initialized, updated every frame and updated, when a gate is passed.
		 * 
		 * @return Builder instance with GateIndicator
		 */
		public Builder addGateIndicator() {
			GateIndicator gateIndicator = new GateIndicator();
			optionalFeaturesToInit.add(gateIndicator);
			optionalFeaturesToRender.add(gateIndicator);
			optionalFeaturesGatePassed.add(gateIndicator);
			return this;
		}

		/**
		 * Creates a new GameController instance out of your defined preferences
		 * in the other methods before.
		 * 
		 * @return new GameController
		 */
		public GameController build() {
			return new GameController(this);
		}
	}
	
	/**
	 * Sets up the environment for the level with its light.
	 */
	private void setUpEnvironment() {
		// setting up the environment
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));
	}
}
