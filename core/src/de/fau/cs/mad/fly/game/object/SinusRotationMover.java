package de.fau.cs.mad.fly.game.object;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Game object mover which moves and rotates the game object and its rigid body
 * in sinusoidal translation.
 * <p>
 * Movement: x = A * sin(B*x + C); x++;
 * 
 * @author Tobi
 * 
 */
public class SinusRotationMover implements IGameObjectMover {
    private GameObject gameObject;
    
    private float i = 0.0f;
    
    private Vector3 rotation = new Vector3();
    private float degrees = 0.0f;
    
    private Vector3 startPosition;
    private Vector3 moving = new Vector3();
    
    // vectors which store A, B and C for x, y and z direction.
    public Vector3 X = new Vector3();
    public Vector3 Y = new Vector3();
    public Vector3 Z = new Vector3();
    
    public SinusRotationMover(GameObject gameObject) {
        this.gameObject = gameObject;
        startPosition = new Vector3();
        gameObject.transform.getTranslation(startPosition);
    }
    
    /**
     * Setter for the rotation.
     * 
     * @param rotation
     *            The rotation vector. Becomes normalized.
     * @param degrees
     *            The degrees per frame.
     */
    public void setRotation(Vector3 rotation, float degrees) {
        this.rotation = rotation.nor();
        this.degrees = degrees;
    }
    
    /**
     * Setter for the rotation. The degrees per frame are calculated as length
     * of the vector.
     * 
     * @param rotation
     *            The rotation vector. Becomes normalized.
     */
    public void setRotation(Vector3 rotation) {
        this.degrees = rotation.len();
        this.rotation = rotation.nor();
    }
    
    @Override
    public void move(float delta) {
        // store A*sin(B*x+C) in level file for x,y,z
        moving.x = X.x * (float) Math.sin(X.y * i + X.z);
        moving.y = Y.x * (float) Math.sin(Y.y * i + Y.z);
        moving.z = Z.x * (float) Math.sin(Z.y * i + Z.z);
        
        gameObject.transform.rotate(rotation, degrees * delta * 10.0f);
        gameObject.transform.setTranslation(startPosition.add(moving));
        gameObject.getRigidBody().setWorldTransform(gameObject.transform);
        
        i += delta;
    }
    
	@Override
	public IGameObjectMover getCopy(GameObject gameObject) {
		SinusRotationMover mover = new SinusRotationMover(gameObject);
		mover.setRotation(rotation, degrees);
		mover.X = this.X;
		mover.Y = this.Y;
		mover.Z = this.Z;
		return mover;
	}
    
}