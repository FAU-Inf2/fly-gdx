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
 * In this level all gates are 10% smaller.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class SmallerGatesLevel implements ILevel, IFeatureInit {
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
        Vector3 scale = new Vector3(.6f, .6f, .6f);
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
        
        InfoOverlay.getInstance().setOverlay(I18n.tLevel("gates.smaller"), 5);
    }
    
}