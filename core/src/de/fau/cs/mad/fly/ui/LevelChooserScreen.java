package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.BackProcessor;
import de.fau.cs.mad.fly.Fly;

/**
 * Offers a selections of Levels to start
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen implements Screen {

	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;

	/**
	 * Processes all the input within the {@link #LevelChooserScreen(Fly)}. the
	 * multiplexer offers the possibility to add several InputProcessors
	 */
	private InputMultiplexer inputProcessor;

	public LevelChooserScreen() {

		batch = new SpriteBatch();
		skin = ((Fly)Gdx.app.getApplicationListener()).getSkin();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()));
		inputProcessor = new InputMultiplexer();
		// create an InputProcess to handle the back key
		InputProcessor backProcessor = new BackProcessor(((Fly)Gdx.app.getApplicationListener()));
		inputProcessor.addProcessor(backProcessor);

		showLevels();
	}

	/**
	 * Shows a list of all available levels.
	 */
	public void showLevels() {
		table = new Table();
		// table.debug();
		// access the current game
		((Fly)Gdx.app.getApplicationListener()).getLevel();
		
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);

		for (int i = 0; i < 2; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final TextField textField = new TextField("...", skin);

			table.row().expand();
			table.add(label).pad(2f);
			table.add(textField).pad(2f);
		}

		for (int i = 2; i < 4; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final SelectBox<String> selectBox = new SelectBox<String>(skin);
			selectBox.setItems(new String[] { "Item 1", "Item 2", "Item 3" });

			table.row().expand();
			table.add(label).pad(2f);
			table.add(selectBox).pad(2f);
		}

		for (int i = 4; i < 20; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final CheckBox checkBox = new CheckBox("", skin);

			table.row().expand();
			table.add(label).pad(2f);
			table.add(checkBox).fill().pad(2f);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
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
		batch.dispose();
		skin.dispose();
		stage.dispose();
	}
}
