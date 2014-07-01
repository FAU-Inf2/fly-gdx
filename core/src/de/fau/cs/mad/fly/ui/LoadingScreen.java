package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.ProgressListener;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f the game screen is loaded.
 * 
 * @author Tobias Zangl
 */
public class LoadingScreen implements Screen {

	private final SpriteBatch batch;
	private final Texture splashImg;

	private final Skin skin;
	private final Stage stage;

	private ProgressBar progressBar;
	private Loader<Level> loader;

	public LoadingScreen(final Skin skin) {
		this.skin = skin;

		batch = new SpriteBatch();
		splashImg = Assets.manager.get(Assets.flyTextureLoadingScreen);

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		addLoadingProgress();
	}

	public void initiate(Loader<Level> loader) {
		this.loader = loader;
		loader.addProgressListener(new ProgressListener.ProgressAdapter<Level>() {
			@Override
			public void progressUpdated(float percent) {
				progressBar.setValue(percent);
			}
		});
		loader.initiate();
	}

	/**
	 * Adds the progress bar to the loading screen.
	 */
	private void addLoadingProgress() {
		Table table = new Table();
		table.pad(Gdx.graphics.getWidth() * 0.2f);
		table.padTop(Gdx.graphics.getHeight() * 0.7f);
		table.setFillParent(true);
		stage.addActor(table);

		progressBar = new ProgressBar(0f, 100f, 1f, false, skin);
		progressBar.setValue(0);
		progressBar.scaleBy(100.0f);

		table.row().expand();
		table.add(progressBar).fill().pad(10f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(splashImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();

		stage.act(delta);
		stage.draw();

		loader.update();
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
		stage.dispose();
	}
}
