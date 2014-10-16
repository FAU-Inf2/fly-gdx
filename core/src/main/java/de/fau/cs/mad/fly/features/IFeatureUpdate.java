package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to
 * update anything while the level is running.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureUpdate {
    /**
     * Called every frame if the game is not paused.
     * 
     * @param delta
     *            The time between the last and the current call.
     */
    public void update(float delta);
}