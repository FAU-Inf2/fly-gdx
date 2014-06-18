package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.res.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Offers a selection of Levels to start
 * 
 * @author Lukas Hahmann
 */
public class LevelChooserScreen extends BasicScreen {
	private static JsonReader reader = new JsonReader();

	public static List<Level.Head> getLevelList() {
		List<Level.Head> hs = new ArrayList<Level.Head>();
		FileHandle dirHandle = Gdx.files.internal("levels/");
		for( FileHandle f : dirHandle.list() ) {
			JsonValue j = reader.parse(f);
			Level.Head h = new Level.Head();
			h.name = j.getString("name");
			h.file = f;
			hs.add(h);
		}
		return hs;
	}

	/**
	 * Shows a list of all available levels.
	 */
	@Override
	public void generateContent() {
		// calculate width and height of buttons and the space in between
		List<Level.Head> allLevels = getLevelList();

		// table that contains all buttons
		Table scrollableTable = new Table(skin);
		scrollableTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// create a button for each level
		int maxRows = (int) Math.ceil((double) allLevels.size() / (double) UI.SmallButtons.BUTTONS_IN_A_ROW);
		for (int row = 0; row < maxRows; row++) {
			int max = Math.min(allLevels.size() - (row * UI.SmallButtons.BUTTONS_IN_A_ROW), UI.SmallButtons.BUTTONS_IN_A_ROW);
			// fill a row with buttons
			for (int i = 0; i < max; i++) {
				final Level.Head level = allLevels.get(row * UI.SmallButtons.BUTTONS_IN_A_ROW + i);
				final TextButton button = new TextButton(level.name, skin, "default");
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						((Fly) Gdx.app.getApplicationListener()).getLoader().startLoading(level);
					}
				});
				scrollableTable.add(button).width(UI.SmallButtons.BUTTON_WIDTH).height(UI.SmallButtons.BUTTON_HEIGHT).pad(UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH, UI.SmallButtons.SPACE_HEIGHT, UI.SmallButtons.SPACE_WIDTH).center();
			}
			scrollableTable.row();
		}

		// place the table of buttons in a ScrollPane to make it scrollable, if
		// not all buttons can be displayed
		ScrollPane levelScrollPane = new ScrollPane(scrollableTable, skin);
		levelScrollPane.setScrollingDisabled(true, false);
		levelScrollPane.setFillParent(true);
		levelScrollPane.setColor(skin.getColor(UI.Window.BACKGROUND_COLOR));
		stage.addActor(levelScrollPane);
	}
}
