package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Displays and changes the options of the game.
 *
 * @author Tobias Zangl
 */
public class OptionScreen implements Screen {
	private final Fly game;
	
	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;
	
	public OptionScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage();
		
		addOptions();
	}
	
	private void addOptions() {
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		for(int i = 0; i < 2; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final TextField textField = new TextField("...", skin);
			
			table.add(label).pad(2f);
			table.add(textField).pad(2f);
			table.row();
		}
		
		for(int i = 2; i < 4; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final SelectBox<String> selectBox = new SelectBox<String>(skin);
			selectBox.setItems(new String[] {"Item 1", "Item 2", "Item 3"});
			
			table.add(label).pad(2f);
			table.add(selectBox).pad(2f);
			table.row();
		}
		
		for(int i = 4; i < 6; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final CheckBox checkBox = new CheckBox("", skin);
			
			table.add(label).pad(2f);
			table.add(checkBox).pad(2f);
			table.row();
		}
		
		final TextButton saveButton = new TextButton("Save", skin, "default");
		final TextButton backButton = new TextButton("Back", skin, "default");
		
		saveButton.addListener(new ClickListener() {			
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				saveOptions();
				game.setMainMenuScreen();
			}
		});
		
		backButton.addListener(new ClickListener() {			
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				game.setMainMenuScreen();
			}
		});

		table.add(saveButton).width(200f).height(40f).pad(10f);
		table.add(backButton).width(200f).height(40f).pad(10f);
	}
	
	private void saveOptions() {
		// TODO
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		    
		batch.begin();
		stage.act(delta);
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
