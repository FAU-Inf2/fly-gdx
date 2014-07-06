package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Motion state class to update the game object transform matrix if the rigid body transform matrix is changed and the other way around.
 * 
 * @author Tobi
 */
public class GameObjectMotionState extends btMotionState {
	public Matrix4 transform;

	@Override
	public void getWorldTransform (Matrix4 worldTrans) {
		worldTrans.set(transform);
	}

	@Override
	public void setWorldTransform (Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
}