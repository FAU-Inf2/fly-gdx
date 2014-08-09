package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

public class GateGoal extends GameObject implements Disposable {
	private GateDisplay display = null;
    private final int gateId;
    private int score;
    public int passedTimes = 0;
    public int[] successors;
    
    public GateGoal(int gateId, GameModel model, GateDisplay display) {
    	super(model, "GateGoal " + gateId);
        this.gateId = gateId;
        this.score = 50;
        this.display = display;
        dummy = true;
    }
    
    public void createRigidBody(CollisionDetector collisionDetector) {        
        if (rigidBody == null) {
			btCollisionShape goalShape = collisionDetector.getShapeManager().createBoxShape(modelId + ".goal", new Vector3(0.8f, 0.1f, 0.8f));
			super.createRigidBody(modelId + ".goal", goalShape, 0.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
			rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
			
			// different scaling for the gates is buggy
			/*transform.scl(scaling);
			rigidBody.getCollisionShape().setLocalScaling(scaling);*/
        }
        collisionDetector.addRigidBody(this);
    }
    
    public void setDisplay(GateDisplay display) {
    	this.display = display;
    }
    
    public GateDisplay getDisplay() {
    	return display;
    }
    
    public int getId() {
    	return gateId;
    }
    
    public int getScore() {
    	return score;
    }
    
	/**
	 * Marks the display game object.
	 */
	public void mark() {
		if(display != null) {
			display.mark();
		}
	}

	/**
	 * Unmarks the display game object.
	 */
	public void unmark() {
		if(display != null) {
			display.unmark();
		}
	}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            return gateId == ((GateGoal) o).hashCode();
        }
    }
    
    @Override
    public int hashCode() {
        return gateId;
    }
    
    @Override
    public String toString() {
        return "#<GateGoal " + gateId + ">";
    }
    
    @Override
    public void dispose() {
        CollisionDetector.getInstance().removeRigidBody(this);
        super.dispose();
    }
}
