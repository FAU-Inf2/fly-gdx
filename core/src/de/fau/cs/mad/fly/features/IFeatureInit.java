package de.fau.cs.mad.fly.features;

import de.fau.cs.mad.fly.game.GameController;

/**
 * Implement this interface when you want to implement a feature that has to initialize anything before the level starts.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureInit {
	/**
	 * Called at the moment the level starts.
	 * @param game		The game controller.
	 */
	public void init(final GameController game);
}
