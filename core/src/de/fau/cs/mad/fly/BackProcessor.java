package de.fau.cs.mad.fly;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class BackProcessor extends InputAdapter {
	
	private Fly game;

	public BackProcessor(Fly game) {
		this.game = game;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if ((keycode == Keys.ESCAPE) || (keycode == Keys.BACK)) {
			game.setMainMenuScreen();
		}
		return false;
	}
}
