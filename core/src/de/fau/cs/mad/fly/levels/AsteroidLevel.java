package de.fau.cs.mad.fly.levels;

import de.fau.cs.mad.fly.game.GameControllerBuilder;

/**
 * Level with asteroid feature used.
 * 
 * @author Tobi
 *
 */
public class AsteroidLevel implements ILevel {

	@Override
	public void create(GameControllerBuilder builder) {
		builder.addAsteroidBelt();
	}

}
