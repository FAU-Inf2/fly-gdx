package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * An upgrade to change the steering of the plane.
 * 
 * @author Tobi
 *
 */
public class ChangeSteeringUpgrade extends Collectible {
	/**
	 * Roll factor change.
	 */
	private float roll;
	
	/**
	 * Azimuth factor change.
	 */
	private float azimuth;
	
	/**
	 * The duration of the upgrade.
	 */
	private float duration;
	
	/**
	 * Creates a new change steering upgrade.
	 * @param roll			The roll change of the upgrade.
	 * @param azimuth		The azimuth change of the upgrade.
	 * @param duration		The duration of the upgrade.
	 */
	public ChangeSteeringUpgrade(float roll, float azimuth, float duration) {
		this.roll = roll;
		this.azimuth = azimuth;
		this.duration = duration;
	}
	
	/**
	 * Getter for the roll change.
	 * @return roll
	 */
	public float getRoll() {
		return roll;
	}
	
	/**
	 * Getter for the azimuth change.
	 * @return azimuth
	 */
	public float getAzimuth() {
		return azimuth;
	}
	
	/**
	 * Getter for the duration of the upgrade.
	 * @return duration
	 */
	public float getDuration() {
		return duration;
	}
	
	@Override
	public String getType() {
		return "ChangeSteeringUpgrade";
	}
}