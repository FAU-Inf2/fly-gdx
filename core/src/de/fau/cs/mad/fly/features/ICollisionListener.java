package de.fau.cs.mad.fly.features;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

/**
 * Implement this interface when you want to receive collision events.
 * 
 * @author Tobias Zangl
 */
public interface ICollisionListener {	
	/**
	 * This method is called by the {@link CollisionContactListener} when a
	 * collision event is received.
	 * 
	 * @param eventType
	 * @param colObj0
	 * @param colObj1
	 */
	public void listen(int eventType, btCollisionObject colObj0, btCollisionObject colObj1);
}
