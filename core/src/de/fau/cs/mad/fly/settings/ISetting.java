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
	public Label getLabel();

	public Actor getActor();
}
