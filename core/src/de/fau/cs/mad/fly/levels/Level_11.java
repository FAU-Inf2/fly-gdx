package de.fau.cs.mad.fly.levels;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.game.AsteroidBelt;
import de.fau.cs.mad.fly.features.upgrades.AddTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.InstantSpeedUpgrade;
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
		AsteroidBelt asteroidBelt = new AsteroidBelt(10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
		builder.addFeatureToLists(asteroidBelt);
		
		InstantSpeedUpgrade instantSpeedUpgrade = new InstantSpeedUpgrade("speedUpgrade", 4.0f, 10.0f);
		builder.addFeatureToLists(instantSpeedUpgrade);
		
		AddTimeUpgrade addTimeUpgrade = new AddTimeUpgrade("timeUpgrade", 10.0f);
		builder.addFeatureToLists(addTimeUpgrade);
	}
}