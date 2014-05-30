package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.BackProcessor;
import de.fau.cs.mad.fly.Fly;

/**
 * Displays and changes the options of the game.
 *
 * @author Tobias Zangl
 */
public class SettingScreen implements Screen {
	private final Fly game;
	private final SettingManager settingManager;

	private Skin skin;
	private Stage stage;

	private InputMultiplexer inputProcessor;
	
	public SettingScreen(final Fly game) {
		this.game = game;
		skin = game.getSkin();
		
		// TODO: not updated if player changes while app is running
		settingManager = game.getPlayer().getSettingManager();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		inputProcessor = new InputMultiplexer(new BackProcessor(), stage);
		
		settingManager.display(stage, skin);
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
		settingManager.saveSettings();
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
