package de.fau.cs.mad.fly.features;

import de.fau.cs.mad.fly.game.GameController;

/**
 * Implement this interface when you want to implement a feature that has to load anything when the level is loading.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureLoad {
	public void load(final GameController game);
}
