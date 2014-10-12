package de.fau.cs.mad.fly.levels.medium;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.levels.ILevel;
import de.fau.cs.mad.fly.res.GateCircuitListener;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * Level script file for the choose right gate level.
 * 
 * @author Tobi
 * 
 */
public class ChooseRightGateLevel implements ILevel, IFeatureInit, GateCircuitListener {
	
	private List<GameObject> components;
    
    @Override
    public void create(GameControllerBuilder builder) {
        builder.addFeatureToLists(this);
    }
    
    @Override
    public void init(GameController game) {
    	components = game.getLevel().components;
        for(GameObject o : components) {
        	if(o.getMover() != null) {
        		System.out.println(o.getId());
        		o.getMover().setActive(false);
        	}
        }
    }
    
    /**
     * Activates the mover of the asteroid with the given id.
     * 
     * @param id		The id of the asteroid.
     */
    private void activateAsteroid(String id) {
        for(GameObject o : components) {
        	if(o.getId().equals(id)) {
        		o.getMover().setActive(true);
        		return;
        	}
        }
    }
    
    @Override
    public void onGatePassed(GateGoal gate) {
    	System.out.println(gate.getId());
        if(gate.getId().equals("GateGoal 0")) {
        	activateAsteroid("Cube.000");
        } else if(gate.getId().equals("GateGoal 3")) {
        	activateAsteroid("Cube.001");
        } else if(gate.getId().equals("GateGoal 7")) {
        	activateAsteroid("Cube.002");
        }
    }

	@Override
	public void onFinished() {
		// do nothing
	}
}