package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * An upgrade to change the steering of the plane.
 * 
 * @author Tobi
 *
 */
public class ChangeSteeringUpgrade extends Collectible {

	float x;
	
	float y;
	
	/**
	 * Creates a new change points upgrade.
	 */
	public ChangeSteeringUpgrade() {
		
	}
	
	@Override
	public String getType() {
		return "ChangeSteeringUpgrade";
	}
}