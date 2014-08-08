package de.fau.cs.mad.fly.levels.tutorials;

import java.util.List;

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
import de.fau.cs.mad.fly.res.EventListener;
import de.fau.cs.mad.fly.res.Gate;

/**
 * Level script file for the gate tutorial level.
 * 
 * @author Tobi
 *
 */
public class GateTutorialLevel implements ILevel, IFeatureInit, EventListener {
	
	/**
	 * Counts the amount of gates that are already reached.
	 */
	private int gateCounter = 0;
	
	
	@Override
	public void create(GameControllerBuilder builder) {
		builder.addFeatureToLists(this);
	}

	@Override
	public void init(GameController game) {
		InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.gate"), 5);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGatePassed(Gate gate) {
		gateCounter++;
		
		if(gateCounter < 3) {
			InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.gate"), 5);
			// fly to next gate
		} else if(gateCounter == 3) {
			InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.gate"), 5);
			// look for indicator
		} else if(gateCounter == 4) {
			InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.gate"), 5);
			// fly through one of the 2 gates
		} else {
			InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.gate"), 5);
			// fly to last gate
		}
	}
}