package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.Fly;

/**
 * Displays and changes the options of the game.
 *
 * @author Tobias Zangl
 */
public class OptionScreen implements Screen, InputProcessor {
	private final Fly game;
	
	private SpriteBatch batch;
	private Skin skin;
	private Stage stage;
	private Table table;

	private InputMultiplexer inputMultiplexer;
	
	public OptionScreen(final Fly game) {
		this.game = game;
		skin = game.getSkin();
		
		batch = new SpriteBatch();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		inputMultiplexer = new InputMultiplexer(stage, this);
		
		addOptions();
	}
	
	/**
	 * Adds the options and the Save and Back buttons to the option screen.
	 */
	private void addOptions() {
		table = new Table();
		//table.debug();
		table.pad(Gdx.graphics.getWidth() * 0.1f);
		table.setFillParent(true);
		stage.addActor(table);
		
		for(int i = 0; i < 2; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final TextField textField = new TextField("...", skin);
			
			table.row().expand();
			table.add(label).pad(2f);
			table.add(textField).pad(2f);
		}
		
		for(int i = 2; i < 4; i++) {
			final Label label = new Label("Option " + i + ": ", skin);
			final SelectBox<String> selectBox = new SelectBox<String>(skin);
			selectBox.setItems(new String[] {"Item 1", "Item 2", "Item 3"});
			
			table.row().expand();
			table.add(label).pad(2f);
			table.add(selectBox).pad(2f);
		}
		
		for(int i = 4; i < 6; i++) {
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
		//Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchBackKey(true);
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

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			game.setMainMenuScreen();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
