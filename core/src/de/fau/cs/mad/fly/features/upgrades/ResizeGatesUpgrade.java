package de.fau.cs.mad.fly.features.upgrades;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.GateCircuit;
import de.fau.cs.mad.fly.res.Level;

/**
 * Used to display and handle gate resize upgrades.
 * 
 * @author Tobi
 *
 */
public class ResizeGatesUpgrade extends CollectibleObjects implements IFeatureInit {
	private GateCircuit gateCircuit;
	
	private Vector3 scale;
	
	/**
	 * Creates an resize gates upgrade with a scaling vector.
	 * 
	 * @param modelRef		The model reference.
	 * @param scale			The scale vector to use.
	 */
	public ResizeGatesUpgrade(String modelRef, Vector3 scale) {
		super("resizeGatesUpgrade", modelRef);
		this.scale = scale;
	}
	
	/**
	 * Creates an resize gates upgrade with a uniform scaling factor.
	 * 
	 * @param modelRef		The model reference.
	 * @param scale			The uniform scale value to use.
	 */
	public ResizeGatesUpgrade(String modelRef, float scale) {
		super("resizeGatesUpgrade", modelRef);
		this.scale = new Vector3(scale, scale, scale);
	}

	@Override
	public void init(GameController game) {
		gateCircuit = game.getLevel().getGateCircuit();
	}

	@Override
	protected void handleCollecting() {
		resizeGates();
		InfoOverlay.getInstance().setOverlay(I18n.t("resizeGatesUpgradeCollected"), 3);
	}
	
	/**
	 * Resizes the gates and the gate holes with the scaling vector.
	 */
	private void resizeGates() {
        for (Gate g : gateCircuit.allGates()) {
	    	g.display.transform.scl(scale);
	    	g.display.getRigidBody().getCollisionShape().setLocalScaling(scale);
	    	
	    	g.goal.transform.scl(scale);
	    	g.goal.getRigidBody().getCollisionShape().setLocalScaling(scale);
	    }
	}
}