package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.fau.cs.mad.fly.Fly;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class DebugOverlay {
	private final Fly game;
	
	private final Skin skin;
	private final Stage stage;
	private Label valueLabels[] = new Label[3];
	private int count;
	
	public DebugOverlay(final Fly game, final Stage stage, int count) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();

		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"), Color.RED);
		
		if(count > 3)
			count = 3;
		this.count = count;
		
		if(count > 0) {
			addLabel("D1:", labelStyle, 0.01f, 0.5f);
			valueLabels[0] = addLabel("0", labelStyle, 0.08f, 0.5f);
		}
		if(count > 1) {
			addLabel("D2:", labelStyle, 0.01f, 0.4f);
			valueLabels[1] = addLabel("0", labelStyle, 0.08f, 0.4f);
		}
		if(count > 2) {
			addLabel("D3:", labelStyle, 0.01f, 0.3f);
			valueLabels[2] = addLabel("0", labelStyle, 0.08f, 0.3f);
		}
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
	public Label addLabel(final String text, final LabelStyle labelStyle, float x, float y) {
		Label label = new Label(text, labelStyle);
		label.setPosition(Gdx.graphics.getWidth() * x, Gdx.graphics.getHeight()*y);
		stage.addActor(label);
		return label;
	}
	
	public void setValue(int index, final String value) {
		if(index >= count)
			return;

		valueLabels[index].setText(value);
	}
}