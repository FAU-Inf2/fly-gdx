package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;

import de.fau.cs.mad.fly.Fly;

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
		settingManager.display(stage, skin);
	}

	@Override
	public void hide() {
		settingManager.saveSettings();
	}

	@Override
	public void dispose() {
		settingManager.saveSettings();
	}
}
