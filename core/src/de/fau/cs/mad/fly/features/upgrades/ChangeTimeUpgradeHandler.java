package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.TimeController;

/**
 * Used to display and handle change time upgrades.
 * 
 * @author Tobi
 *
 */
public class ChangeTimeUpgradeHandler extends CollectibleObjects implements IFeatureInit {
	/**
	 * Time controller to add the time to.
	 */
	private TimeController timeController;

	/**
	 * Creates a new change time upgrade handler.
	 * 
	 * @param modelRef		The model reference.
	 */
	public ChangeTimeUpgradeHandler() {
		super("ChangeTimeUpgrade");
	}
	
	@Override
	public void init(GameController game) {
		timeController = game.getTimeController();
	}

	@Override
	protected void handleCollecting(Collectible c) {		
		ChangeTimeUpgrade upgrade = (ChangeTimeUpgrade) c;
		timeController.addBonusTime(upgrade.getTimeChange());
		InfoOverlay.getInstance().setOverlay(I18n.t("timeUpgradeCollected") + "\n" + I18n.t("bonus") + " " + (upgrade.getTimeChange()) + "s", 3);
	}

}