package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.player.gravity.EmptyGravity;
import de.fau.cs.mad.fly.player.gravity.IGravity;

import java.util.*;

/**
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class Level implements Disposable {
    
    /**
     * Radius of the Level which defines the outer boundary which should be
     * never reached by the user. The default level border defines a sphere with
     * radius 100.
     */
    public final float radius = 100.0f;
    public final static String AMBIENT_ENVIRONMENT = "ambient";
    public final LevelProfile head;
    /** ID of the border object */
    public final static String BORDER_NAME = "border";
    
    public String levelClass = "DefaultLevel";
    
    public List<GameObject> components;
    public final Perspective start;
    private final Environment environment;
    private final Map<String, GameModel> dependencies;
    
    private GameObject borderObject = null;
    
    private GateCircuit gateCircuit = null;
    private CollectibleManager collectibleManager = null;
    
    private IGravity gravity = new EmptyGravity();
    
    private float leftTime = 0;
    
    public float getLeftTime() {
        return leftTime;
    }
    
    public void setLeftTime(float leftTime) {
        this.leftTime = leftTime;
    }
    
    public Level(String name, Perspective start, List<GameObject> components, Map<String, GameModel> dependencies, Map<String, Environment> environments) {
        this.head = new LevelProfile();
        this.head.name = name;
        this.components = components;
        this.start = start;
        this.dependencies = Collections.unmodifiableMap(dependencies);
        this.environment = environments.get("lighting");
        environments.get(AMBIENT_ENVIRONMENT);
        
        int size = components.size();
        GameObject c;
        for (int i = 0; i < size; i++) {
            c = components.get(i);
            if (c.getId().equals(BORDER_NAME)) {
                borderObject = c;
                borderObject.environment = environments.get(AMBIENT_ENVIRONMENT);
                i = size; // break
            }
        }
        
        if (borderObject == null) {
            Gdx.app.log("Level.Level", "No border specified.");
        }
    }
    
    /**
     * Setter for the gravity.
     * 
     * @param gravity
     *            The new gravity for the level.
     */
    public void setGravity(IGravity gravity) {
        this.gravity = gravity;
    }
    
    /**
     * Getter for the gravity.
     * 
     * @return gravity
     */
    public IGravity getGravity() {
        return gravity;
    }
    
    public GameModel getDependency(String id) {
        return dependencies.get(id);
    }
    
    /**
     * Getter for the collectible manager.
     * 
     * @return collectibleManager
     */
    public CollectibleManager getCollectibleManager() {
        return collectibleManager;
    }
    
    /**
     * Adds the collectible manager to the level.
     * 
     * @param collectibleManager
     */
    public void addCollectibleManager(CollectibleManager collectibleManager) {
        this.collectibleManager = collectibleManager;
    }
    
    /**
     * Getter for the gate circuit.
     * 
     * @return gateCircuit
     */
    public GateCircuit getGateCircuit() {
        return gateCircuit;
    }
    
    /**
     * Adds the gate circuit to the level.
     * 
     * @param gateCircuit
     */
    public void addGateCircuit(GateCircuit gateCircuit) {
        this.gateCircuit = gateCircuit;
        this.gateCircuit.level = this;
    }
    
    /**
     * Environment in the level.
     * <p>
     * Includes ambient and directional lights.
     * 
     * @return environment
     */
    public Environment getEnvironment() {
        return environment;
    }
    
    /**
     * Update the level. Checks whether the level is finished or not.
     * 
     * @param delta
     *            time after the last call.
     * @param camera
     *            that displays the level.
     */
    public void update(float delta, PerspectiveCamera camera) {
        borderObject.transform.setToTranslation(camera.position);
        
        gateCircuit.moveGates(delta);
        
        int i;
        final int numberOfComponents = components.size();
        for (i = 0; i < numberOfComponents; i++) {
            components.get(i).move(delta);
        }
        
        collectibleManager.moveCollectibles(delta);
    }
    
    /**
     * Render the level.
     * 
     * @param delta
     *            time after the last call.
     * @param batch
     *            the batch to render the level.
     * @param camera
     *            that displays the level.
     */
    public void render(float delta, ModelBatch batch, PerspectiveCamera camera) {
        int i;
        final int numberOfComponents = components.size();
        for (i = 0; i < numberOfComponents; i++) {
            if (components.get(i).environment != null)
                components.get(i).render(batch, camera);
            else
                components.get(i).render(batch, environment, camera);
        }
        
        gateCircuit.render(batch, environment, camera);
        
        collectibleManager.render(batch, environment, camera);
    }
    
    @Override
    public String toString() {
        return "#<Level name=" + head.name + ">";
    }
    
    @Override
    public void dispose() {
        Gdx.app.log("Level.dispose", "Disposing...");
        for (GameObject o : components)
            o.dispose();
        // for ( GameModel m : dependencies )
        // m.dispose();
    }
}