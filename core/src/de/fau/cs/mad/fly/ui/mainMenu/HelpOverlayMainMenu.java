package de.fau.cs.mad.fly.ui.mainMenu;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.fau.cs.mad.fly.ui.HelpFrame;
import de.fau.cs.mad.fly.ui.WithHelpScreen;

public class HelpOverlayMainMenu extends ClickListener implements InputProcessor {
    
    private final WithHelpScreen backListener;
    private final ArrayList<HelpFrame> helpFrames;
    private int currentHelpFrame;
    
    public HelpOverlayMainMenu(Skin skin, WithHelpScreen backListener) {
        this.backListener = backListener;
        helpFrames = new ArrayList<HelpFrame>();
        helpFrames.add(new HelpFrameMainMenuWelcome(skin));
        helpFrames.add(new HelpFrameMainMenuPlay(skin));
        helpFrames.add(new HelpFrameMainMenuSelectLevel(skin));
        helpFrames.add(new HelpFrameMainMenuSettings(skin));
        helpFrames.add(new HelpFrameMainMenuEnd(skin));
        currentHelpFrame = 0;
    }
    
    public void switchFrameOrQuit() {
        if(currentHelpFrame < helpFrames.size()-1) {
            currentHelpFrame++;
        }
        else {
            currentHelpFrame = 0;
            backListener.endHelp();
        }
    }
    
    public void render() {
        helpFrames.get(currentHelpFrame).render();
    }
    
    @Override
    public boolean keyDown(int keycode) {
        switchFrameOrQuit();
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        // Do nothing
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        switchFrameOrQuit();
        return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switchFrameOrQuit();
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Do nothing
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Do nothing
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Do nothing
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        // Do nothing
        return false;
    }
    
    @Override
    public void clicked(InputEvent event, float x, float y) {
        backListener.startHelp();
        Gdx.input.setInputProcessor(this);
    }
}
