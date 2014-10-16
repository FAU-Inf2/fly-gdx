package de.fau.cs.mad.fly.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/**
 * Handles the Back-Button of the Smartphone and the Escape-Button of the
 * Desktop to go back to the MainMenuScreen or leave the app.
 * 
 * @author Lukas Hahmann
 */
public class GenericBackProcessor extends BackProcessor {
    
    private BasicScreen screenToReturn;
    
    public GenericBackProcessor(BasicScreen screenToReturn) {
        this.screenToReturn = screenToReturn;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            if (screenToReturn == null) {
                Gdx.app.exit();
            } else {
                screenToReturn.set();
            }
            return true;
        }
        return false;
    }
}
