package de.fau.cs.mad.fly.levels;

import de.fau.cs.mad.fly.game.GameControllerBuilder;

/**
 * Level script file for level 11.
 * <p>
 * Uses asteroid belt, add time upgrade and instant speed upgrade.
 * 
 * @author Tobi
 *
 */
public class Level_11 implements ILevel {

	@Override
	public void create(GameControllerBuilder builder) {
		builder.addAsteroidBelt();
		
		builder.addAddTimeUpgrade();
		builder.addInstantSpeedUpgrade();
	}

}