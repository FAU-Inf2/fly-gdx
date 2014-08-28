package de.fau.cs.mad.fly.levels.tutorials;

import com.badlogic.gdx.math.MathUtils;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * Level script file for the gate tutorial level.
 * 
 * @author Tobi
 *
 */
public class GateTutorialLevel implements ILevel, IFeatureInit, GateCircuitListener {
	
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
		InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate"), 5);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGatePassed(GateGoal gate) {
		gateCounter++;
		
		if(gateCounter < 3) {
			InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.congratulation." + MathUtils.random(1, 5)), 3);
		} else if(gateCounter == 3) {
			InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.indicator"), 5);
		} else if(gateCounter == 4) {
			InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.choose"), 5);
		} else {
			InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.finish"), 5);
		}
	}
}