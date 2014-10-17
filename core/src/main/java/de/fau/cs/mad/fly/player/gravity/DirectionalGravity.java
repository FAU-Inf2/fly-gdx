package de.fau.cs.mad.fly.player.gravity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Applies directional gravity to a specific point in the 3D world.
 * 
 * @author Tobi
 * 
 */
public class DirectionalGravity implements IGravity {
    private Vector3 distance = new Vector3();
    
    private Vector3 position;
    private float strength;
    
    public DirectionalGravity(Vector3 position, float strength) {
        this.setPosition(position);
        this.setStrength(strength);
    }
    
    @Override
    public void applyGravity(Matrix4 transform, Vector3 movement) {
        transform.getTranslation(distance);
        distance.sub(position).nor().scl(strength);
        movement.add(distance);
    }
    
    /**
     * Getter for the strength of the gravity.
     * 
     * @return strength
     */
    public float getStrength() {
        return strength;
    }
    
    /**
     * Setter for the strength of the gravity.
     * 
     * @param strength
     *            The new strength.
     */
    public void setStrength(float strength) {
        this.strength = strength;
    }
    
    /**
     * Getter for the position of the gravity center.
     * 
     * @return position
     */
    public Vector3 getPosition() {
        return position;
    }
    
    /**
     * Setter for the position of the gravity center.
     * 
     * @param position
     *            The new position.
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
}