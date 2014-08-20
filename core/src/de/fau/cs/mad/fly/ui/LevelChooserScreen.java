package de.fau.cs.mad.fly.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.profile.LevelGroup;
import de.fau.cs.mad.fly.profile.LevelGroupManager;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.res.Level;

/**
 * Offers a selection of levels to start.
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen extends BasicScreen {
	private LevelGroup levelGroup;
	
	/**
	 * Shows a list of all available levels.
	 */
	public void generateDynamicContent() {
		// calculate width and height of buttons and the space in between
		List<LevelProfile> allLevels = levelGroup.getLevels();
		
		stage.clear();

		// table that contains all buttons
		Table scrollableTable = new Table(skin);
		ScrollPane levelScrollPane = new ScrollPane(scrollableTable, skin);
		levelScrollPane.setScrollingDisabled(true, false);
		levelScrollPane.setFadeScrollBars(false);
		levelScrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		levelScrollPane.setFillParent(true);
		
		// create a button for each level
		int maxRows = (int) Math.ceil((double) allLevels.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
		
		for (int row = 0; row < maxRows; row++) {
			int maxColumns = Math.min(allLevels.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
			// fill a row with buttons
			for (int column = 0; column < maxColumns; column++) {
				final LevelProfile level = allLevels.get(row * UI.Buttons.BUTTONS_IN_A_ROW + column);
				final TextButton button = new TextButton(level.name, skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class));
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Loader.loadLevel(level);
					}
				});
				scrollableTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).expand();
			}
			scrollableTable.row().expand();
		}
		stage.addActor(levelScrollPane);
	}
	
	/**
	 * Sets the current group for which the level chooser screen should display the levels.
	 * 
	 * @param group		The group to display.
	 */
	public void setGroup(LevelGroup group) {
		levelGroup = group;
	}
	
	@Override
	public void show() {
		super.show();
		generateDynamicContent();
	}

	@Override
	protected void generateContent() {
		// TODO Auto-generated method stub
		
	}
}
