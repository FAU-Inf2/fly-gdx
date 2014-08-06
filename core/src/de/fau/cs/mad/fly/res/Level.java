package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

import java.util.*;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level implements Disposable {
    
    public static class Head {
        public String name;
        public int id;
        public FileHandle file;
    }
    
    /**
     * Radius of the Level which defines the outer boundary which should be
     * never reached by the user. The default level border defines a sphere with
     * radius 100.
     */
    public final float radius = 100.0f;
    public final Head head;
    
    public String levelClass = "DefaultLevel";
    
    public List<GameObject> components;
    private List<Collectible> upgrades;
    public final Perspective start;
    private final Environment environment;
    private final Map<String, GameModel> dependencies;
    
    private GameObject borderObject = null;
    private int CollisionTime = 0;
    private int leftCollisionTime = 0;
    
    private GateCircuit gateCircuit = null;

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
    
    public Level(String name, Perspective start, List<GameObject> components, Map<String, GameModel> dependencies) {
        this.head = new Head();
        this.head.name = name;
        this.components = components;
        this.start = start;
        this.environment = new Environment();
        this.dependencies = Collections.unmodifiableMap(dependencies);
        setUpEnvironment();
        
        InitCollisionTime();
        gameOver = false;
        
        for (GameObject c : components) {
            if (c.id.equals("space")) {
                borderObject = c;
            }
        }
        
        if (borderObject == null) {
            Gdx.app.log("Level.Level", "No border specified.");
        }
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
     * @return gateCircuit
     */
    public GateCircuit getGateCircuit() {
    	return gateCircuit;
    }
    
    /**
     * Adds the gate circuit to the level.
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
        // Gdx.app.log("components:", String.valueOf(numberOfComponents));
        for (i = 0; i < numberOfComponents; i++) {
            components.get(i).render(batch, environment, camera);
        }
    }
    
    /**
     * Sets up the environment for the level with its light.
     */
    private void setUpEnvironment() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
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