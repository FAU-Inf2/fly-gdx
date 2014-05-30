package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;

/**
 * Displays the 3D-world.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen {
	private final Fly game;
	
	private InputMultiplexer inputProcessor;
	

	public GameScreen(final Fly game) {
		this.game = game;

		inputProcessor = new InputMultiplexer(game.gameController.getCameraController(), new BackProcessor());
	}

	@Override
	public void render(float delta) {
		game.gameController.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		//Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		
		inputProcessor.addProcessor(game.gameController.getStage());
		
		// delegate all inputs to the #inputProcessor
		Gdx.input.setInputProcessor(inputProcessor);
		
		game.gameController.initGame();
		
		game.getPlayer().getLastLevel().initLevel(game.gameController);
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
