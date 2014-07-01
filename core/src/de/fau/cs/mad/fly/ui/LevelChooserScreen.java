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
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Level;

/**
 * Offers a selection of Levels to start
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen extends BasicScreen {
	

	/**
	 * Shows a list of all available levels.
	 */
	@Override
	public void generateContent() {
		// calculate width and height of buttons and the space in between
		List<Level.Head> allLevels = LevelManager.getInstance().getLevelList();

		// table that contains all buttons
		Table scrollableTable = new Table(skin);
		scrollableTable.setFillParent(true);
		scrollableTable.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		
		ScrollPane levelScrollPane = new ScrollPane(scrollableTable, skin);
		levelScrollPane.setScrollingDisabled(true, false);
		levelScrollPane.setFillParent(true);
		levelScrollPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));
		
		// create a button for each level
		int maxRows = (int) Math.ceil((double) allLevels.size() / (double) UI.Buttons.BUTTONS_IN_A_ROW);
		for (int row = 0; row < maxRows; row++) {
			int max = Math.min(allLevels.size() - (row * UI.Buttons.BUTTONS_IN_A_ROW), UI.Buttons.BUTTONS_IN_A_ROW);
			// fill a row with buttons
			for (int i = 0; i < max; i++) {
				final Level.Head level = allLevels.get(row * UI.Buttons.BUTTONS_IN_A_ROW + i);
				final TextButton button = new TextButton(level.name, skin.get(UI.Buttons.STYLE, TextButtonStyle.class));
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						PlayerManager.getInstance().getCurrentPlayer().setLastLevel(level);
						((Fly) Gdx.app.getApplicationListener()).loadLevel(level);
					}
				});
				scrollableTable.add(button).width(UI.Buttons.MAIN_BUTTON_WIDTH).height(UI.Buttons.MAIN_BUTTON_HEIGHT).pad(UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH, UI.Buttons.SPACE_HEIGHT, UI.Buttons.SPACE_WIDTH).center();
			}
			scrollableTable.row();
		}

		// place the table of buttons in a ScrollPane to make it scrollable, if
		// not all buttons can be displayed
		
		stage.addActor(levelScrollPane);
	}
}
