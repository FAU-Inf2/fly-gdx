package de.fau.cs.mad.fly.levels;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.game.AsteroidBelt;
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
		AsteroidBelt asteroidBelt = new AsteroidBelt(10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
		builder.addFeatureToLists(asteroidBelt);
	}

}