package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.player.gravity.EmptyGravity;
import de.fau.cs.mad.fly.player.gravity.IGravity;

import java.util.*;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level implements Disposable {
    
    /**
     * Radius of the Level which defines the outer boundary which should be
     * never reached by the user. The default level border defines a sphere with
     * radius 100.
     */
    public final float radius = 100.0f;
    public final LevelProfile head;
    
    public String levelClass = "DefaultLevel";
    
    public List<GameObject> components;
    private List<Collectible> upgrades;
    public final Perspective start;
    private final Environment environment, ambientEnvironment;
    private final Map<String, GameModel> dependencies;
    
    private GameObject borderObject = null;
    private int CollisionTime = 0;
    private int leftCollisionTime = 0;
    
    private GateCircuit gateCircuit = null;
    
    private IGravity gravity = new EmptyGravity();
    
    private float leftTime = 0;
    
    public float getLeftTime() {
        return leftTime;
    }
    
    public void setLeftTime(float leftTime) {
        this.leftTime = leftTime;
    }
    
    protected void InitCollisionTime() {
        CollisionTime = 3;
        leftCollisionTime = CollisionTime;
    }
    
    private boolean gameOver = false;
    
    public boolean isGameOver() {
        return gameOver;
    }

    public Level(String name, Perspective start, List<GameObject> components, Map<String, GameModel> dependencies, Map<String, Environment> environments) {
        this.head = new LevelProfile();
        this.head.name = name;
        this.components = components;
        this.start = start;
        this.dependencies = Collections.unmodifiableMap(dependencies);
        this.environment = environments.get("lighting");
        this.ambientEnvironment = environments.get("ambient");
        InitCollisionTime();
        gameOver = false;
        
        for (GameObject c : components) {
            if (c.getId().equals("space")) {
                borderObject = c;
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
    
    public Map<String, GameModel> getDependencies() {
        return dependencies;
    }
    
    public void setUpgrades(List<Collectible> upgrades) {
        this.upgrades = upgrades;
    }
    
    public List<Collectible> getUpgrades() {
        return upgrades;
    }
    
    public void finishLevel() {
        gameOver = true;
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
        final int numberOfUpgrades = upgrades.size();
        for (i = 0; i < numberOfUpgrades; i++) {
            upgrades.get(i).move(delta);
        }
        
        if (gameOver == false && ((int) leftTime <= 0 || leftCollisionTime <= 0)) {
            gateCircuit.circuitFinished();
        }
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
            if(components.get(i).environment != null) components.get(i).render(batch, camera);
            else components.get(i).render(batch, environment, camera);
        }
        
        gateCircuit.render(batch, environment, camera);
        
        final int numberOfUpgrades = upgrades.size();
        for (i = 0; i < numberOfUpgrades; i++) {
            upgrades.get(i).render(batch, environment, camera);
        }
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