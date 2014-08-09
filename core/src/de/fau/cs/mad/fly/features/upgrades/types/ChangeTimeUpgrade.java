package de.fau.cs.mad.fly.features.upgrades.types;

import de.fau.cs.mad.fly.game.GameModel;

/**
 * An upgrade to change the current time.
 * 
 * @author Tobi
 *
 */
public class ChangeTimeUpgrade extends Collectible {
	/**
	 * The amount of time to add or sub if the upgrade is collected.
	 */
	int timeChange = 0;
	
	/**
	 * Creates a new change time upgrade.
	 * @param model				The model of the upgrade.
	 * @param timeChange		The amount of time to change.
	 */
	public ChangeTimeUpgrade(GameModel model, int timeChange) {
		super(model);
		this.timeChange = timeChange;
	}
	
	/**
	 * Getter for the time change.
	 * @return the time change.
	 */
	public int getTimeChange() {
		return timeChange;
	}
	
	@Override
	public String getType() {
		return "ChangeTimeUpgrade";
	}
}