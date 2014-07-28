package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.IntegerTimeListener;
import de.fau.cs.mad.fly.game.TimeController;
import de.fau.cs.mad.fly.ui.UI;

/**
 * 
 * @author Tobi
 *
 */
public class InfoButtonOverlay {
	/**
	 * Singleton info button overlay instance.
	 */
	private static InfoButtonOverlay instance;
	
	/**
	 * Creates the info overlay instance.
	 */
	public static void createInfoButtonOverlay(final Skin skin, final Stage stage) {
		instance = new InfoButtonOverlay(skin, stage);
	}

	/**
	 * Getter for the info overlay instance.
	 * @return instance
	 */
	public static InfoButtonOverlay getInstance() {
		return instance;
	}
	
	private final Label infoLabel;
	private final Table innerTable;
	private final TextButton button;

	protected InfoButtonOverlay(final Skin skin, final Stage stage) {
		innerTable = new Table();
		final NinePatchDrawable background = new NinePatchDrawable(skin.get("green-progress-bar", NinePatch.class));
		innerTable.setBackground(background);
		
		infoLabel = new Label("", skin);
		
		innerTable.row().expand();
		innerTable.add(infoLabel);
		innerTable.row().expand();
		button = new TextButton(I18n.t("ok"), skin, UI.Buttons.DEFAULT_STYLE);
		innerTable.add(button);
		innerTable.row().expand();
		stage.addActor(innerTable);
		
		setVisible(false);

		button.addListener(new ClickListener() {
			@Override 
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});
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
	 * 
	 * @param text		The new text of the info overlay.
	 */
	public void setOverlay(String text) {

		if(text.contains("\n")) {
			innerTable.setBounds(800, 1600, 2400, 750);
		} else {		
			innerTable.setBounds(800, 1800, 2400, 550);
		}
		infoLabel.setText(text);
		setVisible(true);
	}
}