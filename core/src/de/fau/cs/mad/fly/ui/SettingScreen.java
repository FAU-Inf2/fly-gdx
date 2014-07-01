package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Displays and changes the options of the game.
 *
 * @author Tobias Zangl
 */
public class SettingScreen extends BasicScreen {
	private SettingManager settingManager;
	
	@Override
	protected void generateContent() {
		// TODO: not updated if player changes while app is running
		settingManager = ((Fly) Gdx.app.getApplicationListener()).getPlayer().getSettingManager();
		Table table = new Table();
		table.setFillParent(true);
		table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		stage.addActor(table);
		
		final Table settingTable = new Table();
		final ScrollPane settingPane = new ScrollPane(settingTable, skin);
		settingPane.setFadeScrollBars(false);
	 	settingPane.setScrollingDisabled(true, false);
	 	settingPane.setStyle(skin.get(UI.Window.TRANSPARENT_SCROLL_PANE_STYLE, ScrollPane.ScrollPaneStyle.class));

		for(String s : settingManager.getSettingList()) {
			settingTable.row().expand();
			settingTable.add(settingManager.getSettingMap().get(s).getLabel()).pad(6f).uniform();
			settingTable.add(settingManager.getSettingMap().get(s).getActor()).pad(6f).uniform();
		}

		table.row().expand();
		table.add(settingPane);
	}

}
