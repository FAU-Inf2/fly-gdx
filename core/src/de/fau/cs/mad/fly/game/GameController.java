package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.res.Level;

//Lukas: we probably rename this class, to avoid confusion with com.badlogic.gdx.Game, suggestion: GameController
public class GameController {
	private Fly game;
	// private GameOverlay gameOverlay; will be added as an optional feature
	private ArrayList<IFeatureInit> optionalFeaturesToInit;
	private ArrayList<IFeatureRender> optionalFeaturesToRender;
	private CameraController camController;
	private boolean useSensorData;
	PerspectiveCamera camera;

	private Level level;

	private boolean isRunning;

	public GameController(Fly game) {
		this.game = game;	
		
		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
		
		camController = new CameraController(useSensorData, game);
		optionalFeaturesToInit = new ArrayList<IFeatureInit>();
		optionalFeaturesToRender = new ArrayList<IFeatureRender>();
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
	 * If an optional Feature needs to be initialized, it has to register with
	 * this Method.
	 * 
	 * @param optionalFeature
	 *            that has to be initialized at startup of the Level.
	 */
	public void registerToInitGame(IFeatureInit optionalFeature) {
		optionalFeaturesToInit.add(optionalFeature);
	}

	/**
	 * This method is called, when the level is loaded. It initializes all
	 * default functions that are needed in all levels, like render the level.
	 * Furthermore all optional features in {@link #optionalFeaturesToInit} are
	 * initialized.
	 */
	public void initGame() {
		
		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
		camController.setUseSensorData(useSensorData);
		
		boolean useRolling = game.getSettingManager().getCheckBoxValue("useRoll");
		camController.setUseRolling(useRolling);
		
		camController.setUpCamera();
		camera = camController.getCamera();
		
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

	
	/**
	 * If an optional Feature needs to be rendered, it has to register with
	 * this Method.
	 * 
	 * @param optionalFeature
	 *            that has to be rendered in each frame.
	 */
	public void registerToRender(IFeatureRender optionalFeature) {
		optionalFeaturesToRender.add(optionalFeature);
	}
	
	public void render(float delta) {
		if (!isRunning)
			return;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		camera = camController.recomputeCamera(delta);

		game.getPlayer().getLastLevel().render(camera);
		
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

}
