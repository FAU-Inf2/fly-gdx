package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
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

	/**
	 * Position of the game object.
	 */
	private final Vector3 position = new Vector3();
	
	/**
	 * Center of the bounding box of the game object.
	 */
	private final Vector3 center = new Vector3();
	
	/**
	 * Dimensions of the bounding box of the game object.
	 */
	private final Vector3 dimensions = new Vector3();

	/**
	 * Bounding box of the game object used for frustum culling.
	 */
	private final static BoundingBox bounds = new BoundingBox();

	/**
	 * Collision object of the game object.
	 */
	private btCollisionObject collisionObject;

	/**
	 * Model of the game object.
	 */
	private final GameModel gmodel;

	/**
	 * Data used for collision detection.
	 */
	public Object userData;

	/**
	 * Determines if the game object is currently visible.
	 */
	private boolean visible = true;

	/**
	 * Collision group of the game object for filtering.
	 */
	public short filterGroup = CollisionDetector.OBJECT_FLAG;
	
	/**
	 * Collision mask of the game object. It can only collide with objects in the filter mask.
	 */
	public short filterMask = CollisionDetector.ALL_FLAG;

	public String id;
	public String modelId;

	// TODO: create more constructors to match the ModelInstance constructors

	/**
	 * Contructs a new game object without any collision detection.
	 * @param model
	 */
	public GameObject(GameModel model) {
		super(model.display);
		this.gmodel = model;
		this.userData = this;
		initBoundingBox();
	}

	/**
	 * Adds a collision object with a shape to the game object and adds it to the collision world.
	 * @param shape
	 */
	public void setCollisionObject(btCollisionShape shape) {
		setCollisionObject(CollisionDetector.createObject(this, shape, this));
	}

	/**
	 * Sets the collision object of the game object.
	 * @param collisionObject
	 */
	public void setCollisionObject(btCollisionObject ollisionObject) {
		this.collisionObject = ollisionObject;
	}

	/**
	 * Initializes the bounding box for the frustum culling.
	 * <p>
	 * Size of the bounding box is doubled to make sure the object is always displayed when it should be.
	 */
	private void initBoundingBox() {		
		calculateBoundingBox(bounds);
		center.set(bounds.getCenter());
		dimensions.set(bounds.getDimensions().cpy().scl(2.0f));
	}

	/**
	 * Returns if the object is hidden.
	 */
	public boolean isHidden() { return !visible; }

	/**
	 * Returns if the object is visible.
	 */
	public boolean isVisible() { return visible; }

	/**
	 * Makes the object hidden.
	 */
	public void hide() { visible = false; }
	/**
	 * Makes the object visible.
	 */
	public void show() { visible = true; }

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
	 * Marks the game object with a special color.
	 */
	public void mark() {
		materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
	}

	/**
	 * Unmarks the object.
	 */
	public void unmark() {
		materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));
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
	public void render(ModelBatch batch, PerspectiveCamera cam) {
		render(batch, null, cam);
	}

	/**
	 * Renders the game object with environment and updates the position of the collision object if one is defined.
	 */
	public void render(ModelBatch batch, Environment environment, PerspectiveCamera cam) {
		if(collisionObject != null)
			collisionObject.setWorldTransform(transform);
		if ( visible && isVisible(cam) )
			if ( environment == null )
				batch.render(this);
			else
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
		Gdx.app.log("GameObject.dispose", "dispose " + id);
		if ( collisionObject != null )
			collisionObject.dispose();
	}
}
