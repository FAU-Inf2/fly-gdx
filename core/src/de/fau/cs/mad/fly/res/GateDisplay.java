package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Displays the visible part of the gate.
 * 
 * @author Tobi
 * 
 */
public class GateDisplay extends GameObject implements Disposable {
    private GateGoal goal = null;
    public Vector3 gatePosition = new Vector3();
    
    /**
     * Creates a new gate display.
     * 
     * @param model
     *            The model of the gate display.
     */
    public GateDisplay(GameModel model, String type) {
        super(model, type);
    }
    
    /**
     * Creates the rigid body of the gate display if its not already created.
     * 
     * @param collisionDetector
     */
    public void createRigidBody(CollisionDetector collisionDetector) {
        if (rigidBody == null) {
            btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(super.getModelId(), this);
            super.createRigidBody(super.getModelId(), displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
            
            // different scaling for the gates is buggy
            /*
             * transform.scl(scaling);
             * rigidBody.getCollisionShape().setLocalScaling(scaling);
             */
            
            transform.getTranslation(gatePosition);
        }
        collisionDetector.addRigidBody(this);
    }
    
    /**
     * Setter for the gate goal for this display.
     * 
     * @param goal
     *            The gate goal.
     */
    public void setGoal(GateGoal goal) {
        this.goal = goal;
    }
    
    /**
     * Getter for the gate goal for this display.
     * 
     * @return goal
     */
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
