package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.res.Assets;

/**
 * Singleton class which cares about the particle system.
 * 
 * @author Tobi
 * 
 */
public class ParticleController {
    
    /**
     * Singleton particle controller instance.
     */
    private static ParticleController instance;
    
    /**
     * Creates the particle controller if there is not already an instance
     * created.
     */
    public static void createParticleController() {
        if (instance == null) {
            instance = new ParticleController();
        }
    }
    
    /**
     * Getter for the particle controller singleton.
     * 
     * @return instance
     */
    public static ParticleController getInstance() {
        return instance;
    }
    
    /**
     * The singleton particle system.
     */
    private ParticleSystem particleSystem;
    
    /**
     * The billboard particle batch to draw the billboard particles.
     */
    private BillboardParticleBatch billboardParticleBatch;
    
    /**
     * The model batch.
     */
    private ModelBatch batch;
    
    /**
     * Resets the particle system and loads the new one.
     * 
     * @param camera
     *            The camera to render with.
     * @param batch
     *            The batch to render with.
     */
    public void load(Camera camera, ModelBatch batch) {
        this.batch = batch;
        
        reset();
        
        billboardParticleBatch = new BillboardParticleBatch();
        billboardParticleBatch.setCamera(camera);
        particleSystem.add(billboardParticleBatch);
        
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        Assets.manager.setLoader(ParticleEffect.class, loader);
    }
    
    /**
     * Resets the particle system.
     */
    public void reset() {
        particleSystem = ParticleSystem.get();
        particleSystem.removeAll();
        particleSystem.getBatches().clear();
    }
    
    /**
     * Adds an effect to the particle system.
     * 
     * @param effect
     *            The effect to add.
     */
    public void addEffect(ParticleEffect effect) {
        particleSystem.add(effect);
    }
    
    /**
     * Removes an effect from the particle system.
     * 
     * @param effect
     *            The effect to remove.
     */
    public void removeEffect(ParticleEffect effect) {
        particleSystem.remove(effect);
    }
    
    /**
     * Returns the current particle batches.
     * 
     * @return batches from the particle system.
     */
    public Array<ParticleBatch<?>> getBatches() {
        return particleSystem.getBatches();
    }
    
    /**
     * Renders and updates the particle system.
     */
    public void render() {
        particleSystem.update();
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        batch.render(particleSystem);
    }
}
