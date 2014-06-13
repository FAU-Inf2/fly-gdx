package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

/**
 * Wrapper for ModelInstance.
 * <p>
 * Extends ModelInstance with features for frustum culling and collision
 * detection.
 * 
 * @author Tobias Zangl
 */
public class GameObject extends ModelInstance implements Disposable {	
	private final Vector3 center = new Vector3();
	private final Vector3 dimensions = new Vector3();
	private final Vector3 position = new Vector3();

	private final static BoundingBox bounds = new BoundingBox();

	private btCollisionObject collisionObject;
	

	// TODO: create more constructors to match the ModelInstance constructors

	/**
	 * Creates a new GameObject without any collision detection.
	 */
	public GameObject(Model model) {
		super(model);

		initBoundingBox();
	}
	
	/**
	 * Adds a collision object to the game object and adds it to the collision world.
	 */
	public void addCollisionObject(CollisionDetector collisionDetector, btCollisionShape shape, int userValue, Object userData) {
		collisionObject = collisionDetector.createObject(this, shape, userValue, userData);
	}
	
	public void addCollisionObject(CollisionDetector collisionDetector, btCollisionShape shape, int userValue, Object userData, short filterGroup, short filterMask) {
		collisionObject = collisionDetector.createObject(this, shape, userValue, userData, filterGroup, filterMask);
	}
	
	/**
	 * Initializes the bounding box for the frustum culling.
	 */
	private void initBoundingBox() {		
		calculateBoundingBox(bounds);
		center.set(bounds.getCenter());
		dimensions.set(bounds.getDimensions().cpy().scl(2.0f));
	}

	/**
	 * Checks if the object is visible for the given Camera.
	 * 
	 * @param camera
	 *            the Camera for the frustum culling.
	 * @return true, if the object is visible, otherwise false.
	 */
	public boolean isVisible(final Camera camera) {
		transform.getTranslation(position);
		position.add(center);
		return camera.frustum.boundsInFrustum(position, dimensions);
	}
	
	/**
	 * Setter for the collisionObject.userData of the GameObject.
	 */
	public void setCollisionTarget(Object object) {
		if(collisionObject == null)
			return;
		
		collisionObject.userData = object;
	}
	
	/**
	 * Setter for the collisionObject.userValue of the GameObject.
	 */
	public void setCollisionType(int userValue) {
		if(collisionObject == null)
			return;
		
		collisionObject.setUserValue(userValue);
	}
	
	/**
	 * Getter for the collision object.
	 */
	public btCollisionObject getCollisionObject() {
		return collisionObject;
	}
	
	/**
	 * Renders the game object and updates the position of the collision object if one is defined.
	 */
	public void render(ModelBatch batch) {
		if(collisionObject != null) {
			collisionObject.setWorldTransform(transform);
		}
		
		batch.render(this);
	}
	
	/**
	 * Renders the game object with environment and updates the position of the collision object if one is defined.
	 */
	public void render(ModelBatch batch, Environment environment) {
		if(collisionObject != null) {
			collisionObject.setWorldTransform(transform);
		}
		
		batch.render(this, environment);
	}

	/**
	 * Getter of the position in 3D space of the object.
	 * 
	 * @return {@link #position}
	 */
	public Vector3 getPosition() {
		transform.getTranslation(position);
		return position;
	}

	@Override
	public void dispose() {
		//collisionObject.getCollisionShape().dispose();
		//collisionObject.dispose();		
	}
}