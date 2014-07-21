package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.TimeController;

/**
 * 
 * @author Tobi
 *
 */
public class AddTimeUpgrade extends CollectibleObjects {
	private TimeController timeController;
	private float addedTime;

	public AddTimeUpgrade(String modelRef, float addedTime) {
		super(modelRef);
		this.addedTime = addedTime;
	}
	
	@Override
	public void init(GameController game) {
		super.init(game);
		timeController = game.getTimeController();
	}

	@Override
	protected void handleCollecting() {
		timeController.addBonusTime(addedTime);
	}

}
