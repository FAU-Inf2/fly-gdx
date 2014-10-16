package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import de.fau.cs.mad.fly.features.upgrades.types.Collectible;

/**
 * Manages and displays all collectible objects.
 * 
 * @author Tobi
 * 
 */
public class CollectibleManager {
    /**
     * The list with all collectible objects.
     */
    private List<Collectible> collectibles;
    
    /**
     * Creates a new collectible manager with no collectibles.
     */
    public CollectibleManager() {
        collectibles = new ArrayList<Collectible>();
    }
    
    /**
     * Creates a new collectible manager with collectibles.
     * 
     * @param collectibles
     *            The list of collectibles.
     */
    public CollectibleManager(List<Collectible> collectibles) {
        this.collectibles = collectibles;
    }
    
    /**
     * Getter for the list of collectibles.
     * 
     * @return list of collectibles.
     */
    public List<Collectible> getCollectibles() {
        return collectibles;
    }
    
    /**
     * Setter for the list of collectibles.
     * 
     * @param list
     *            of collectibles.
     */
    public void setCollectibles(List<Collectible> collectibles) {
        this.collectibles = collectibles;
    }
    
    /**
     * Adds a collectible to the collectible manager.
     * 
     * @param c
     *            The collectible that should be added.
     */
    public void addCollectible(Collectible c) {
        collectibles.add(c);
    }
    
    /**
     * Removes a collectible to the collectible manager.
     * 
     * @param c
     *            The collectible that should be removed.
     */
    public void removeCollectible(Collectible c) {
        collectibles.remove(c);
        c.dispose();
    }
    
    /**
     * Calls the mover for all collectibles.
     * 
     * @param delta
     *            Time since last call.
     */
    public void moveCollectibles(float delta) {
        final int numberOfUpgrades = collectibles.size();
        for (int i = 0; i < numberOfUpgrades; i++) {
            collectibles.get(i).move(delta);
        }
    }
    
    /**
     * Renders the collectibles.
     * 
     * @param batch
     *            The model batch for the rendering.
     * @param environment
     *            The environment for the rendering.
     * @param camera
     *            The camera for the rendering.
     */
    public void render(ModelBatch batch, Environment environment, PerspectiveCamera camera) {
        final int numberOfUpgrades = collectibles.size();
        for (int i = 0; i < numberOfUpgrades; i++) {
            collectibles.get(i).render(batch, environment, camera);
        }
    }
}
