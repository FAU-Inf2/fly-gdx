package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to receive collision events.
 * 
 * @author Tobias Zangl
 */
public interface ICollisionListener<T, P> {
	/**
	 * This method is called by the {@link de.fau.cs.mad.fly.game.CollisionDetector.CollisionContactListener} when a
	 * collision event is received.
	 */
	public void onCollision(T o1, P o2);
}

