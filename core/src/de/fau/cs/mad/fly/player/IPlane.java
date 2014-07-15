package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Interface that has to implemented by everything that a user can steer in Fly.
 * 
 * @author Lukas Hahmann
 * 
 */
public interface IPlane extends IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	/**
	 * Setter for the parameter which indicates whether the plane should fly curves or roll
	 * @param rolling
	 */
	public void setRolling(boolean rolling);
	
	/**
	 * Getter for the game object instance.
	 * @return GameObject
	 */
	public GameObject getInstance();
	
	/**
	 * Getter for the model.
	 * @return Model
	 */
	public Model getModel();
	
	/**
	 * Getter for the maximum health of the spaceship.
	 * @return maximum health of the spaceship.
	 */
	public int getMaxHealth();

	/**
	 * Setter for the speed of the plane.
	 */
	public void setSpeed(float speed);
	
	/**
	 * Getter for the speed of the plane.
	 * @return speed
	 */
	public float getSpeed();
	
	/**
	 * Getter for the azimuth speed of the plane.
	 * @return azimuth speed
	 */
	public float getAzimuthSpeed();
	
	/**
	 * Getter for the rolling speed of the plane.
	 * @return rolling speed
	 */
	public float getRollingSpeed();
	
	/**
	 * Getter for the current position of the plane.
	 * @return position
	 */
	public Vector3 getPosition();
	
	/**
	 * Rotates the plane with given roll and azimuth dir.
	 * 
	 * @param rollDir
	 * @param azimuthDir
	 */
	public void rotate(float rollDir, float azimuthDir, float deltaFactor);
}