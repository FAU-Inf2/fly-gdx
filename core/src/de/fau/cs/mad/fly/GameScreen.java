package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;

import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.ui.BackProcessor;

/**
 * Provides a screen for the game itself.
 * 
 * @author Tobias Zangl
 */
public class GameScreen implements Screen {
	private final Fly game;
	
	private InputMultiplexer inputProcessor;

	public GameScreen(final Fly game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		game.getGameController().renderGame(delta);
	}

	@Override
	public void resize(int width, int height) {
		game.getGameController().getStage().getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		// delegate all inputs to the #inputProcessor
		// TODO: put stage in GameScreen and new InputMultiplexer back to the constructor
		Gdx.app.log("GameScreen.show", "Setting up Multiplexer!");
		inputProcessor = new InputMultiplexer(game.getGameController().getStage(), game.getGameController().getCameraController(), new BackProcessor());
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputProcessor);

		Gdx.app.log("GameScreen.show", "Now, init your game!");
		game.getGameController().initGame();
	}

	@Override
	public void hide() {
		Gdx.app.log("GameScreen.hide", "hide game screen");
		game.getGameController().disposeGame();

		String levelPath = PlayerManager.getInstance().getCurrentPlayer().getLastLevel().file.path();
		Gdx.app.log("Gamescreen.hide", "dispose level: " + levelPath);
		Assets.unload(levelPath);
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
		Gdx.app.log("GameScreen.dispose", "dispose game screen");
		Assets.dispose();
		//game.getGameController().disposeGame();
	}
}
