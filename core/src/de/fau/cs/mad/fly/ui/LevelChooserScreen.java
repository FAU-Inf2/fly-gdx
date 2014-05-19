package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Value;

import de.fau.cs.mad.fly.BackProcessor;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.res.Level;

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
		skin = ((Fly) Gdx.app.getApplicationListener()).getSkin();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()));
		inputProcessor = new InputMultiplexer();
		// create an InputProcess to handle the back key
		InputProcessor backProcessor = new BackProcessor(
				((Fly) Gdx.app.getApplicationListener()));
		inputProcessor.addProcessor(backProcessor);
		inputProcessor.addProcessor(stage);

		showLevels();
	}

	/**
	 * Shows a list of all available levels.
	 */
	public void showLevels() {

		int buttonsInARow = 3;
		float percentageOfButtonsWitdth = .8f;
		float percentageOfSpaceWidth = 1 - percentageOfButtonsWitdth;
		float buttonWidth = percentageOfButtonsWitdth / 3.0f
				* Gdx.graphics.getWidth();
		float spaceWidth = percentageOfSpaceWidth / 6 * Gdx.graphics.getWidth();

		float percentageOfButtonsHeight = .7f;
		float percentageOfSpaceHeight = .15f;
		float buttonHeight = percentageOfButtonsHeight / 3.0f
				* Gdx.graphics.getHeight();
		float spaceHeight = percentageOfSpaceHeight / 6
				* Gdx.graphics.getHeight();

		Table scrollableTable = new Table(skin);

		ArrayList<Level> allLevels = new ArrayList<Level>();
		Level level1 = new Level();
		level1.name = "Level 1";
		allLevels.add(level1);
		Level level2 = new Level();
		level2.name = "Level 2";
		allLevels.add(level2);
		Level level3 = new Level();
		level3.name = "Level 3";
		allLevels.add(level3);
		Level level4 = new Level();
		level4.name = "Level 4";
		allLevels.add(level4);
		Level level5 = new Level();
		level5.name = "Level 4";
		allLevels.add(level5);
		Level level6 = new Level();
		level6.name = "Level 4";
		allLevels.add(level6);
		Level level7 = new Level();
		level7.name = "Level 4";
		allLevels.add(level7);
		Level level8 = new Level();
		level8.name = "Level 4";
		allLevels.add(level8);
		Level level9 = new Level();
		level9.name = "Level 4";
		allLevels.add(level9);
		Level level10 = new Level();
		level10.name = "Level 4";
		allLevels.add(level10);

		scrollableTable.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		int maxRows = ((int) Math.ceil(((double) allLevels.size())
				/ ((double) buttonsInARow)));
		for (int row = 0; row < maxRows; row++) {
			int max = Math.min(allLevels.size() - (row * buttonsInARow),
					buttonsInARow);
			for (int i = 0; i < max; i++) {
				final TextButton button = new TextButton(allLevels.get(row
						* buttonsInARow + i).name, skin, "default");
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// TODO: set Level in Game
						((Fly) Gdx.app.getApplicationListener())
								.setLoadingScreen();
					}
				});
				scrollableTable.add(button).width(buttonWidth)
						.height(buttonHeight)
						.pad(spaceHeight, spaceWidth, spaceHeight, spaceWidth)
						.center();
			}
			scrollableTable.row();
			scrollableTable.invalidate();
		}

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
