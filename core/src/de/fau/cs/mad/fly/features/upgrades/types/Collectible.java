package de.fau.cs.mad.fly.features.upgrades.types;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Abstract class for any object the user can collect in the level.
 *  
 * @author Tobi
 *
 */
public abstract class Collectible {
	/**
	 * The game object to display the collectible.
	 */
	private GameObject gameObject;
	
	/**
	 * Setter for the game object to display the collectible.
	 * @param the game object.
	 */
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	/**
	 * Getter for the game object to display the collectible.
	 * @return the game object.
	 */
	public GameObject getGameObject() {
		return gameObject;
	}

	/**
	 * Getter for the type of the collectible.
	 * @return type of the collectible.
	 */
	public abstract String getType();
}