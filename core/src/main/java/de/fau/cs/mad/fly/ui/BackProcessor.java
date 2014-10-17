package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.ui.screens.LevelLoadingScreen;
import de.fau.cs.mad.fly.ui.screens.MainMenuScreen;

/**
 * Handles the Back-Button of the Smartphone and the Escape-Button of the
 * Desktop to go back to the MainMenuScreen or leave the app.
 * 
 * @author Lukas Hahmann
 */
public class BackProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            Fly game = (Fly) Gdx.app.getApplicationListener();
            
            if (!(game.getScreen() instanceof MainMenuScreen)) {
                if (game.getScreen() instanceof LevelLoadingScreen) {
                    game.getGameController().disposeGame();
                }
                ((Fly)Gdx.app.getApplicationListener()).getMainMenuScreen().set();
            } else {
                Gdx.app.exit();
            }
            return true;
        }
        return false;
    }
}
