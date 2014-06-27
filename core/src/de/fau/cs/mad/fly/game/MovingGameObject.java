package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.math.Vector3;

/**
 * A game object that is able to move.
 * <p>
 * Extends GameObject and adds a direction and an up vector.
 * Is able to move forward in a specific speed and to rotate the direction and the up vector for a rotation of the whole object.
 * 
 * @author Tobias Zangl
 */
public class MovingGameObject extends GameObject {
	
	/**
	 * The moving speed of the object.
	 */
	private float movingSpeed = 0.0f;

	/**
	 * The moving direction of the object.
	 */
	private Vector3 directionVector;
	
	/**
	 * The up direction of the object, always perpendicular to the moving direction.
	 */
	private Vector3 upVector;
	
	/**
	 * The rotation speed if the object has to perform a rotation in every frame.
	 */
	private float rotationSpeed = 0.0f;
	
	/**
	 * The rotation vector if the object has to perform a rotation in every frame.
	 */
	private Vector3 rotationVector;

	/**
	 * Contructs a moving game object with a model.
	 * @param model
	 */
	public MovingGameObject(GameModel model) {
		super(model);

		directionVector = new Vector3(1.0f, 0.0f, 0.0f);
		upVector = new Vector3(0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * Rotates the object.
	 * @param delta
	 */
	public void rotate(float delta) {
		directionVector.rotate(rotationVector, rotationSpeed * delta);
		upVector.rotate(rotationVector, rotationSpeed * delta);
		
		// TODO: use dir, up, cross(dir, up) as matrix
		transform.rotate(rotationVector, rotationSpeed * delta);
	}
	
	/**
	 * Moves the object in its direction with its given speed.
	 * @param delta
	 */
	public void move(float delta) {
		Vector3 movingTranslation = directionVector.cpy().scl(movingSpeed * delta);
		transform.trn(movingTranslation);
	}
	
	
	/**
	 * Flips the direction vector and the rotation vector around.
	 */
	public void flipDirection() {
		directionVector.scl(-1.0f);
		rotationVector.scl(-1.0f);
	}
	
	/**
	 * Setter for the rotation vector.
	 * @param vector
	 */
	public void setRotationVector(Vector3 vector) {
		rotationVector = vector;
		rotationVector.nor();
	}
	
	/**
	 * Setter for the rotation speed.
	 * @param speed
	 */
	public void setRotationSpeed(float speed) {
		rotationSpeed = speed;
	}
	
	/**
	 * Setter for the moving vector. It also changes the up vector accordingly.
	 * @param vector
	 */
	public void setMovingVector(Vector3 vector) {
		directionVector = vector.nor();
		if(vector != new Vector3(1.0f, 0.0f, 0.0f)) {
			Vector3 helpVector = vector.cpy().crs(new Vector3(1.0f, 0.0f, 0.0f));
			upVector = vector.cpy().crs(helpVector);
		} else {
			Vector3 helpVector = vector.cpy().crs(new Vector3(0.0f, 1.0f, 0.0f));
			upVector = vector.cpy().crs(helpVector);
		}
	}
	
	/**
	 * Setter for the moving speed.
	 * @param speed
	 */
	public void setMovingSpeed(float speed) {
		movingSpeed = speed;
	}

}
