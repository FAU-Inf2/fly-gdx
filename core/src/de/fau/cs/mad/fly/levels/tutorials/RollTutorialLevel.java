package de.fau.cs.mad.fly.levels.tutorials;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;
import de.fau.cs.mad.fly.settings.SettingManager;

/**
 * Level script file for the gate tutorial level with only up and down movement allowed.
 * 
 * @author Tobi
 *
 */
public class RollTutorialLevel implements ILevel, IFeatureInit, GateCircuitListener {
	
	@Override
	public void create(GameControllerBuilder builder) {
		builder.addFeatureToLists(this);
	}

	@Override
	public void init(GameController game) {
		game.getFlightController().setAzimuthFactorChange(0.0f);
		
        Preferences preferences = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getSettingManager().getPreferences();
        if (preferences.getBoolean(SettingManager.USE_TOUCH)) {
        	InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.roll.touch"), 5);
        } else {
        	InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.roll.sensor"), 5);
        }
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGatePassed(GateGoal gate) {
		InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.congratulation." + MathUtils.random(1, 5)), 3);
	}
}