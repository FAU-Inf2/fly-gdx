package de.fau.cs.mad.fly.levels.hard;

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
 * Level script file for the first level with rotating gates.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class GatesTurningLevel implements ILevel, IFeatureInit, GateCircuitListener {
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
        InfoOverlay.getInstance().setOverlay(I18n.tLevel("hard.choose.right.spaceship"), 5);
    }
    
    @Override
    public void onGatePassed(GateGoal gate) {
        if (gate.getGateId() == 1) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.gate.choose"), 3);
        } 
    }

    @Override
    public void onFinished() {
        // nothing to do
    }
    
}