package de.fau.cs.mad.fly.levels;

import de.fau.cs.mad.fly.game.GameControllerBuilder;

/**
 * Interface for the level script files.
 * 
 * @author Tobi
 *
 */
public interface ILevel {
	
	/**
	 * Used to add the features to the game controller builder.
	 * 
	 * @param builder			The game controller builder to add the features to.
	 */
	public void create(GameControllerBuilder builder);
	
}