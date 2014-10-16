package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to
 * render anything while the level is running.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureRender {
    /**
     * Called every frame.
     * 
     * @param delta
     *            The time between the last and the current call.
     */
    public void render(float delta);
}