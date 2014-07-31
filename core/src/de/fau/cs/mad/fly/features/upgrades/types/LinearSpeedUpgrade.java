package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * An upgrade to change the current speed of the plane.
 * 
 * @author Tobi
 *
 */
public class LinearSpeedUpgrade extends Collectible {
	/**
	 * The speed factor of the speed upgrade increase.
	 */
	private float increaseFactor;
	
	/**
	 * The duration of the speed upgrade increase.
	 */
	private float increaseDuration;
	
	/**
	 * The speed factor of the speed upgrade decrease.
	 */
	private float decreaseFactor;
	
	/**
	 * Creates a new linear speed upgrade handler.
	 * @param upgradeIncreaseFactor			The speed factor of the speed upgrade increase.
	 * @param upgradeIncreaseDuration		The duration of the speed upgrade increase.
	 * @param upgradeDecreaseFactor			The speed factor of the speed upgrade decrease.
	 */
	public LinearSpeedUpgrade(float increaseFactor, float increaseDuration, float decreaseFactor) {
		this.increaseFactor = increaseFactor;
		this.increaseDuration = increaseDuration;
		this.decreaseFactor = decreaseFactor;
	}
	
	/**
	 * Getter for the increase factor.
	 * @return the increase factor.
	 */
	public float getIncreaseFactor() {
		return increaseFactor;
	}
	
	/**
	 * Getter for the increase duration.
	 * @return the increase duration.
	 */
	public float getIncreaseDuration() {
		return increaseDuration;
	}
	
	/**
	 * Getter for the decrease factor.
	 * @return the decrease factor.
	 */
	public float getDecreaseFactor() {
		return decreaseFactor;
	}
	
	@Override
	public String getType() {
		return "LinearSpeedUpgrade";
	}
}