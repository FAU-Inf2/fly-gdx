package de.fau.cs.mad.fly.levels.beginner;

import com.badlogic.gdx.math.MathUtils;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * Level script file for the level where you have to decide the first time which
 * gate to use.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class SeveralGatesLevel implements ILevel, IFeatureInit, GateCircuitListener {
    
    private int gatesPassed = 0;
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
        InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.choose"), 5);
    }
    
    @Override
    public void onFinished() {
        // do nothing
    }
    
    @Override
    public void onGatePassed(GateGoal gate) {
        gatesPassed++;
        if (gatesPassed == 3) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.choose"), 3);
        } else if (gatesPassed < 5) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.congratulation." + MathUtils.random(1, 5)), 3);
        }
    }
}