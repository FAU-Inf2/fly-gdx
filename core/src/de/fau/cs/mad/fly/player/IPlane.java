package de.fau.cs.mad.fly.player;

import java.util.HashMap;
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
import de.fau.cs.mad.fly.player.gravity.IGravity;

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
        public int levelGroupDependency;
        public float speed;
        public float rollingSpeed;
        public float azimuthSpeed;
        public int lives;
        public float rotationSpeed = 0.0f;
        public Vector3 rotation = null;
        public Vector3 particleOffset = null;
        public FileHandle file;
        public int[] upgradeTypes;
        public List<String> upgrades;
        
        private Map<String, Integer> upgradesBought = new HashMap<String, Integer>();
        
        /**
         * @return the upgradesBought
         */
        public Map<String, Integer> getUpgradesBought() {
            return upgradesBought;
        }
        
        private Map<String, Integer> upgradesEquiped = new HashMap<String, Integer>();
        
        /**
         * @return the upgradesEquiped
         */
        public Map<String, Integer> getUpgradesEquiped() {
            return upgradesEquiped;
        }
        
        public void addUpgradeBought(String name, int value) {
            upgradesBought.put(name, value);
        }
        
        public void addUpgradeEquiped(String name, int value) {
            upgradesEquiped.put(name, value);
        }
        
    }
    
    /**
     * Getter for the head.
     * 
     * @return the head of the plane.
     */
    public IPlane.Head getHead();
    
    /**
     * Getter for the model.
     * 
     * @return Model
     */
    public Model getModel();
    
    /**
     * Getter for the maximum health of the spaceship.
     * 
     * @return maximum health of the spaceship.
     */
    public int getMaxHealth();
    
    /**
     * Setter for the speed of the plane.
     * 
     * @param speed
     */
    public void setSpeed(float speed);
    
    /**
     * Getter for the speed of the plane.
     * 
     * @return speed
     */
    public float getSpeed();
    
    /**
     * Setter for the gravity of the plane.
     * 
     * @param gravity
     */
    public void setGravity(IGravity gravity);
    
    /**
     * Getter for the gravity of the plane.
     * 
     * @return gravity
     */
    public IGravity getGravity();
    
    /**
     * Getter for the azimuth speed of the plane.
     * 
     * @return azimuth speed
     */
    public float getAzimuthSpeed();
    
    /**
     * Getter for the rolling speed of the plane.
     * 
     * @return rolling speed
     */
    public float getRollingSpeed();
    
    /**
     * Resets the speed to the normal plane value without upgrades.
     */
    public void resetSpeed();
    
    /**
     * Returns the transformation matrix.
     * 
     * @return transform
     */
    public Matrix4 getTransform();
    
    /**
     * Returns the position.
     * 
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