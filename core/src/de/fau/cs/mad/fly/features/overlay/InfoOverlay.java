package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.features.IFeatureUpdate;

/**
 * 
 * @author Tobi
 *
 */
public class InfoOverlay implements IFeatureUpdate {
	/**
	 * Singleton info overlay instance.
	 */
	private static InfoOverlay instance;
	
	/**
	 * Creates the info overlay instance.
	 */
	public static void createInfoOverlay(final Skin skin, final Stage stage) {
		instance = new InfoOverlay(skin, stage);
	}

	/**
	 * Getter for the info overlay instance.
	 * @return instance
	 */
	public static InfoOverlay getInstance() {
		return instance;
	}
	
	private final Label infoLabel;
	private final Table innerTable;

	private float duration = 0.0f;

	protected InfoOverlay(final Skin skin, final Stage stage) {
		innerTable = new Table();
		final NinePatchDrawable background = new NinePatchDrawable(skin.get("green-progress-bar", NinePatch.class));
		innerTable.setBackground(background);
		
		infoLabel = new Label("", skin);
		
		innerTable.row().expand();
		innerTable.add(infoLabel);
		innerTable.row().expand();
		stage.addActor(innerTable);
		
		setVisible(false);
	}

	@Override
	public void update(float delta) {
		if(!innerTable.isVisible()) {
			return;
		}
		
		duration -= delta;
		
		if(duration < 0.0f) {
			setVisible(false);
		}
	}
	
	/**
	 * Setter for the visibility of the info overlay.
	 * @param visible	True, if the info overlay should be visible, false otherwise.
	 */
	public void setVisible(boolean visible) {
		innerTable.setVisible(visible);
	}
	
	/**
	 * Setter for the text of the info overlay.
	 * <p>
	 * Starts also the timer for the display.
	 * 
	 * @param text		The new text of the info overlay.
	 * @param duration	The duration in seconds the overlay should stay.
	 */
	public void setOverlay(String text, int duration) {
		this.duration = (float) duration;
		
		if(text.contains("\n")) {
			innerTable.setBounds(750, 1950, 2300, 400);
		} else {		
			innerTable.setBounds(750, 2050, 2300, 200);
		}
		infoLabel.setText(text);
		setVisible(true);
	}
}