package de.fau.cs.mad.fly.features;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Implement this interface when you want to receive collision events.
 * 
 * @author Tobias Zangl
 */
public interface ICollisionListener {	
	/**
	 * This method is called by the {@link de.fau.cs.mad.fly.game.CollisionDetector.CollisionContactListener} when a
	 * collision event is received.
	 */
	public void onCollision(GameObject o1, GameObject o2);
}
