package de.fau.cs.mad.fly.levels;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.game.FlyingObjects;
import de.fau.cs.mad.fly.game.GameControllerBuilder;

/**
 * Level with asteroid feature used.
 * 
 * @author Tobi
 * 
 */
public class AsteroidBelt implements ILevel {
    
    @Override
    public void create(GameControllerBuilder builder) {
        FlyingObjects asteroidBelt = new FlyingObjects("asteroid", 10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
        builder.addFeatureToLists(asteroidBelt);
    }
    
}