package de.fau.cs.mad.fly.levels.medium;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * This level starts with a hint to search for the gate resize upgrade.
 * Furthermore it minimizes some gates of the prefered way.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class BestWaySmallGatesLevel implements ILevel, IFeatureInit {
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
        
        Vector3 scale = new Vector3(.3f, .3f, .3f);
        Vector3 scaling = new Vector3();
        for (GateGoal g : game.getLevel().getGateCircuit().allGateGoals()) {
            if (g.getDisplay() != null) {
                g.getDisplay().transform.scl(scale);
                g.getDisplay().transform.getScale(scaling);
                g.getDisplay().getRigidBody().getCollisionShape().setLocalScaling(scaling);
            }
            g.transform.scl(scale);
            g.transform.getScale(scaling);
            g.getRigidBody().getCollisionShape().setLocalScaling(scaling);
        }
        
        InfoOverlay.getInstance().setOverlay(I18n.tLevel("medium.search.gate.resize.upgrade"), 5);
    }
    
}