package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to
 * draw anything while the level is running.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureDraw {
    /**
     * Called every frame.
     * 
     * @param delta
     *            The time between the last and the current call.
     */
    public void draw(float delta);
}