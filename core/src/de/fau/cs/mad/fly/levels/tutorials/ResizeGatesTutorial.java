package de.fau.cs.mad.fly.levels.tutorials;

import java.util.List;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.res.Gate;

/**
 * Level script file for the resize gates tutorial.
 * 
 * @author Tobi
 *
 */
public class ResizeGatesTutorial implements ILevel, IFeatureInit, ICollisionListener<Spaceship, Collectible> {
	
	@Override
	public void create(GameControllerBuilder builder) {
		builder.addFeatureToLists(this);
	}

	@Override
	public void init(GameController game) {
		game.getLevel();
		
		Vector3 scale = new Vector3(0.4f, 0.4f, 0.4f);
		Vector3 scaling = new Vector3();
        for (Gate g : game.getLevel().getGateCircuit().allGates()) {
	    	g.display.transform.scl(scale);
	    	g.display.transform.getScale(scaling);
	    	g.display.getRigidBody().getCollisionShape().setLocalScaling(scaling);
	    	g.goal.transform.scl(scale);
	    	g.goal.transform.getScale(scaling);
	    	g.goal.getRigidBody().getCollisionShape().setLocalScaling(scaling);
	    }
		
		InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.resize"), 5);
	}
	

	@Override
	public void onCollision(Spaceship o1, Collectible o2) {
		List<GameObject> objects = GameController.getInstance().getLevel().components;
		for(GameObject object : objects) {
			if(object.id.equals("IndicatorArrow")) {
				object.hide();
				return;
			}
		}
	}
}