package de.fau.cs.mad.fly.levels;

import de.fau.cs.mad.fly.game.GameControllerBuilder;

/**
 * Default level script class without any additional features.
 * <p>
 * Is used if no other level script class is defined or the defined one is not
 * implemented.
 * 
 * @author Tobi
 * 
 */
public class DefaultLevel implements ILevel {
    
    @Override
    public void create(GameControllerBuilder builder) {
        // no extra features needed in the default level.
    }
    
}