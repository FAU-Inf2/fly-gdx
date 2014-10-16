package de.fau.cs.mad.fly.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Interface for each Setting that is displayed in Fly. It needs a Label which
 * is displayed as description and an Actor, like a check box or a slider.
 * Furthermore it should save its state in a given Preferences object.
 * 
 * @author Lukas Hahmann
 * 
 */
public interface ISetting {
	public static enum Groups {
		GENERAL, AUDIO, CONTROLS
	}
    public String getDescription();

    public Actor getActor();
    
    /**
     * Returns the string that identifies the helping text for this Setting in
     * the I18N
     */
    public String getHelpingText();

	public boolean isHidden();

	public Groups group();
}
