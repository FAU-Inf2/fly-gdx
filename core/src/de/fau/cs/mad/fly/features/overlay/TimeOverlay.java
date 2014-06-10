package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeOverlay implements IFeatureInit, IFeatureRender {
	private final Fly game;
	
	private Skin skin;
	private Stage stage;
	private Label timeDescription, timeCounter;
	private float time;
	
	public TimeOverlay(final Fly game, Stage stage) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();

		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"), Color.RED);
		
		timeDescription = addLabel("Time:", labelStyle, 0.01f, 0.0f);
		timeCounter = addLabel("0", labelStyle, 0.13f, 0.0f);
	}

	/**
	 * Adds a Label to the screen.
	 * 
	 * @param text
	 *            the default text for the Label
	 * @param labelStyle
	 *            the LabelStyle for the Label
	 * @param x
	 *            the x position on the screen in percent, should be between -100.0, +100.0
	 * @param y
	 *            the y position on the screen in percent, should be between -100.0, +100.0
	 */
	public Label addLabel(String text, LabelStyle labelStyle, float x, float y) {
		Label label = new Label(text, labelStyle);
		label.setPosition(Gdx.graphics.getWidth()*x, game.getAbsoluteY(y));
		stage.addActor(label);
		return label;
	}

	@Override
	public void render(float delta) {
		timeCounter.setText(String.valueOf((long) time));

		time += delta;
	}

	@Override
	public void init(GameController gameController) {
		time = 0.0f;
	}
}