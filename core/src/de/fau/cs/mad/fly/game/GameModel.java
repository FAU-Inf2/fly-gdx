package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by danyel on 12/06/14.
 */
public class GameModel implements Disposable {

	/**
	 * The displayed model.
	 */
	public final Model display;
	
	/**
	 * The model used for collision detection if a special model is needed.
	 * <p>
	 * Hitbox model is not mandatory. Check first if a hitbox model is set and if not, use the {@link #CollisionShapeManager} to create shapes dynamically.
	 */
	public final Model hitbox;
	
	/**
	 * Constructs a new game model with display and hitbox model.
	 * @param display
	 * @param hitbox
	 */
	public GameModel(final Model display, final Model hitbox) {
		this.display = display;
		this.hitbox = hitbox;
	}

	/**
	 * Constructs a new game model with display but without hitbox model.
	 * @param display
	 */
	public GameModel(final Model display) {
		this(display, display);
	}

	@Override
	public void dispose() {
		// TODO: dispose models, or done elsewhere?
	}
}