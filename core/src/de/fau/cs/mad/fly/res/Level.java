package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;

import de.fau.cs.mad.fly.Assets;
import de.fau.cs.mad.fly.geo.Perspective;

import java.util.ArrayList;
import java.util.List;

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

	private ModelInstance levelBorderModel;
	
	public List<ModelInstance> gateModels;
	
	public void initLevel() {
		if (levelBorder != null) {
			levelBorderModel = new ModelInstance(
					Assets.manager.get(new AssetDescriptor<Model>(levelBorder,
							Model.class)));
		} else {
			// CRASH
		}
		
		gateModels = new ArrayList<ModelInstance>();
		
		for (Gate g : gates) {
			ModelResource m = (ModelResource) dependencies.get(g.model);
			ModelInstance mi = new ModelInstance(
					Assets.manager.get(m.descriptor));
			mi.transform = new Matrix4(g.transformMatrix);
			gateModels.add(mi);
		}
	}

	/**
	 * Object that is used as level border
	 * 
	 * @return level border
	 */
	public ModelInstance getLevelBorder() {
		return levelBorderModel;
	}

	/**
	 * Render the level
	 * 
	 * @param camera
	 *            that displays the level
	 */
	public void render(Environment environment, PerspectiveCamera camera,
			ModelBatch batch, float delta) {
		
		// rendering outer space
		if (levelBorderModel != null) {
			batch.render(levelBorderModel);
		}
		// render gates
		for (ModelInstance mi : gateModels) {
			batch.render(mi, environment);
		}
		
	}

	@Override
	public String toString() {
		return "#<Level:" + id + " name=" + name + " startingPoint=" + start
				+ " gates=" + gates;
	}
}