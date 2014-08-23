package de.fau.cs.mad.fly.player.particle;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

/**
 * Interface for particle effects for planes.
 * 
 * @author Tobi
 *
 */
public interface IParticle extends Disposable {
	/**
	 * Loads the particle effect.
	 * @param camera			The camera to render.
	 * @param batch				The model batch to render.
	 * @param plane				The plane to which the particle effect belongs.
	 */
	void load(Camera camera, ModelBatch batch, String plane);
	
	/**
	 * Initializes the particle effect.
	 */
	void init();
	
	/**
	 * Renders the particle effect.
	 * @param targetMatrix		The current position of the plane.
	 */
	void render(Matrix4 targetMatrix);
	
	/**
	 * Stops the particle effect.
	 */
	void stop();
}
