package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.ProgressListener;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.ui.ScalableProgressBar.ScalableProgressBarStyle;

/**
 * Displays the loading screen with a progress bar.
 * <p>
 * If the value of the progress bar reaches 100f the game screen is loaded.
 * 
 * @author Tobias Zangl
 */
public class LoadingScreen extends BasicScreen {

	private Sprite background;

	private ScalableProgressBar progressBar;
	private Loader<Level> loader;
	private Batch batch;
	private float progressBarWidth = 2000f;


	public void initiate(Loader<Level> loader) {
		batch = new SpriteBatch();
		Assets.load(Assets.background);
		background = new Sprite(Assets.manager.get(Assets.background));
		// Here the current display size should be used, to make sure the image is not stretched
		float xSkalingFactor = Gdx.graphics.getWidth()/background.getWidth();
		float ySkalingFactor = Gdx.graphics.getHeight()/background.getHeight();
		float deltaX = 0f;
		float deltaY = 0f;
		background.setOrigin(0,0);
		if(xSkalingFactor >= ySkalingFactor) {
			background.setScale(xSkalingFactor);
			deltaY = (Gdx.graphics.getHeight() - background.getHeight() * xSkalingFactor)/2.0f;
		}
		else {
			background.setScale(ySkalingFactor);
			deltaX = (Gdx.graphics.getWidth() - background.getWidth() * ySkalingFactor)/2.0f;
		}
		background.setPosition(deltaX, deltaY);
		
		ScalableProgressBarStyle style = skin.get("default", ScalableProgressBarStyle.class);
		progressBar = new ScalableProgressBar(style);
		progressBar.setWidth(progressBarWidth);
		
		Table table = new Table();
		table.setFillParent(true);
		table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		stage.addActor(table);
		
		table.row().expand();
		table.add(progressBar).bottom();
		table.row().expand();
		
		this.loader = loader;
		loader.addProgressListener(new ProgressListener.ProgressAdapter<Level>() {
			@Override
			public void progressUpdated(float percent) {
				progressBar.setProgress(percent/100f);
			}
		});
		loader.initiate();
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		background.draw(batch);
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
		stage.dispose();
	}


	@Override
	protected void generateContent() {
		// done in initiate
	}
}
