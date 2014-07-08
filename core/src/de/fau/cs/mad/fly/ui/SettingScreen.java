package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Displays and changes the options of the game.
 * 
 * @author Tobias Zangl
 */
public class SettingScreen extends BasicScreen {
	private SettingManager settingManager;
	private float padding = 50;
	Table settingTable;
	String displayPlayer = "";

	@Override
	protected void generateContent() {
		settingTable = new Table();

		Table table = new Table();
		table.setFillParent(true);
		table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE,
				UI.Window.BORDER_SPACE);
		table.add(settingTable).center();
		stage.addActor(table);
		generateContentDynamic();
	}

	private void generateContentDynamic() {
		Player player = PlayerManager.getInstance().getCurrentPlayer();
		if (displayPlayer == null || (!displayPlayer.equals(player.getName()))) {
			settingTable.clear();
			player.createSettingsUI();
			settingManager = player.getSettingManager();
			for (String s : settingManager.getSettingList()) {
				settingTable.row().expand();
				settingTable.add(settingManager.getSettingMap().get(s).getLabel()).right()
						.pad(padding);
				settingTable.add(settingManager.getSettingMap().get(s).getActor()).pad(padding,
						3 * padding, padding, padding);
			}
			displayPlayer = PlayerManager.getInstance().getCurrentPlayer().getName();
		}
	}

	@Override
	public void show() {
		super.show();
		generateContentDynamic();

	}

}
