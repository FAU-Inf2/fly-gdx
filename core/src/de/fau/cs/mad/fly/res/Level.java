package de.fau.cs.mad.fly.res;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.geo.Perspective;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level extends Resource implements Disposable {

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
		
		ModelBuilder modelBuilder = new ModelBuilder();
		
		int n = 0;
		for (Gate gate : gates) {
			// TODO: use Gate constructor for this
			ModelResource m = (ModelResource) dependencies.get(gate.modelId);
			gate.model = new GameObject(Assets.manager.get(m.descriptor));
			gate.model.transform = new Matrix4(gate.transformMatrix);

			gate.goalModel = new GameObject(modelBuilder.createBox(1.0f, 0.05f, 1.0f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal));
			gate.goalModel.transform = new Matrix4(gate.transformMatrix);

			//btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(gate.modelId, CollisionDetector.USERVALUE_GATES + n, gate.model, gameController.getCollisionDetector().OBJECT_FLAG, gameController.getCollisionDetector().ALL_FLAG);
			//gate.model.addCollisionObject(gameController.getCollisionDetector(), shape, CollisionDetector.USERVALUE_GATES, gate.model);
			
			btCollisionShape goalShape = gameController.getCollisionDetector().getShapeManager().createBoxShape(gate.modelId + ".goal", new Vector3(1.0f, 0.05f, 1.0f));
			gate.goalModel.addCollisionObject(gameController.getCollisionDetector(), goalShape, CollisionDetector.USERVALUE_GATE_GOALS, gate, gameController.getCollisionDetector().DUMMY_FLAG, gameController.getCollisionDetector().ALL_FLAG);
			// TODO: dispose boxShape? or needed by collisionGoal?
			
			gate.fillSuccessorGateList(gates);
			
			gate.init();
			n++;
		}
		
		System.out.println(this);
	}

	/**
	 * Object that is used as level border.
	 * 
	 * @return level border
	 */
	public GameObject getLevelBorder() {
		return levelBorderModel;
	}
	
	/**
	 * Environment in the level.
	 * <p>
	 * Includes ambient and directional lights.
	 * 
	 * @return environment
	 */
	public Environment getEnvironment() {
		return environment;
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
			levelBorderModel.render(batch);
		}
		// render gates
		for (Gate gate : gates) {
			gate.render(batch, camera, environment);
		}
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
		return "#<Level " + id + ": name='" + name + "' gates=" + gates + ">";
	}

	@Override
	public void dispose() {
		for (Gate gate : gates) {
			gate.dispose();
		}
		
		gates.clear();
		
		levelBorderModel.dispose();
	}
}