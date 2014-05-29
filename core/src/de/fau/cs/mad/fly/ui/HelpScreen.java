package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.BackProcessor;
import de.fau.cs.mad.fly.Fly;

/**
 * Displays the help information.
 *
 * @author Tobias Zangl
 */
public class HelpScreen implements Screen {
	private final Fly game;

	private Skin skin;
	private Stage stage;
	private Table table;
	
	private InputMultiplexer inputProcessor;
	
	public HelpScreen(final Fly game) {
		this.game = game;
		skin = game.getSkin();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		inputProcessor = new InputMultiplexer(new BackProcessor(), stage);
		
		addHelp();
	}
	
	/**
	 * Adds the scrollable help text and the Back button to the help screen.
	 */
	private void addHelp() {
		table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);
		
		final String helpString = "Lorem ipsum dolor sit amet,\nconsectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut\nlabore et dolore magna aliqua.\nUt enim ad minim veniam,\nquis nostrud exercitation ullamco\nlaboris nisi ut aliquip ex\nea commodo consequat.\nDuis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\nExcepteur sint occaecat cupidatat non proident,\nsunt in culpa qui officia deserunt mollit anim\nid est laborum.\nLorem ipsum dolor sit amet,\nconsectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut\nlabore et dolore magna aliqua.\nUt enim ad minim veniam,\nquis nostrud exercitation ullamco\nlaboris nisi ut aliquip ex\nea commodo consequat.\nDuis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\nExcepteur sint occaecat cupidatat non proident,\nsunt in culpa qui officia deserunt mollit anim\nid est laborum.";
		final Label helpLabel = new Label(helpString, skin);
		
		final Table helpTable = new Table();
		final ScrollPane helpPane = new ScrollPane(helpTable, skin);
		helpTable.add(helpLabel).pad(10f);
		helpPane.setFadeScrollBars(true);
		
		table.row().expand();
		table.add(helpPane);
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
		Gdx.input.setCatchBackKey(true);
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
		// TODO Auto-generated method stub
	}
}
