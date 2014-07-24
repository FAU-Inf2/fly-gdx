package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.IntegerTimeListener;
import de.fau.cs.mad.fly.game.TimeController;

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
	}

	@Override
	public void update(float delta) {
		if(!innerTable.isVisible()) {
			return;
		}
		
		System.out.println(duration);
		
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
		System.out.println("GO " + duration);
		this.duration = (float) duration;
		
		if(text.contains("\n")) {
			innerTable.setBounds(800, 1950, 2400, 400);
		} else {		
			innerTable.setBounds(800, 2050, 2400, 200);
		}
		infoLabel.setText(text);
		setVisible(true);
	}
}