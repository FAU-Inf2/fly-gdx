package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
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

	private final btCollisionObject body;

	/**
	 * Creates a new GameObject with btBoxShape with the size of the
	 * BoundingBox.
	 */
	public GameObject(Model model, String rootNode, boolean mergeTransform) {
		super(model, rootNode, mergeTransform);

		calculateBoundingBox(bounds);
		center.set(bounds.getCenter());
		dimensions.set(bounds.getDimensions());

		body = new btCollisionObject();
		body.setCollisionShape(new btBoxShape(bounds.getDimensions().cpy()
				.scl(0.5f)));
	}

	/**
	 * Creates a new GameObject with given btCollisionShape.
	 */
	public GameObject(Model model, String rootNode, btCollisionShape shape,
			boolean mergeTransform) {
		super(model, rootNode, mergeTransform);

		calculateBoundingBox(bounds);
		center.set(bounds.getCenter());
		dimensions.set(bounds.getDimensions());

		body = new btCollisionObject();
		body.setCollisionShape(shape);
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
	 * Getter of the position in 3D space of the object.
	 * 
	 * @return {@link #position}
	 */
	public Vector3 getPosition() {
		return position;
	}

	@Override
	public void dispose() {
		body.dispose();
	}
}