package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.fau.cs.mad.fly.Fly;

public abstract class BasicScreen implements Screen {
	
	protected final Skin skin;
	protected final Stage stage;
	protected final Viewport viewport;

	/**
	 * Processes all the input within the {@link #LevelChooserScreen(Fly)}. the
	 * multiplexer offers the possibility to add several InputProcessors
	 */
	protected InputMultiplexer inputProcessor;
	
	public BasicScreen() {
		stage = new Stage();
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();
		float widthScalingFactor = UI.Window.REFERENCE_WIDTH / (float)Gdx.graphics.getWidth();
		float heightScalingFactor = UI.Window.REFERENCE_HEIGHT / (float)Gdx.graphics.getHeight();
		float scalingFactor = Math.max(widthScalingFactor, heightScalingFactor);
		viewport = new FillViewport(Gdx.graphics.getWidth()*scalingFactor, Gdx.graphics.getHeight()*scalingFactor, stage.getCamera());
		stage.setViewport(viewport);
		inputProcessor = new InputMultiplexer(new BackProcessor(), stage);
		generateContent();
	}

	/** You have to overwrite this method to create your custom content */
	abstract protected void generateContent();
	
	@Override
	public void render(float delta) {
		Color backgroundColor = skin.getColor(UI.Window.BACKGROUND_COLOR);
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		Table.drawDebug(stage);
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
		Gdx.app.log("BasicScreen", "dispose screen");
		// everything that implements the interface Disposable should be
		// disposed, because Java garbage collections does not care about such
		// objects

		stage.dispose();
	}

}
