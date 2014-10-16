package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to
 * dispose anything after the level is over.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureDispose {
    /**
     * Called when the level is disposed.
     */
    public void dispose();
}
