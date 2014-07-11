package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the frames per second.
 * 
 * @author Tobias Zangl
 */
public class FPSOverlay implements IFeatureRender {

	private final Stage stage;
	private Label fpsDescription, fpsCounter;
	
	public FPSOverlay(final Skin skin, final Stage stage) {
		this.stage = stage;
		
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		fpsDescription = new Label(I18n.t("fps"), labelStyle);
		fpsDescription.setPosition(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE*4);
        stage.addActor(fpsDescription);
        fpsCounter = new Label("", labelStyle);
        fpsCounter.setPosition(UI.Window.BORDER_SPACE + fpsDescription.getWidth() + 50, 5*UI.Window.BORDER_SPACE);
        stage.addActor(fpsCounter);
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
		label.setPosition(Gdx.graphics.getWidth() * x, Gdx.graphics.getHeight()*y);
		stage.addActor(label);
		return label;
	}

	@Override
	public void render(float delta) {
		int framesPerSecond = (int) (1.0 / delta);

		fpsCounter.setText(String.valueOf(framesPerSecond));
	}
}
