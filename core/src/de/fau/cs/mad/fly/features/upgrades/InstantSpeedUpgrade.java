package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlayerManager;

/**
 * Used to display and handle instant speed upgrades.
 * 
 * @author Tobi
 *
 */
public class InstantSpeedUpgrade extends CollectibleObjects implements IFeatureUpdate {
	
	/**
	 * The plane which speed should be changed after a speed upgrade was collected.
	 */
	private IPlane plane;
	
	/**
	 * Determines if the speed upgrade was collected and has to be handled.
	 */
	private boolean isCollected = false;

	/**
	 * Determines if the speed upgrade change is forever.
	 */
	private boolean isInfinite = false;

	/**
	 * A backup of the old speed.
	 */
	private float oldSpeed;
	
	/**
	 * The speed factor of the speed upgrade.
	 */
	private float upgradeSpeedFactor;
	
	/**
	 * The duration of the speed upgrade.
	 */
	private float upgradeDuration;
	
	/**
	 * The duration the speed upgrade was already used.
	 */
	private float duration;
	
	/**
	 * Creates a new instant speed upgrade.
	 * 
	 * @param modelRef					The model reference for the speed upgrade model.
	 * @param upgradeSpeedFactor		The speed factor of the speed upgrade.
	 * @param upgradeDuration			The duration of the speed upgrade. A value <= 0.0f means infinite duration.
	 */
	public InstantSpeedUpgrade(String modelRef, float upgradeSpeedFactor, float upgradeDuration) {
		super(modelRef);
		plane = PlayerManager.getInstance().getCurrentPlayer().getPlane();
		
		this.upgradeSpeedFactor = upgradeSpeedFactor;
		this.upgradeDuration = upgradeDuration;
		if(upgradeDuration <= 0.0f) {
			isInfinite = true;
		}
	}

	@Override
	protected void handleCollecting() {
		isCollected = true;
		
		oldSpeed = plane.getSpeed();
		plane.setSpeed(oldSpeed * upgradeSpeedFactor);
		duration = 0.0f;
	}

	@Override
	public void update(float delta) {
		if(!isCollected) {
			return;
		}
		
		duration += delta;
		
		if(!isInfinite && duration >= upgradeDuration) {
			plane.setSpeed(plane.getSpeed() / upgradeSpeedFactor);
			isCollected = false;
		}
	}

}