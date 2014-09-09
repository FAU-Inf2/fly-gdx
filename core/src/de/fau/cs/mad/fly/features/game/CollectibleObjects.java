package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.CollectibleManager;

/**
 * Used do display and handle any sort of collectible objects in the game.
 * 
 * @author Tobi
 */
public abstract class CollectibleObjects implements IFeatureLoad, IFeatureDispose, ICollisionListener {
	
	/**
	 * The collectible manager.
	 */
	private CollectibleManager collectibleManager;

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
		collectibleManager = game.getLevel().getCollectibleManager();
		
		collectibleObjects = new ArrayList<Collectible>();
		CollisionDetector collisionDetector = CollisionDetector.getInstance();
		
		for(Collectible c : collectibleManager.getCollectibles()) {
			if(c.getType().equals(type)) {
				c.createShapeAndRigidBody(collisionDetector, type);
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
	public void onCollision(GameObject g1, GameObject g2) {
		if(!(g2 instanceof Collectible)) {
			return;
		}
		
		Collectible c = (Collectible) g2;
			
		
		if(!collectibleObjects.contains(c)) {
			return;
		}
		
		c.hide();
		collectibleObjects.remove(c);

		handleCollecting(c);
		
		c.removeRigidBody();
		collectibleManager.removeCollectible(c);
	}
	
	public void addObject(Collectible c) {
		collectibleObjects.add(c);
	}
}