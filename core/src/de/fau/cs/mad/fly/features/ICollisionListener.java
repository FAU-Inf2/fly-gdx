package de.fau.cs.mad.fly.features;

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
	 * @param userValue0
	 * @param userValue1
	 */
	public void listen(int eventType, int userValue0, int userValue1);
}
