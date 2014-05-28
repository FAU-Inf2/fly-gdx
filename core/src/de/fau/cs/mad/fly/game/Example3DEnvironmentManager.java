package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

/**
 * This is a sample class to demonstrate how 3d resources could be handled. To
 * pull it out of the class Level would allow us to have more than one component
 * that adds 3d models to the scene, which is necessary at least for the
 * GateIndicator.
 * 
 * @author Lukas Hahmann
 * 
 */
public class Example3DEnvironmentManager {

	private Environment environment;

	private ModelBatch batch;

	private List<ModelInstance> modelInstances;

	public Example3DEnvironmentManager() {
		setUpEnvironment();
		batch = new ModelBatch();
		// TODO: think about an appropriate and fast data structure to manage
		// adding new ModelInstances during runtime and removing them with O(1).
		// Suggestion: HashMap
		modelInstances = new ArrayList<ModelInstance>();
	}
	
	public void addModelInstance(ModelInstance instance) {
		this.modelInstances.add(instance);
	}
	
	public void removeModelInstance(ModelInstance instance) {
		this.modelInstances.remove(instance);
	}

	/**
	 * Render all known model Instances.
	 * 
	 * @param camera
	 */
	public void render(PerspectiveCamera camera) {
		batch.begin(camera);
		for (ModelInstance mi : modelInstances) {
			batch.render(mi, environment);
		}
		batch.end();
	}

	/**
	 * Sets up the environment for the level with its light.
	 */
	private void setUpEnvironment() {
		// setting up the environment
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));
	}

}
