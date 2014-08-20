package de.fau.cs.mad.fly.player.gravity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Applies constant gravity to an object.
 * 
 * @author Tobi
 *
 */
public class ConstantGravity implements IGravity {	
	private Vector3 gravity;
	
	public ConstantGravity(Vector3 gravity) {
		this.setGravity(gravity);
	}

	@Override
	public void applyGravity(Matrix4 transform, Vector3 movement) {
		movement.add(gravity);
	}

	/**
	 * Getter for the gravity.
	 * @return gravity
	 */
	public Vector3 getGravity() {
		return gravity;
	}

	/**
	 * Setter for the gravity.
	 * @param gravity		The new gravity.
	 */
	public void setGravity(Vector3 gravity) {
		this.gravity = gravity;
	}

}
