package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.features.game.CollectibleObjects;

/**
 * 
 * @author Tobi
 *
 */
public class AddTimeUpgrade extends CollectibleObjects {
	private int addedTime;

	public AddTimeUpgrade(String modelRef, int addedTime) {
		super(modelRef);
		this.addedTime = addedTime;
	}

	@Override
	protected void handleCollecting() {
		//+= addedTime;
	}

}
