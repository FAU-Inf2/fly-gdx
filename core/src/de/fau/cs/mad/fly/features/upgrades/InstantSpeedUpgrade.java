package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.IPlane;

/**
 * Used to display and handle instant speed upgrades.
 * 
 * @author Tobi
 *
 */
public class InstantSpeedUpgrade extends CollectibleObjects implements IFeatureInit, IFeatureUpdate {
	
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
		super("instantSpeedUpgrade", modelRef);
		
		this.upgradeSpeedFactor = upgradeSpeedFactor;
		this.upgradeDuration = upgradeDuration;
		if(upgradeDuration <= 0.0f) {
			isInfinite = true;
		}
	}
	
	@Override
	public void init(GameController game) {
		plane = game.getPlayer().getPlane();
	}

	@Override
	protected void handleCollecting() {
		isCollected = true;
		
		oldSpeed = plane.getSpeed();
		plane.setSpeed(oldSpeed * upgradeSpeedFactor);
		duration = 0.0f;
		
		InfoOverlay.getInstance().setOverlay(I18n.t("speedUpgradeCollected") + "\n" + I18n.t("bonus") + " " + ((int) (upgradeSpeedFactor * 100.0f)+ "% " + I18n.t("for") + " " + (int) upgradeDuration) + "s.", 3);
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