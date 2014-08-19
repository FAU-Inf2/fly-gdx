package de.fau.cs.mad.fly.player;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;

/**
 * Interface that has to implemented by everything that a user can steer in Fly.
 * 
 * @author Lukas Hahmann
 * 
 */
public interface IPlane extends IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	public static class Head {
        public int id;
        public String name;
        public String modelRef;
        public float speed;
        public float rollingSpeed;
        public float azimuthSpeed;
        public int lives;
        public FileHandle file;
        public int[] upgradeTypes;
        public List<String> upgrades;
        
        public Map<String, Integer> upgradesBought;
    }
	
	/**
	 * Setter for the parameter which indicates whether the plane should fly curves or roll
	 * @param rolling
	 */
	public void setRolling(boolean rolling);
	
	/**
	 * Getter for the head
	 * @return the head of the plane
	 */
	public IPlane.Head getHead();
	
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
	 * Resets the speed to the normal plane value without upgrades.
	 */
	public void resetSpeed();
	
	/**
	 * Returns the transformation matrix.
	 * @return transform
	 */
	public Matrix4 getTransform();
	
	/**
	 * Returns the position.
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