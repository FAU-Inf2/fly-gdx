package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import de.fau.cs.mad.fly.Fly;

/**
 * Handles the Back-Button of the Smartphone and the Escape-Button of the Desktop
 * to go back to the MainMenuScreen or leave the app.
 * 
 * @author Lukas Hahmann 
 */
public class BackProcessor extends InputAdapter {
	@Override
	public boolean keyDown(int keycode) {
		if ((keycode == Keys.ESCAPE) || (keycode == Keys.BACK)) {
			if(((Fly) Gdx.app.getApplicationListener()).getScreen() instanceof MainMenuScreen) {
				((Fly) Gdx.app.getApplicationListener()).getScreen().dispose();
			}
			else {
				((Fly) Gdx.app.getApplicationListener()).setMainMenuScreen();
			}
			return true;
		}
		return false;
	}
}
