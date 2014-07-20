package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.Spaceship;

/**
 * Used do display and handle any sort of collectible objects in the game.
 * 
 * @author Tobi
 */
public abstract class CollectibleObjects implements IFeatureLoad, IFeatureInit, IFeatureDispose, ICollisionListener<Spaceship, GameObject> {

	/**
	 * ModelBatch used to render the collectible objects.
	 */
	private ModelBatch batch;
	
	/**
	 * Environment used to render the collectible objects.
	 */
	private Environment environment;
	
	/**
	 * Camera used to render the collectible objects.
	 */
	private PerspectiveCamera camera;

	/**
	 * The model reference for the collectible objects.
	 */
	private String modelRef;
	
	/**
	 * List of the currently active collectible objects.
	 */
	private List<GameObject> collectibleObjects;
	
	/**
	 * Creates a new collectible objects game feature.
	 * 
	 * @param collectibleType		The type of the collectible object in the level file.
	 * @param modelRef				The model reference in the level file used to display the collectible objects.
	 */
	public CollectibleObjects(String modelRef) {
		this.modelRef = modelRef;
	}
	
	@Override
	public void load(GameController game) {
		batch = game.getBatch();
		environment = game.getLevel().getEnvironment();

		collectibleObjects = new ArrayList<GameObject>();		
		
		for(GameObject gameObject : game.getLevel().components) {
			if(gameObject.modelId.equals(modelRef)) {
				btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, gameObject);
				gameObject.createRigidBody(modelRef, shape, 1.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
				gameObject.getRigidBody().setCollisionFlags(gameObject.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
				CollisionDetector.getInstance().addRigidBody(gameObject);

				gameObject.addMotionState();
				gameObject.setDummy(true);
				gameObject.getRigidBody().setSleepingThresholds(0.01f, 0.01f);

				collectibleObjects.add(gameObject);
			}
		}

		Gdx.app.log("CollectibleObjects.load", "Collectible objects created.");
	}
	
	@Override
	public void init(GameController game) {
		camera = game.getCamera();
	}

	@Override
	public void dispose() {
		// TODO: needed?
	}
	
	/**
	 * Starts the handling after the collectible object was collected.
	 * <p>
	 * Removes the collected object from the collision world and hides it in the rendered world.
	 */
	protected abstract void handleCollecting();

	@Override
	public void onCollision(Spaceship ship, GameObject gameObject) {
		if(!collectibleObjects.contains(gameObject)) {
			return;
		}
		
		gameObject.hide();
		collectibleObjects.remove(gameObject);
		gameObject.removeRigidBody();
		
		handleCollecting();
	}
}