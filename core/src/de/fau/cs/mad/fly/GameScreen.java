package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.fau.cs.mad.fly.game.CameraController;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Displays the 3D-world.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen{
	private final Fly game;
	
	CameraController camController;

	private PerspectiveCamera camera;
	private boolean useSensorData;
	
	private GameOverlay gameOverlay;
	
	private InputMultiplexer inputProcessor;
	GameController gameController;

	public GameScreen(final Fly game) {
		this.game = game;
		
		gameOverlay = new GameOverlay(game);

		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
		
		camController = new CameraController(useSensorData, game);
		
		camera = camController.getCamera();
		
		gameController = new GameController(this.game, camController);
		inputProcessor = new InputMultiplexer();
		
		// create an InputProcess to handle the back key
		InputProcessor backProcessor = new BackProcessor();
		inputProcessor.addProcessor(camController);
		inputProcessor.addProcessor(backProcessor);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		
		camera = camController.recomputeCamera(delta);

		game.getPlayer().getLastLevel().render(camera);
		
		gameOverlay.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		//Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		
		// delegate all inputs to the #inputProcessor
		Gdx.input.setInputProcessor(inputProcessor);
		
		useSensorData = !game.getSettingManager().getCheckBoxValue("useTouch");
		camController.setUseSensorData(useSensorData);
		
		boolean useRolling = game.getSettingManager().getCheckBoxValue("useRoll");
		camController.setUseRolling(useRolling);
		
		camController.setUpCamera();
		camera = camController.getCamera();
		
		game.getPlayer().getLastLevel().initLevel();
		
		gameOverlay.initOverlay();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}
}
