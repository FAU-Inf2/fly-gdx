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
	private final Vector3 center = new Vector3();
	private final Vector3 dimensions = new Vector3();
	private final Vector3 position = new Vector3();

	private final static BoundingBox bounds = new BoundingBox();

	private btCollisionObject collisionObject;

	private final GameModel gmodel;

	public Object userData;

	private boolean visible = true;

	public short filterGroup = CollisionDetector.OBJECT_FLAG;
	public short filterMask = CollisionDetector.ALL_FLAG;

	public String id;
	public String modelId;

	// TODO: create more constructors to match the ModelInstance constructors

	/**
	 * Creates a new GameObject without any collision detection.
	 */
	public GameObject(GameModel model) {
		super(model.display);
		this.gmodel = model;
		this.userData = this;
		initBoundingBox();
	}

	/**
	 * Adds a collision object to the game object and adds it to the collision world.
	 */
	public void setCollisionObject(btCollisionShape shape) {
		setCollisionObject(CollisionDetector.createObject(this, shape, this));
	}

	public void setCollisionObject(btCollisionObject o) {
		collisionObject = o;
	}

	/**
	 * Initializes the bounding box for the frustum culling.
	 */
	private void initBoundingBox() {		
		calculateBoundingBox(bounds);
		center.set(bounds.getCenter());
		dimensions.set(bounds.getDimensions().cpy().scl(2.0f));
	}

	public boolean isHidden() { return !visible; }

	public boolean isVisible() { return visible; }

	public void hide() { visible = false; }

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

	public void mark() {
		materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
	}

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
