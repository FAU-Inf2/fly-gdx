package de.fau.cs.mad.fly.ui;

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
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.Fly;


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
		skin = game.getSkin();
		
		batch = new SpriteBatch();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		addMenu();
	}
	
	/**
	 * Adds the main menu to the main menu screen.
	 * <p>
	 * Includes buttons for Start, Options, Help, Exit.
	 */
	private void addMenu() {
		table = new Table();
		//table.debug();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);

		final TextButton continueButton = new TextButton("Continue", skin, "default");
		final TextButton chooseLevelButton = new TextButton("Choose Level", skin, "default");
		final TextButton optionButton = new TextButton("Settings", skin, "default");
		final TextButton helpButton = new TextButton("Help", skin, "default");
		final TextButton exitButton = new TextButton("Exit", skin, "default");

		table.row().expand();
		table.add(continueButton).fill().pad(10f).colspan(2);
		table.row().expand();
		table.add(chooseLevelButton).fill().pad(10f).uniform();
		table.add(helpButton).fill().pad(10f).uniform();
		table.row().expand();
		table.add(optionButton).fill().pad(10f).uniform();
		table.add(exitButton).fill().pad(10f).uniform();
		
		chooseLevelButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setLevelChoosingScreen();
			}
		});
		
		continueButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setLoadingScreen();
			}
		});
		
		optionButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setSettingScreen();
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
				Gdx.app.exit();
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
		//Table.drawDebug(stage);
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
