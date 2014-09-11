package de.fau.cs.mad.fly.levels.medium;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.player.gravity.ConstantGravity;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;

public class GravityLevel implements ILevel, IFeatureInit, GateCircuitListener {
    
    /**
     * Counts the amount of gates that are already reached.
     */
    private int gateCounter = 0;
    
    /**
     * The game controller.
     */
    private GameController gameController;
    
    private ConstantGravity leftGravity, rightGravity;
    
    @Override
    public void onFinished() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onGatePassed(GateGoal gate) {
        gateCounter++;
        
        if (gateCounter == 2) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("medium.gravity.firstChange"), 5);
            gameController.getPlayer().getPlane().setGravity(rightGravity);
        } else if (gateCounter == 6) {
            InfoOverlay.getInstance().setOverlay(I18n.tLevel("medium.gravity.secondChange"), 5);
            gameController.getPlayer().getPlane().setGravity(leftGravity);
        }
    }
    
    @Override
    public void init(GameController game) {
        gameController = game;
        
        rightGravity = new ConstantGravity(new Vector3(0.5f, 0.0f, 0.0f));
        leftGravity = new ConstantGravity(new Vector3(-0.5f, 0.0f, 0.0f));
    }
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
}
