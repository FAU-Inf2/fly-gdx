package de.fau.cs.mad.fly.ui;

import java.util.ArrayList;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

		Table scrollableTable = new Table(skin);

		ArrayList<Level> allLevels = new ArrayList<Level>();

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

		final TextButton level1 = new TextButton(String.valueOf(Gdx.graphics
				.getWidth()), skin, "default");
		final TextButton level2 = new TextButton(String.valueOf(Gdx.graphics
				.getHeight()), skin, "default");
		final TextButton level3 = new TextButton(String.valueOf(buttonWidth),
				skin, "default");
		final TextButton level4 = new TextButton(String.valueOf(spaceWidth),
				skin, "default");
		final TextButton level5 = new TextButton("Level 5", skin, "default");
		final TextButton level6 = new TextButton("Level 6", skin, "default");
		final TextButton level7 = new TextButton("Level 7", skin, "default");
		final TextButton level8 = new TextButton("Level 8", skin, "default");
		final TextButton level9 = new TextButton("Level 9", skin, "default");
		final TextButton level10 = new TextButton("Level 10", skin, "default");
		final TextButton level11 = new TextButton("Level 11", skin, "default");
		final TextButton level12 = new TextButton("Level 12", skin, "default");

		ArrayList<TextButton> buttons = new ArrayList<TextButton>();
		buttons.add(level1);
		buttons.add(level2);
		buttons.add(level3);
		buttons.add(level4);
		buttons.add(level5);
		buttons.add(level6);
		buttons.add(level7);
		buttons.add(level8);
		buttons.add(level9);
		buttons.add(level10);
		buttons.add(level11);
		buttons.add(level12);

		scrollableTable.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		for (int row = 0; row < 4; row++) {
			for (int i = row * 3; i < row * 3 + 3; i++) {
				scrollableTable.add(buttons.get(i)).width(buttonWidth)
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
