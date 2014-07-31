package de.fau.cs.mad.fly.features.upgrades;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.ResizeGatesUpgrade;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.GateCircuit;

/**
 * Used to display and handle gate resize upgrades.
 * 
 * @author Tobi
 *
 */
public class ResizeGatesUpgradeHandler extends CollectibleObjects implements IFeatureInit {
	private GateCircuit gateCircuit;
	
	/**
	 * Creates an resize gates upgrade handler.
	 */
	public ResizeGatesUpgradeHandler() {
		super("ResizeGatesUpgrade");
	}

	@Override
	public void init(GameController game) {
		gateCircuit = game.getLevel().getGateCircuit();
	}

	@Override
	protected void handleCollecting(Collectible c) {
		ResizeGatesUpgrade upgrade = (ResizeGatesUpgrade) c;
		resizeGates(upgrade.getScale());
		InfoOverlay.getInstance().setOverlay(I18n.t("resizeGatesUpgradeCollected"), 3);
	}
	
	/**
	 * Resizes the gates and the gate holes with the scaling vector.
	 * @param scale		The scaling vector of the resizing of the gates.
	 */
	private void resizeGates(Vector3 scale) {
        for (Gate g : gateCircuit.allGates()) {
	    	g.display.transform.scl(scale);
	    	g.display.getRigidBody().getCollisionShape().setLocalScaling(scale);
	    	g.goal.transform.scl(scale);
	    	g.goal.getRigidBody().getCollisionShape().setLocalScaling(scale);
	    }
	}
}