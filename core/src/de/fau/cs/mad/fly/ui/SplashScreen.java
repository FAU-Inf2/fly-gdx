package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import de.fau.cs.mad.fly.Fly;

/**
 * Displays the splash screen.
 * 
 * @author Tobias Zangl
 */
public class SplashScreen implements Screen {
	private final Fly game;
	
	private final SpriteBatch batch;
	private Texture splashImg;
	
	private long startTime;
	private final static long splashDuration = 2000;
	
	public SplashScreen(final Fly game) {
		this.game = game;
		
		batch = new SpriteBatch();
		splashImg = new Texture("Fly.png");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(splashImg, 0, 0);
		batch.end();
		
		if(TimeUtils.millis() > startTime + splashDuration) {
			game.setMainMenuScreen();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		startTime = TimeUtils.millis();
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
