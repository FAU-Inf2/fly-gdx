package de.fau.cs.mad.fly.ui.help;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Overlay that manages several HelpFrames for different things that should be
 * described in the current screen.
 * 
 * @author Lukas Hahmann
 * 
 */
public class HelpOverlay extends ClickListener implements InputProcessor {
    
    private final WithHelpOverlay backListener;
    private final List<OverlayFrame> helpFrames;
    private int currentHelpFrame;
    
    /**
     * Create a new {@link HelpOverlay}. No {@link OverlayFrame}s are in this
     * {@link HelpOverlay} unless you call {@link #addHelpFrame(OverlayFrame)}.
     * 
     * @param screen
     *            that is called to display the help frames in this
     *            {@link HelpOverlay}
     */
    public HelpOverlay(WithHelpOverlay screen) {
        this.backListener = screen;
        helpFrames = new ArrayList<OverlayFrame>();
        currentHelpFrame = 0;
    }
    
    /**
     * Adds a new {@link OverlayFrame} to this overlay.
     * <p>
     * The order of how {@link OverlayFrame}s are added is the order the
     * {@link OverlayFrame}s are shown.
     * 
     * @param newHelpFrame
     */
    public void addHelpFrame(OverlayFrame newHelpFrame) {
        helpFrames.add(newHelpFrame);
    }
    
    /**
     * Internal method, that is called, whenever a key is pressed or the user
     * touches the screen. It goes to the next {@link OverlayFrame} or ends the
     * help if the last {@link OverlayFrame} is reached.
     */
    private void switchFrameOrQuit() {
        if (currentHelpFrame < helpFrames.size() - 1) {
            currentHelpFrame++;
            helpFrames.get(currentHelpFrame).generateContent();
        } else {
            currentHelpFrame = 0;
            backListener.endHelp();
        }
    }
    
    /**
     * Render-Method that must be called by the {@link #backListener} to display
     * this help.
     */
    public void render() {
        helpFrames.get(currentHelpFrame).render();
    }
    
    /**
     * When pressing the back button or Esc, the help mode should be ended to
     * avoid that a user is stacked to the help even if he/she misclicked on the
     * help button.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            currentHelpFrame = 0;
            backListener.endHelp();
        }
        return true;
    }
    
    /**
     * Key-Pressing is already catched by {@link #keyDown(int)}.
     */
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
        helpFrames.get(currentHelpFrame).generateContent();
        Gdx.input.setInputProcessor(this);
    }
}
