package de.fau.cs.mad.fly.features;

/**
 * Implement this interface when you want to implement a feature that has to render or update anything while the game is running.
 * 
 * @author Tobias Zangl
 */
public interface IFeatureRender {
	public void render(float delta);
}
