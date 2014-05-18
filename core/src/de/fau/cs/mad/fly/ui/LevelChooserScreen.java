package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
	private Table outerTable;
	private List<Label> levelList;
	private ScrollPane levelScrollPane;

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
		inputProcessor.addProcessor(stage);
		
		showLevels();
	}

	/**
	 * Shows a list of all available levels.
	 */
	public void showLevels() {

		Table scrollableTable = new Table(skin);
		
		final TextButton level1 = new TextButton("Level 1", skin, "default");
		final TextButton level2 = new TextButton("Level 2", skin, "default");
		final TextButton level3 = new TextButton("Level 3", skin, "default");
		final TextButton level4 = new TextButton("Level 4", skin, "default");
		
		scrollableTable.add(level1).size(400).pad(10f).uniform().row();
		scrollableTable.add(level2).size(400).pad(10f).uniform().row();
		scrollableTable.add(level3).size(400).pad(10f).uniform().row();
		scrollableTable.add(level4).size(400).pad(10f).uniform().row();
		
		levelScrollPane = new ScrollPane(scrollableTable, skin);
		levelScrollPane.setScrollingDisabled(true, false);
		levelScrollPane.setFillParent(true);
		
		stage.addActor(levelScrollPane);
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
