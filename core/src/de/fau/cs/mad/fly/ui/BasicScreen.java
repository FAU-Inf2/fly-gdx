package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.fau.cs.mad.fly.Fly;

public abstract class BasicScreen implements Screen {
	
	protected final Skin skin;
	protected final Stage stage;

	/**
	 * Processes all the input within the {@link #LevelChooserScreen(Fly)}. the
	 * multiplexer offers the possibility to add several InputProcessors
	 */
	protected InputMultiplexer inputProcessor;
	
	public BasicScreen() {
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		inputProcessor = new InputMultiplexer(new BackProcessor(), stage);

		generateContent();
	}

	/** You have to overwrite this method to create your custom content */
	abstract protected void generateContent();
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(UI.Window.BACKGROUND_COLOR.r, UI.Window.BACKGROUND_COLOR.g, UI.Window.BACKGROUND_COLOR.b, UI.Window.BACKGROUND_COLOR.a);
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
		// everything that implements the interface Disposable should be
		// disposed, because Java garbage collections does not care about such
		// objects
	}

}
