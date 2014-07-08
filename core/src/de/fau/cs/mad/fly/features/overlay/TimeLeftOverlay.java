package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.game.IntegerTimeListener;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display the used time.
 * 
 * @author Tobias Zangl
 */
public class TimeLeftOverlay implements IntegerTimeListener {

	private Label timeDescription, timeCounter;

	public TimeLeftOverlay(Skin skin, final Stage stage) {
		LabelStyle labelStyle = skin.get("red", LabelStyle.class);
		timeDescription = new Label(I18n.t("leftTime"), labelStyle);
		timeDescription.setPosition(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		stage.addActor(timeDescription);
		timeCounter = new Label("", labelStyle);
		timeCounter.setPosition(UI.Window.BORDER_SPACE + timeDescription.getWidth() + 50, 2*UI.Window.BORDER_SPACE);
		stage.addActor(timeCounter);
	}

	@Override
	public void integerTimeChanged(int newTime) {
		//Gdx.app.log("timeChanged", "overlay: " + String.valueOf(newTime));
		timeCounter.setText(String.valueOf(newTime));
	}
}