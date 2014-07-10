package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to receive events if a collectible object was collected.
 * 
 * @author Tobias Zangl
 */
public interface ICollectListener {
	/**
	 * Called whenever a collectible objects was collected by the player.
	 * @param collectibleType	The type of the collected object.
	 */
	public void onCollect(String collectibleType);
}