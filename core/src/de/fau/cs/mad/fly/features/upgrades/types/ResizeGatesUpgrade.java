package de.fau.cs.mad.fly.features.upgrades.types;

import com.badlogic.gdx.math.Vector3;

/**
 * An upgrade to resize the gates.
 * 
 * @author Tobi
 *
 */
public class ResizeGatesUpgrade extends Collectible {
	/**
	 * The scaling vector.
	 */
	private Vector3 scale;
	
	/**
	 * Creates a new resize gates upgrade.
	 * @param scale			The scaling vector.
	 */
	public ResizeGatesUpgrade(Vector3 scale) {
		this.scale = scale;
	}
	
	/**
	 * Getter for the scaling vector.
	 * @return the scaling vector.
	 */
	public Vector3 getScale() {
		return scale;
	}
	
	@Override
	public String getType() {
		return "ResizeGatesUpgrade";
	}
}