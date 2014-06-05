package de.fau.cs.mad.fly.res;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;

import de.fau.cs.mad.fly.Assets;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.geo.Perspective;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level extends Resource {

	/**
	 * Name of the level which is displayed to the user when choosing levels
	 */
	public String name = "";

	/**
	 * Name of the object which is used as level border. Default is outer space.
	 */
	public String levelBorder = "spacesphere.obj";

	/**
	 * Radius of the Level which defines the outer boundary which should be
	 * never reached by the user. The default level border defines a sphere with
	 * radius 100.
	 */
	public float radius = 100.0f;

	public List<Gate> gates;

	public Perspective start;

	private Environment environment;

	private ModelBatch batch;

	private GameObject levelBorderModel;
	
	//For what is this member needed?
	//private Vector3 cullingPosition = new Vector3();
	
	public void initLevel(GameController gameController) {
		setUpEnvironment();

		batch = gameController.batch;
		

		if (levelBorder != null) {
			levelBorderModel = new GameObject(
					Assets.manager.get(new AssetDescriptor<Model>(levelBorder,
							Model.class)));
		} else {
			// CRASH
		}
		
		//gateModels = new ArrayList<GameObject>();
		
		for (Gate g : gates) {
			ModelResource m = (ModelResource) dependencies.get(g.modelId);
			g.model = new GameObject(
					Assets.manager.get(m.descriptor));
			g.model.transform = new Matrix4(g.transformMatrix);
		}
	}

	/**
	 * Object that is used as level border
	 * 
	 * @return level border
	 */
	public GameObject getLevelBorder() {
		return levelBorderModel;
	}

	/**
	 * Render the level
	 * 
	 * @param camera
	 *            that displays the level
	 */
	public void render(PerspectiveCamera camera) {
		// rendering outer space
		if (levelBorderModel != null) {
			batch.render(levelBorderModel);
		}
		// render gates
		int x = 0;
		for (Gate gate : gates) {
			if(gate.model.isVisible(camera)) {
				batch.render(gate.model, environment);
				x++;
			}
		}
		
		// debug of the count of rendered gates
		//System.out.println(x);
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

	@Override
	public String toString() {
		return "#<Level:" + id + " name=" + name + " startingPoint=" + start
				+ " gates=" + gates;
	}
}