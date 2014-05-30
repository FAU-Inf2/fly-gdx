package de.fau.cs.mad.fly.features;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.res.Gate;

/**
 * Implement this interface when you want to be noticed when a gate is passed.
 * 
 * @author Lukas Hahmann
 * 
 */
public interface IFeatureGatePassed {
	/**
	 * This method is called by the {@link GameController} when a gate is
	 * passed.
	 * 
	 * @param gameController
	 */
	public void gatePassed(Gate passedGate);
}
