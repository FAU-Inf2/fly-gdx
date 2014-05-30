package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display the frames per second.
 * 
 * @author Tobias Zangl
 */
public class FPSOverlay implements IFeatureRender {
	private final Fly game;
	
	private Skin skin;
	private Stage stage;
	private Label fpsDescription, fpsCounter;
	
	public FPSOverlay(final Fly game, Stage stage) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();

		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"), Color.RED);
		
		fpsDescription = addLabel("FPS:", labelStyle, 0.05f, 0.92f);
		fpsCounter = addLabel("0", labelStyle, 0.15f, 0.92f);
	}

	/**
	 * Adds a Label to the screen.
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
		label.setPosition(20.0f, 0.0f);
		label.setPosition(game.getAbsoluteX(x), game.getAbsoluteY(y));
		stage.addActor(label);
		return label;
	}

	@Override
	public void render(float delta) {
		int framesPerSecond = (int) (1.0 / delta);

		fpsCounter.setText(String.valueOf(framesPerSecond));
		stage.act(delta);
		stage.draw();
	}
}
