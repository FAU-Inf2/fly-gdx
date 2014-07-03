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
	
	@Override
	protected void generateContent() {
		generateContentDynamic();
	}
	
	private void generateContentDynamic(){
		stage.clear();
		Player player =  PlayerManager.getInstance().getCurrentPlayer();
		settingManager = player.getSettingManager();
		Table table = new Table();
		table.setFillParent(true);
		table.pad(UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE, UI.Window.BORDER_SPACE);
		
		
		final Table settingTable = new Table();


		for(String s : settingManager.getSettingList()) {
			settingTable.row().expand();
			settingTable.add(settingManager.getSettingMap().get(s).getLabel()).right().pad(padding);
			settingTable.add(settingManager.getSettingMap().get(s).getActor()).pad(padding, 3*padding, padding, padding);
		}
		table.add(settingTable).center();
		stage.addActor(table);
	}
	
	@Override
	public void show() {
		super.show();
		generateContentDynamic();

	}
	

}
