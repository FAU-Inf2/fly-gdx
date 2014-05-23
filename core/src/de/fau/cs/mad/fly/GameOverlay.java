package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Displays and updates the 2D overlay while the 3D world is rendered
 * 
 * @author Tobias Zangl
 */
public class GameOverlay {
	private final Fly game;
	
	private Skin skin;
	private Stage stage;
	private Label timeDescription, timeCounter;
	private float time;
	private Label fpsDescription, fpsCounter;
	private int framesPerSecond;
	
	public GameOverlay(final Fly game) {
		this.game = game;
		
		skin = game.getSkin();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"), Color.RED);
		timeDescription = new Label("Time:", labelStyle);
		timeDescription.setPosition(20f, 10f);
		timeCounter = new Label("0", labelStyle);
		timeCounter.setPosition(100f, 10f);
		
		stage.addActor(timeDescription);
		stage.addActor(timeCounter);
		
		fpsDescription = new Label("FPS:", labelStyle);
		fpsDescription.setPosition(20f, game.getAbsoluteY(0.9f));
		fpsCounter = new Label("0", labelStyle);
		fpsCounter.setPosition(100f, game.getAbsoluteY(0.9f));

		stage.addActor(fpsDescription);
		stage.addActor(fpsCounter);

		initOverlay();
	}
	
	public void initOverlay() {
		time = 0.0f;

		if(!game.getSettingManager().getCheckBoxValue("showTime")) {
			timeDescription.setVisible(false);
			timeCounter.setVisible(false);
		} else {
			timeDescription.setVisible(true);
			timeCounter.setVisible(true);
		}
		
		if(!game.getSettingManager().getCheckBoxValue("showFPS")) {
			fpsDescription.setVisible(false);
			fpsCounter.setVisible(false);
		} else {
			fpsDescription.setVisible(true);
			fpsCounter.setVisible(true);
		}
	}
	
	public void render(float delta) {
		framesPerSecond = (int) (1.0 / delta);

		timeCounter.setText(String.valueOf((long) time));
		fpsCounter.setText(String.valueOf(framesPerSecond));
		stage.act(delta);
		stage.draw();
		
		time += delta;
	}
}
