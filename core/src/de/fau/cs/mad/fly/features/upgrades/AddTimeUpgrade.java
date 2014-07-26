package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.TimeController;

/**
 * Used to display and handle add time upgrades.
 * 
 * @author Tobi
 *
 */
public class AddTimeUpgrade extends CollectibleObjects implements IFeatureInit {
	/**
	 * Time controller to add the time to.
	 */
	private TimeController timeController;
	
	/**
	 * The amount of time to add.
	 */
	private float addedTime;

	/**
	 * Creates an add time upgrade with specific model for the upgrade display and specific time which is added to the current time used by the player.
	 * 
	 * @param modelRef		The model reference.
	 * @param addedTime		The time to add in seconds.
	 */
	public AddTimeUpgrade(String modelRef, float addedTime) {
		super("addTimeUpgrade", modelRef);
		this.addedTime = addedTime;
	}
	
	@Override
	public void init(GameController game) {
		timeController = game.getTimeController();
	}

	@Override
	protected void handleCollecting() {
		timeController.addBonusTime(addedTime);
		InfoOverlay.getInstance().setOverlay(I18n.t("timeUpgradeCollected") + "\n" + I18n.t("bonus") + " " + ((int) addedTime) + "s.", 3);
	}

}