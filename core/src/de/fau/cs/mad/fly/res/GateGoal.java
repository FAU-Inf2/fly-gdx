package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Manages the invisible part of the gate.
 * 
 * @author Tobi
 *
 */
public class GateGoal extends GameObject implements Disposable {
	private GateDisplay display = null;
    private final int gateId;
    private int score;
    public int passedTimes = 0;
    public int[] successors;
    
    /**
     * Creates a new gate goal.
     * @param gateId		The gate id.
     * @param model			The model of the goal. It is usually not visible.
     * @param display		The displayed part of the goal.
     */
    public GateGoal(int gateId, GameModel model, GateDisplay display) {
    	super(model, "GateGoal " + gateId);
        this.gateId = gateId;
        this.score = 50;
        this.display = display;
        dummy = true;
    }
    
    /**
     * Creates the rigid body of the gate goal if its not already created.
     * @param collisionDetector
     */
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
    
    /**
     * Setter for the gate display for this goal.
     * @param display		The gate display.
     */
    public void setDisplay(GateDisplay display) {
    	this.display = display;
    }
    
    /**
     * Getter for the gate display for this goal.
     * @return display
     */
    public GateDisplay getDisplay() {
    	return display;
    }
    
    /**
     * Getter for the id of this gate.
     * @return gateId
     */
    public int getId() {
    	return gateId;
    }
    
    /**
     * Getter for the score of this gate.
     * @return score
     */
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
