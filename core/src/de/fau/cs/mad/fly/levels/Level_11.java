package de.fau.cs.mad.fly.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.game.FlyingObjects;
import de.fau.cs.mad.fly.features.upgrades.AddTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.InstantSpeedUpgrade;
import de.fau.cs.mad.fly.features.upgrades.LinearSpeedUpgrade;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.game.IntegerTimeListener;
import de.fau.cs.mad.fly.player.Spaceship;

/**
 * Level script file for level 11.
 * <p>
 * Uses asteroid belt, add time upgrade and instant speed upgrade.
 * 
 * @author Tobi
 *
 */
public class Level_11 implements ILevel, IntegerTimeListener, ICollisionListener<Spaceship, GameObject> {

	@Override
	public void create(GameControllerBuilder builder) {
		builder.addFeatureToLists(this);
		
		FlyingObjects asteroidBelt = new FlyingObjects("asteroid", 10, "asteroid", new Vector3(20.0f, 20.0f, 20.0f));
		builder.addFeatureToLists(asteroidBelt);
		
		InstantSpeedUpgrade instantSpeedUpgrade = new InstantSpeedUpgrade("speedUpgrade", 4.0f, 10.0f);
		builder.addFeatureToLists(instantSpeedUpgrade);
		
		LinearSpeedUpgrade linearSpeedUpgrade = new LinearSpeedUpgrade("speedUpgrade", 2.0f, 10.0f, 4.0f);
		builder.addFeatureToLists(linearSpeedUpgrade);
		
		AddTimeUpgrade addTimeUpgrade = new AddTimeUpgrade("timeUpgrade", 10.0f);
		builder.addFeatureToLists(addTimeUpgrade);
	}

	@Override
	public boolean integerTimeChanged(int newTime) {
		//Gdx.app.log("Level_11", "Time: " + newTime);
		return false;
	}

	@Override
	public void onCollision(Spaceship o1, GameObject o2) {
		// TODO Auto-generated method stub
		
	}
}