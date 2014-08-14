package de.fau.cs.mad.fly.game.object;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Game object mover which rotates the game object and its rigid body.
 * 
 * @author Tobi
 *
 */
public class RotationMover implements IGameObjectMover {
	private GameObject gameObject;
	
	private Vector3 rotation;
	private float degrees;
	
	/**
	 * Creates a new rotation mover.
	 * @param gameObject		The game object to which the mover belongs.
	 */
	public RotationMover(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	/**
	 * Setter for the rotation.
	 * @param rotation		The rotation vector. Becomes normalized.
	 * @param degrees		The degrees per frame.
	 */
	public void setRotation(Vector3 rotation, float degrees) {
		this.rotation = rotation.nor();
		this.degrees = degrees;
	}
	
	/**
	 * Setter for the rotation. The degrees per frame are calculated as length of the vector.
	 * @param rotation		The rotation vector. Becomes normalized.
	 */
	public void setRotation(Vector3 rotation) {
		this.degrees = rotation.len();
		this.rotation = rotation.nor();
	}

	@Override
	public void move(float delta) {
		gameObject.transform.rotate(rotation, degrees * delta * 10.0f);
		gameObject.getRigidBody().setWorldTransform(gameObject.transform);
	}

}