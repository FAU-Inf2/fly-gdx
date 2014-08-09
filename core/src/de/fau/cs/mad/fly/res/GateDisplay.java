package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

public class GateDisplay extends GameObject implements Disposable {
    private GateGoal goal = null;
    
    public GateDisplay(GameModel model) {
    	super(model, "GateDisplay");
    }
    
    public void createRigidBody(CollisionDetector collisionDetector) {        
        if (rigidBody == null) {
			btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(modelId, this);
			super.createRigidBody(modelId, displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
			
			// different scaling for the gates is buggy
			/*transform.scl(scaling);
			rigidBody.getCollisionShape().setLocalScaling(scaling);*/
        }
        collisionDetector.addRigidBody(this);
    }
    
    public void setGoal(GateGoal goal) {
    	this.goal = goal;
    }
    
    public GateGoal getGoal() {
    	return goal;
    }
    
	/**
	 * Marks the game object with a special color.
	 */
	public void mark() {
		materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
	}

	/**
	 * Unmarks the object.
	 */
	public void unmark() {
		materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));
	}
    
    @Override
    public void dispose() {
        CollisionDetector.getInstance().removeRigidBody(this);
        super.dispose();
    }
}
