package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f the game screen is loaded.
 * 
 * @author Tobias Zangl
 */
public class LoadingScreen implements Screen {
	private final Fly game;
	
	private SpriteBatch batch;
	private Texture splashImg;
	
	private Skin skin;
	private Stage stage;
	
	private ProgressBar progressBar;
	
	private float progress = 0f;
	
	public LoadingScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		splashImg = new Texture("Fly.png");
	
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		if(Gdx.app.getType() == ApplicationType.Android) {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
		} else {
			stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		}
	
		addLoadingProgress();
	}

	/**
	 * Adds the progress bar to the loading screen.
	 */
	private void addLoadingProgress() {				
		progressBar = new ProgressBar(0f, 100f, 1f, false, skin);
		progressBar.setValue(progress);
		
		stage.addActor(progressBar);
	}
	
	/**
	 * Sets the value of the progress bar.
	 * 
	 * @param value the new value for the progress bar.
	 */
	public void setProgress(float value) {
		progress = value;
		progressBar.setValue(progress);
	}
	
	/**
	 * Increments the value of the progress bar.
	 * 
	 * @param value the incremental value.
	 */
	public void incProgress(float value) {
		progress += value;
		progressBar.setValue(progress);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(splashImg, 0, 0);
		batch.end();
		
		stage.act(delta);
		stage.draw();
		
		incProgress(1f);
		
		if(progress >= 100f) {
			game.setGameScreen();
			setProgress(0f);
		}
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
		splashImg.dispose();
		batch.dispose();
	}
}
