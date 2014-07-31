package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * An upgrade to change the current speed of the plane.
 * 
 * @author Tobi
 *
 */
public class InstantSpeedUpgrade extends Collectible {
	/**
	 * The speed factor of the speed upgrade.
	 */
	private float speedFactor;
	
	/**
	 * The duration of the speed upgrade.
	 */
	private float duration;
	
	/**
	 * Creates a new instant speed upgrade.
	 * @param speedFactor		The speed factor of the upgrade.
	 * @param duration			The duration of the upgrade. A value <= 0.0f means infinite duration.
	 */
	public InstantSpeedUpgrade(float speedFactor, float duration) {
		this.speedFactor = speedFactor;
		this.duration = duration;
	}
	
	/**
	 * Getter for the speed factor.
	 * @return the speed factor.
	 */
	public float getSpeedFactor() {
		return speedFactor;
	}
	
	/**
	 * Getter for the duration.
	 * @return the duration.
	 */
	public float getDuration() {
		return duration;
	}
	
	@Override
	public String getType() {
		return "InstantSpeedUpgrade";
	}
}