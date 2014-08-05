package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.profile.LevelManager;

/**
 * Offers a selection of level groups.
 * 
 * @author Lukas Hahmann
 */
public class LevelGroupScreen extends BasicScreen {
	
	/**
	 * Shows a list of all available level groups.
	 */
	@Override
	public void generateContent() {
		// calculate width and height of buttons and the space in between
		List<LevelManager.LevelGroup> levelGroups = LevelManager.getInstance().getLevelGroups();

		// table that contains all buttons
		Table scrollableTable = new Table(skin);
		ScrollPane levelScrollPane = new ScrollPane(scrollableTable, skin);
		levelScrollPane.setScrollingDisabled(true, false);
		levelScrollPane.setFadeScrollBars(false);
		levelScrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		levelScrollPane.setFillParent(true);
		
		// create a button for each level group
		int maxRows = (int) Math.ceil((double) levelGroups.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
		
		for (int row = 0; row < maxRows; row++) {
			int maxColumns = Math.min(levelGroups.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
			// fill a row with buttons
			for (int column = 0; column < maxColumns; column++) {
				final LevelManager.LevelGroup group = levelGroups.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
				final TextButton button = new TextButton(group.name + "/", skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class));
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						((Fly) Gdx.app.getApplicationListener()).setLevelChooserScreen(group);
					}
				});
				scrollableTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
			}
			scrollableTable.row().expand();
		}
		stage.addActor(levelScrollPane);
	}
}
