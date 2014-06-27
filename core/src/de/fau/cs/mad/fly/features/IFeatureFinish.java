package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to be called after the level is finished.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureFinish {
	/**
	 * Called when the level is finished.
	 */
	public void finish();
}
