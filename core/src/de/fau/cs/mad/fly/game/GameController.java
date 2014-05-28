package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.fau.cs.mad.fly.Player;
import de.fau.cs.mad.fly.res.Level;

public class GameController {
	private Player player;
	// private GameOverlay gameOverlay; will be added as an optional feature
	private ArrayList<IFeatureInit> optionalFeaturesToInit;
	private ArrayList<IFeatureRender> optionalFeaturesToRender;
	private CameraController camController;
	private boolean useSensorData;
	PerspectiveCamera camera;
	private LevelProgress levelProgress;

	private Level level;

	private boolean isRunning;

	public GameController(Builder gameControllerBuilder) {
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

		player.getLastLevel().render(camera);

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

		// render optional features, for example game overlay
		for (IFeatureRender optionalFeature : optionalFeaturesToRender) {
			optionalFeature.render(this, delta);
		}
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
		private static ArrayList<IFeatureRender> optionalFeaturesToRender = new ArrayList<IFeatureRender>();
		private static ArrayList<IFeatureGatePassed> optionalFeaturesGatePassed = new ArrayList<IFeatureGatePassed>();
		private static LevelProgress levelProgress = new LevelProgress();

		/**
		 * Creates a basic {@link GameController} with a certain level, linked
		 * to the current player, its settings and the selected level.
		 * 
		 * @param player
		 *            needed to get the current settings and the level
		 * @return new GameController with the current selected level and the
		 *         selected settings
		 */
		public Builder setPlayer(Player player) {
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
