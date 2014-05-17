package de.fau.cs.mad.fly;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Offers a selections of Levels to start
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen implements Screen {
	private final Fly game;

	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;
	
	/**
	 * Processes all the input within the {@link #LevelChooserScreen(Fly)}. the
	 * multiplexer offers the possibility to add several InputProcessors
	 */
	private InputMultiplexer inputProcessor;

	public LevelChooserScreen(final Fly game) {
		this.game = game;

		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		if (Gdx.app.getType() == ApplicationType.Android) {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / 2f,
					Gdx.graphics.getHeight() / 2f));
		} else {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight()));
		}
		inputProcessor = new InputMultiplexer();
		// create an InputProcess to handle the back key
		InputProcessor backProcessor = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if ((keycode == Keys.ESCAPE) || (keycode == Keys.BACK)) {
					game.setMainMenuScreen();
				}
				return false;
			}
		};
		inputProcessor.addProcessor(backProcessor);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		// allow this screen to catch the back key
		Gdx.input.setCatchBackKey(true);
		// delegate all inputs to the #inputProcessor
		Gdx.input.setInputProcessor(inputProcessor);
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
		batch.dispose();
	}
}
