package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * An upgrade to change the current points.
 * 
 * @author Tobi
 *
 */
public class ChangePointsUpgrade extends Collectible {
	/**
	 * The amount of points to add or sub if the upgrade is collected.
	 */
	int pointsChange = 0;
	
	/**
	 * Creates a new change points upgrade.
	 * @param pointsChange		The amount of points to change.
	 */
	public ChangePointsUpgrade(int pointsChange) {
		this.pointsChange = pointsChange;
	}
	
	/**
	 * Getter for the points change.
	 * @return the points change.
	 */
	public int getPointsChange() {
		return pointsChange;
	}
	
	@Override
	public String getType() {
		return "ChangePointsUpgrade";
	}
}