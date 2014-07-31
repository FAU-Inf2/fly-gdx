package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeSteeringUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Used to display and handle change steering upgrades.
 * 
 * @author Tobi
 *
 */
public class ChangeSteeringUpgradeHandler extends CollectibleObjects implements IFeatureInit, IFeatureUpdate {
	/**
	 * The flight controller.
	 */
	private FlightController flightController;
	
	/**
	 * Determines if the speed upgrade was collected and has to be handled.
	 */
	private boolean isCollected = false;

	/**
	 * Determines if the speed upgrade change is forever.
	 */
	private boolean isInfinite = false;
	
	/**
	 * The duration the steering change upgrade was already used.
	 */
	private float duration;
	
	/**
	 * Creates a new instant speed upgrade handler.
	 */
	public ChangeSteeringUpgradeHandler() {
		super("ChangeSteeringUpgrade");
	}
	
	@Override
	public void init(GameController game) {
		flightController = game.getFlightController();
	}

	@Override
	protected void handleCollecting(Collectible c) {
		ChangeSteeringUpgrade upgrade = (ChangeSteeringUpgrade) c;
		isCollected = true;

		flightController.setRollFactorChange(upgrade.getRoll());
		flightController.setAzimuthFactorChange(upgrade.getAzimuth());
		duration = upgrade.getDuration();
		if(upgrade.getDuration() <= 0.0f) {
			isInfinite = true;
		}
		
		InfoOverlay.getInstance().setOverlay(I18n.t("changeSteeringUpgradeCollected"), 3);
	}

	@Override
	public void update(float delta) {
		if(!isCollected) {
			return;
		}
		
		duration -= delta;
		
		if(!isInfinite && duration <= 0.0) {
			flightController.resetFactorChange();
			isCollected = false;
		}
	}
}