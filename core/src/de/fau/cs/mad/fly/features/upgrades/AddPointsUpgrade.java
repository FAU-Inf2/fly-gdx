package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Used to display and handle add point upgrades.
 * 
 * @author Tobi
 *
 */
public class AddPointsUpgrade extends CollectibleObjects {
	
	/**
	 * The amount of points to add.
	 */
	private int addedPoints;

	/**
	 * Creates an add point upgrade with specific model for the upgrade display and specific points which are added to the players points.
	 * 
	 * @param modelRef		The model reference.
	 * @param addedTime		The points to add.
	 */
	public AddPointsUpgrade(String modelRef, int addedPoints) {
		super("addPointsUpgrade", modelRef);
		this.addedPoints = addedPoints;
	}

	@Override
	protected void handleCollecting() {
		GameController.getInstance().getPlayer().addBonusPoints(addedPoints);
		InfoOverlay.getInstance().setOverlay(I18n.t("pointUpgradeCollected") + "\n" + I18n.t("bonus") + " " + addedPoints + "", 3);
	}

}