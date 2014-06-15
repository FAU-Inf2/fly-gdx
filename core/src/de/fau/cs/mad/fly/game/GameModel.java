package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Created by danyel on 12/06/14.
 */
public class GameModel {

	public final Model display;
	public final Model hitbox;

	public GameModel(final Model display, final Model hitbox) {
		this.display = display;
		this.hitbox = hitbox;
	}

	public GameModel(final Model display) {
		this(display, display);
	}

}