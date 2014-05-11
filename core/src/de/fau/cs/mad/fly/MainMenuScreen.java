package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Displays the main menu with Start, Options, Help and Exit buttons.
 * 
 * @author Tobias Zangl
 */
public class MainMenuScreen implements Screen {
	private final Fly game;
	
	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;
	
	public MainMenuScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
		
		Gdx.app.log("FLY", "Viewport: " + Gdx.graphics.getWidth() + ":" + Gdx.graphics.getHeight());

		addMenu();
	}
	
	/**
	 * Adds the main menu to the main menu screen.
	 * <p>
	 * Includes buttons for Start, Options, Help, Exit.
	 */
	private void addMenu() {
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		final TextButton startButton = new TextButton("Start", skin, "default");
		final TextButton optionButton = new TextButton("Options", skin, "default");
		final TextButton helpButton = new TextButton("Help", skin, "default");
		final TextButton exitButton = new TextButton("Exit", skin, "default");
		
		table.add(startButton).width(200f).height(40f).pad(10f);
		table.add(helpButton).width(200f).height(40f).pad(10f);
		table.row();
		table.add(optionButton).width(200f).height(40f).pad(10f);
		table.add(exitButton).width(200f).height(40f).pad(10f);
		
		startButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setLoadingScreen();
			}
		});
		
		optionButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setOptionScreen();
			}
		});
		
		helpButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setHelpScreen();
			}
		});
		
		exitButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				// disabled for debugging reasons
				Gdx.app.exit();
			}
		});
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
		Gdx.input.setInputProcessor(stage);
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
