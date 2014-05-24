package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import de.fau.cs.mad.fly.ui.MainMenuScreen;

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
