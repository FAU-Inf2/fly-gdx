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
	private IGameObjectMover nextMover = new EmptyMover();
	
	private Vector3 startPosition;
	private Matrix4 transform;
	
	// vectors which store A, B and C for x, y and z direction.
	private Vector3 X, Y, Z;
	
	public SinusMover(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	/**
	 * Setter for the next mover.
	 * @param nextMover		The next mover which should be called after this mover.
	 */
	public void setNextMover(IGameObjectMover nextMover) {
		this.nextMover = nextMover;
	}

	@Override
	public void move(float delta) {
		
		//nextMover.move(delta);
	}

}