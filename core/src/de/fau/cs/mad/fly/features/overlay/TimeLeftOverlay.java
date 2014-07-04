package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IFeatureInit, IFeatureUpdate {
	private final Fly game;
	
	private final Skin skin;
	private final Stage stage;
	private Label timeDescription, timeCounter;
	private float time;
	
	public TimeLeftOverlay(final Fly game, final Stage stage, float timeMax) {
		this.game = game;
		this.stage = stage;
		skin = game.getSkin();
		
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		timeDescription = addLabel(I18n.t("leftTime"), labelStyle, 0.01f, 0.0f);
		timeCounter = addLabel(String.valueOf((long) time), labelStyle, timeDescription.getWidth()+50, 0.0f);
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
		label.setPosition(x+UI.Window.BORDER_SPACE, y+UI.Window.BORDER_SPACE);
		stage.addActor(label);
		return label;
	}

	@Override
	public void update(float delta) {
		timeCounter.setText(String.valueOf((long) time));
		if(! game.getGameController().getLevel().isGameOver() )
		{
			time -= delta;		
			if(time <= 0.0f) {
			
				time = 0.0f;
			}
			game.getGameController().getLevel().setLeftTime(time);
		}
	}

	@Override
	public void init(final GameController gameController) {		
		time = game.getGameController().getLevel().getLeftTime();
	}
}