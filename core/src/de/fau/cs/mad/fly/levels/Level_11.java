package de.fau.cs.mad.fly.levels;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.game.FlyingObjects;
import de.fau.cs.mad.fly.features.upgrades.ChangePointsUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ChangeSteeringUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ChangeTimeUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.InstantSpeedUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.ResizeGatesUpgradeHandler;
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

		builder.addFeatureToLists(new InstantSpeedUpgradeHandler());
		builder.addFeatureToLists(new ChangeTimeUpgradeHandler());
		builder.addFeatureToLists(new ChangePointsUpgradeHandler());
		builder.addFeatureToLists(new ResizeGatesUpgradeHandler());
		builder.addFeatureToLists(new ChangeSteeringUpgradeHandler());
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