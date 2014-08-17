package de.fau.cs.mad.fly.player.gravity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Interface for gravity.
 * 
 * @author Tobi
 *
 */
public interface IGravity {	
	/**
	 * Applied every frame.
	 * 
	 * @param transform			The transform matrix of the object.
	 * @param movement			The movement which gets changed by the gravity.
	 */
	public void applyGravity(Matrix4 transform, Vector3 movement);
}
