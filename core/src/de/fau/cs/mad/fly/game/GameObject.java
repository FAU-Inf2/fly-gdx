package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
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
	 * Rigid body of the game object.
	 */
	protected btRigidBody rigidBody;
	
	/**
	 * Scaling stored in the level file.
	 */
	public Vector3 scaling = new Vector3(1.0f, 1.0f, 1.0f);
	
	/**
	 * Motion state for the rigid body.
	 */
	public GameObjectMotionState motionState;

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
	 * Determines if the game object is only a dummy object and the player does not lose life if colliding with it.
	 */
	protected boolean dummy = false;

	/**
	 * Collision group of the game object for filtering.
	 */
	public short filterGroup = CollisionDetector.OBJECT_FLAG;
	
	/**
	 * Collision mask of the game object. It can only collide with objects in the filter mask.
	 */
	public short filterMask = CollisionDetector.ALL_FLAG;

	/**
	 * Id of the game object.
	 */
	public String id;
	
	/**
	 * Id of the model of this game object.
	 */
	public String modelId;

    /**
     * Environment this GameObject is to be rendered with (if it is rendered)
     */
    public Environment environment;
	
	private Vector3 startLinearVelocity = new Vector3();
	private Vector3 startAngularVelocity = new Vector3();

	// TODO: create more constructors to match the ModelInstance constructors

	/**
	 * Constructs a new game object without any collision detection.
	 * @param model
	 */
	public GameObject(GameModel model) {
		this(model, "");
	}
	
	/**
	 * Constructs a new game object without any collision detection.
	 * @param model
	 * @param id
	 */
	public GameObject(GameModel model, String id) {
		super(model.display);
		this.gmodel = model;
		this.userData = this;
		this.id = id;
		initBoundingBox();
	}
	
	/**
	 * Adds a rigid body with a shape and a rigid body info to the game object and adds it to the collision world.
	 * @param shape
	 * @param rigidBodyInfo
	 */
	public void createRigidBody(String id, btCollisionShape shape, float mass, short filterGroup, short filterMask) {
		btRigidBodyConstructionInfo info = CollisionDetector.getInstance().getRigidBodyInfoManager().createRigidBodyInfo(id, shape, mass);
		
		this.filterGroup = filterGroup;
		this.filterMask = filterMask;
		this.rigidBody = CollisionDetector.createRigidBody(this, shape, this, info);
		
		rigidBody.setLinearVelocity(startLinearVelocity);
		rigidBody.setAngularVelocity(startAngularVelocity);
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
	 * Updates the scale of the bounding box if the transform matrix was scaled.
	 */
	public void scaleBoundingBox() {
		Vector3 dummy = new Vector3();
		center.scl(transform.getScale(dummy));
		dimensions.scl(transform.getScale(dummy));
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
	 * Setter if the game object is only a dummy object.
	 * 
	 * @param isDummy
	 */
	public void setDummy(boolean isDummy) {
		dummy = isDummy;
	}
	
	/**
	 * Checks if the game object is only a dummy object.
	 * 
	 * @return true, if the object is a dummy, otherwise false.
	 */
	public boolean isDummy() {
		return dummy;
	}
	
	/**
	 * Setter for the rigidBody.userData of the GameObject.
	 */
	public void setCollisionTarget(Object object) {
		if(rigidBody == null)
			return;
		
		rigidBody.userData = object;
	}
	
	/**
	 * Setter for the rigidBody.userValue of the GameObject.
	 */
	public void setCollisionType(int userValue) {
		if(rigidBody == null)
			return;
		rigidBody.setUserValue(userValue);
	}
	
	/**
	 * Setter for the rigid body restitution of the GameObject.
	 */
	public void setRestitution(float rest) {
		if(rigidBody == null)
			return;
		rigidBody.setRestitution(rest);
	}
	
	/**
	 * Adds a motion state to the game object which cares about the updating of the transform matrix if the rigid body is updated by the dynamic world.
	 */
	public void addMotionState() {
		motionState = new GameObjectMotionState();
		motionState.transform = transform;
		rigidBody.setMotionState(motionState);
	}
	
	/**
	 * Getter for the rigid body.
	 */
	public btRigidBody getRigidBody() {
		return rigidBody;
	}
	
	/**
	 * Updates the current transform matrix with the rigid body transform matrix after the rigid body simulation.
	 */
	public void updateRigidBody() {
		rigidBody.getWorldTransform(transform);
	}

	/**
	 * Renders the game object.
	 * 
	 * @param batch		The model batch of the screen.
	 * @param cam		The camera used to display the world.
	 */
	public void render(ModelBatch batch, PerspectiveCamera cam) {
		render(batch, environment, cam);
	}
	
	/**
	 * Renders the game object with environment.
	 * 
	 * @param batch				The model batch of the screen.
	 * @param environment		The environment used to display the world.
	 * @param cam				The camera used to display the world.
	 */
	public void render(ModelBatch batch, Environment environment, PerspectiveCamera cam) {		
		if(visible && isVisible(cam)) {
			if(environment == null) {
				batch.render(this);
			} else {
				batch.render(this, environment);
			}
		}
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
	
	/**
	 * Setter for the rotation.
	 * @param vel
	 */
	public void setRotation(Vector3 vel) {
		rigidBody.setAngularVelocity(vel);
	}
	
	/**
	 * Setter for the movement.
	 * @param vel
	 */
	public void setMovement(Vector3 vel) {
		rigidBody.setLinearVelocity(vel);
	}
	
	/**
	 * Setter for the start linear velocity which is used for the movement when the rigid body is created.
	 * @param vel
	 */
	public void setStartLinearVelocity(Vector3 vel) {
		startLinearVelocity = vel;
	}
	
	/**
	 * Setter for the start angular velocity which is used for the rotation when the rigid body is created.
	 * @param vel
	 */
	public void setStartAngularVelocity(Vector3 vel) {
		startAngularVelocity = vel;
	}
	
	/**
	 * Flips the direction around.
	 */
	public void flipDirection() {		
		rigidBody.setLinearVelocity(rigidBody.getLinearVelocity().scl(-1.0f));
	}
	
	/**
	 * Removes the rigid body from the collision world and disposes it.
	 */
	public void removeRigidBody() {
		if(rigidBody != null && CollisionDetector.getInstance() != null) {
			CollisionDetector.getInstance().removeRigidBody(this);
			rigidBody.dispose();
			rigidBody = null;
		}
	}

	@Override
	public void dispose() {
		removeRigidBody();
	}
}
