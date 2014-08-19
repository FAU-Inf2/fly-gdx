package de.fau.cs.mad.fly.player.gravity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Applies no gravity to an object.
 * 
 * @author Tobi
 *
 */
public class EmptyGravity implements IGravity {	
	public EmptyGravity() {
	}

	@Override
	public void applyGravity(Matrix4 transform, Vector3 movement) {
	}
}
