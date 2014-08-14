package de.fau.cs.mad.fly.game.object;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Game object mover which moves the game object and its rigid body in sinusoidal translation.
 * <p>
 * x = A * sin(B*x + C);
 * x++;
 * 
 * @author Tobi
 *
 */
public class SinusMover implements IGameObjectMover {
	private GameObject gameObject;
	
	private float i = 0.0f;
	
	private Vector3 startPosition;
	private Vector3 moving = new Vector3();
	
	// vectors which store A, B and C for x, y and z direction.
	public Vector3 X = new Vector3();
	public Vector3 Y = new Vector3();
	public Vector3 Z = new Vector3();
	
	public SinusMover(GameObject gameObject) {
		this.gameObject = gameObject;
		startPosition = new Vector3();
		gameObject.transform.getTranslation(startPosition);
	}

	@Override
	public void move(float delta) {
    	// store A*sin(B*x+C) in level file for x,y,z
		moving.x = X.x * (float) Math.sin(X.y * i + X.z) * 0.1f;
		moving.y = Y.x * (float) Math.sin(Y.y * i + Y.z) * 0.1f;
		moving.z = Z.x * (float) Math.sin(Z.y * i + Z.z) * 0.1f;

    	gameObject.transform.setTranslation(startPosition.add(moving));
		gameObject.getRigidBody().setWorldTransform(gameObject.transform);
		
		i += delta;
	}

}