package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

/**
 * Displays the 3D-world.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen{
	private final Fly game;
	
	private GameOverlay gameOverlay;
	
	private InputMultiplexer inputProcessor;
	

	public GameScreen(final Fly game) {
		this.game = game;
		
		gameOverlay = new GameOverlay(game);

		inputProcessor = new InputMultiplexer();
		
		// create an InputProcess to handle the back key
		InputProcessor backProcessor = new BackProcessor();
		inputProcessor.addProcessor(game.gameController.getCameraController());
		inputProcessor.addProcessor(backProcessor);
	}

	@Override
	public void render(float delta) {
		game.gameController.render(delta);		
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
		
		game.gameController.initGame();
		
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
