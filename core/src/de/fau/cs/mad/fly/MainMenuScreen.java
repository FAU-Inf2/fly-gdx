package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Displays the main menu with:
 * - Start
 * - Options
 * - Help
 * - Exit
 * 
 * @author Tobias Zangl
 */
public class MainMenuScreen implements Screen {
	final Fly game;
	
	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;
	
	private Window infoWindow;
	
	public MainMenuScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage();
		
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		final TextButton startButton = new TextButton("Start", skin, "default");
		final TextButton optionButton = new TextButton("Options", skin, "default");
		final TextButton helpButton = new TextButton("Help", skin, "default");
		final TextButton exitButton = new TextButton("Exit", skin, "default");
		
		infoWindow = new Window("Info", skin, "default");
		infoWindow.setVisible(false);
		final Label label = new Label("Game started!", skin);
		infoWindow.add(label);
		stage.addActor(infoWindow);
		
		startButton.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				if(infoWindow.isVisible()) {
					infoWindow.setVisible(false);
				} else {
					infoWindow.setVisible(true);
				}
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
				//Gdx.app.exit();
			}
		});
		
		table.add(startButton).width(200f).height(40f).pad(10f);
		table.add(helpButton).width(200f).height(40f).pad(10f);
		table.row();
		table.add(optionButton).width(200f).height(40f).pad(10f);
		table.add(exitButton).width(200f).height(40f).pad(10f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		    
		batch.begin();
		stage.draw();
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
