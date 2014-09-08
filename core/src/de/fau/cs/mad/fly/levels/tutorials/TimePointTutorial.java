package de.fau.cs.mad.fly.levels.tutorials;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * Level script file for the tutorial to explain movement in both directions,
 * points and remaining time.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class TimePointTutorial implements ILevel, IFeatureInit, GateCircuitListener {
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
        InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.navigate.both.directions"), 4);
    }
    
    @Override
    public void onFinished() {
        // do nothing
    }
    
    @Override
    public void onGatePassed(GateGoal gate) {
        if(gate.getGateId() == 0) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.points"), 3);
        }
        else if (gate.getGateId() == 2) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("tutorial.remaining.time"), 3);
        }
    }
}