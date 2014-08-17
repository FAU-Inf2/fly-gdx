package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.GameScreen;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;

/**
 * Handles the Back-Button of the Smartphone and the Escape-Button of the Desktop
 * to go back to the MainMenuScreen or leave the app.
 * 
 * @author Lukas Hahmann 
 */
public class BackProcessor extends InputAdapter {
	@Override
	public boolean keyDown(int keycode) {
		if ( keycode == Keys.ESCAPE || keycode == Keys.BACK ) {
			Fly game = (Fly) Gdx.app.getApplicationListener();
			//Gdx.app.log("BackProcessor.keyDown", "Back, screen.class=" + game.getScreen().getClass().getName());
			if (game.getScreen() instanceof GameScreen) {
				LevelProfile lastLevel = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevel();
				PlayerProfileManager.getInstance().getCurrentPlayerProfile().setCurrentLevel(null);
		    	PlayerProfileManager.getInstance().getCurrentPlayerProfile().setLastLevel(lastLevel);
			}
			if (game.getScreen() instanceof LevelChooserScreen) {
				game.setLevelGroupScreen();
			} else if (!(game.getScreen() instanceof MainMenuScreen)) {
				game.setMainMenuScreen();
			} else {
				Gdx.app.exit();
			}
			return true;
		}
		return false;
	}
}
