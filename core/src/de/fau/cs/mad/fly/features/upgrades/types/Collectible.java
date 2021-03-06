package de.fau.cs.mad.fly.features.upgrades.types;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Abstract class for any object the user can collect in the level.
 * 
 * @author Tobi
 * 
 */
public class Collectible extends GameObject {
    
    /**
     * Creates a new collectible.
     * 
     * @param model
     *            The model of the upgrade.
     */
    public Collectible(GameModel model) {
        // TODO: use a proper string for the creation of the GameObject
        super(model, "");
    }
    
    /**
     * Creates the rigid body for the collectible.
     * 
     * @param collisionDetector
     *            The collision detector.
     * @param type
     *            The collectible type for the shape.
     */
    public void createShapeAndRigidBody(CollisionDetector collisionDetector, String type) {
        btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(type, this);
        createRigidBody(type, shape, 1.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
        getRigidBody().setCollisionFlags(getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        CollisionDetector.getInstance().addRigidBody(this);
        setDummy(true);
    }
    
    /**
     * Getter for the type of the collectible.
     * 
     * @return type of the collectible.
     */
    public String getType() {
        return "Collectible";
    }
}