package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.profile.PlayerManager;

/**
 * Used to display and handle linear increasing and decreasing speed upgrades.
 * 
 * @author Tobi
 *
 */
public class LinearSpeedUpgrade extends CollectibleObjects implements IFeatureUpdate {
	
	/**
	 * The plane which speed should be changed after a speed upgrade was collected.
	 */
	private IPlane plane;
	
	/**
	 * Determines if the speed upgrade was collected and has to be handled.
	 */
	private boolean isCollected = false;

	/**
	 * A backup of the old speed.
	 */
	private float oldSpeed;
	
	/**
	 * The speed factor of the speed upgrade increase.
	 */
	private float upgradeIncreaseFactor;
	
	/**
	 * The duration of the speed upgrade increase.
	 */
	private float upgradeIncreaseDuration;
	
	/**
	 * The speed factor of the speed upgrade decrease.
	 */
	private float upgradeDecreaseFactor;
	
	/**
	 * The duration the speed upgrade was already used.
	 */
	private float duration;
	
	/**
	 * Creates a new instant speed upgrade.
	 * 
	 * @param modelRef						The model reference for the speed upgrade model.
	 * @param upgradeIncreaseFactor			The speed factor of the speed upgrade increase.
	 * @param upgradeIncreaseDuration		The duration of the speed upgrade increase.
	 * @param upgradeDecreaseFactor			The speed factor of the speed upgrade decrease.
	 */
	public LinearSpeedUpgrade(String modelRef, float upgradeIncreaseFactor, float upgradeIncreaseDuration, float upgradeDecreaseFactor) {
		super(modelRef);
		plane = PlayerManager.getInstance().getCurrentPlayer().getPlane();
		
		this.upgradeIncreaseFactor = upgradeIncreaseFactor;
		this.upgradeIncreaseDuration = upgradeIncreaseDuration;
		this.upgradeDecreaseFactor = upgradeDecreaseFactor;
	}

	@Override
	protected void handleCollecting() {
		isCollected = true;
		
		oldSpeed = plane.getSpeed();
		duration = 0.0f;
	}

	@Override
	public void update(float delta) {
		if(!isCollected) {
			return;
		}
		
		duration += delta;
		
		if(duration <= upgradeIncreaseDuration) {
			plane.setSpeed(plane.getSpeed() * delta * upgradeIncreaseFactor);
		} else if(duration > upgradeIncreaseDuration) {
			if(plane.getSpeed() < oldSpeed) {
				plane.setSpeed(oldSpeed);
				isCollected = false;
			} else {
				plane.setSpeed(plane.getSpeed() / (delta * upgradeDecreaseFactor));
			}
		}
	}

}