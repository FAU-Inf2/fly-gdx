package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.Spaceship;

/**
 * Used do display and handle any sort of collectible objects in the game.
 * 
 * @author Tobi
 */
public abstract class CollectibleObjects implements IFeatureLoad, IFeatureDispose, ICollisionListener<Spaceship, Collectible> {

	/**
	 * The type of the collectible objects.
	 */
	private String type;
	
	/**
	 * List of the currently active collectible objects.
	 */
	private List<Collectible> collectibleObjects;
	
	/**
	 * Creates a new collectible objects game feature.
	 * 
	 * @param collectibleType		The type of the collectible object in the level file.
	 */
	public CollectibleObjects(String type) {
		this.type = type;
	}
	
	@Override
	public void load(GameController game) {
		collectibleObjects = new ArrayList<Collectible>();
		
		for(Collectible c : game.getLevel().getUpgrades()) {
			if(c.getType().equals(type)) {
				GameObject gameObject = c.getGameObject();
				btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(type, gameObject);
				gameObject.createRigidBody(type, shape, 1.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
				gameObject.getRigidBody().setCollisionFlags(gameObject.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
				CollisionDetector.getInstance().addRigidBody(gameObject);

				gameObject.addMotionState();
				gameObject.setDummy(true);
				gameObject.getRigidBody().setSleepingThresholds(0.01f, 0.01f);
				
				gameObject.userData = c;

				collectibleObjects.add(c);
			}
		}

		Gdx.app.log("CollectibleObjects.load", "Collectible object rigid bodies created.");
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
	protected abstract void handleCollecting(Collectible c);

	@Override
	public void onCollision(Spaceship ship, Collectible c) {		
		if(!collectibleObjects.contains(c)) {
			return;
		}
		
		c.getGameObject().hide();
		collectibleObjects.remove(c);
		c.getGameObject().removeRigidBody();
		
		handleCollecting(c);
	}
}