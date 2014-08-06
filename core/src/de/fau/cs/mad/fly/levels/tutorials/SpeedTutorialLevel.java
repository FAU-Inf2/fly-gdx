package de.fau.cs.mad.fly.levels.tutorials;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;

/**
 * Level script file for the speed tutorial level.
 * 
 * @author Tobi
 *
 */
public class SpeedTutorialLevel implements ILevel, IFeatureInit {
	
	@Override
	public void create(GameControllerBuilder builder) {
		builder.addFeatureToLists(this);
	}

	@Override
	public void init(GameController game) {
		InfoOverlay.getInstance().setOverlay(I18n.t("tutorial.speed"), 5);
	}
}