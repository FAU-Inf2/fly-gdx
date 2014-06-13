package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.game.GameObject;

/**
 * Represents a gate in the game.
 * <p>
 * Stores and renders the model of the gate and stores the information about
 * the successor gates of it.
 * 
 * @author Lukas Hahmann
 */
public class Gate implements Disposable {

	public static final int NO_GATE = -1;

	public int id;

	public String modelId;

	public float[] transformMatrix;
	
	public ArrayList<Integer> successors = new ArrayList<Integer>();
	public ArrayList<Gate> successorGates = new ArrayList<Gate>();
	
	public GameObject model = null;
	public GameObject goalModel = null;
	
	public btCollisionObject collisionObject;
	public btCollisionObject collisionGoal;
	public btCollisionShape boxShape;
	
	private ModelBatch batch;
	//private PerspectiveCamera camera;
	private Environment environment;
	
	private boolean visible = true;
	
	public Gate() {
		// DUMMY
	}
	
	public Gate(int id, ArrayList<Integer> successors, AssetDescriptor<Model> modelAsset, Matrix4 transformMatrix, ModelBatch batch, Environment environment) {
		// TODO: use this constructor instead of Gate() called by JSON-to-Class and Level.initLevel()
		this.id = id;
		this.successors = successors;
		this.batch = batch;
		//this.camera = camera;
		this.environment = environment;

		model = new GameObject(Assets.manager.get(modelAsset));
		model.transform = transformMatrix;
		
		init();
	}
	
	/**
	 * Fills the successor gate list with the before created gate instances.
	 */
	public void fillSuccessorGateList(List<Gate> allGates) {
		for(Integer i : successors) {
			successorGates.add(allGates.get(i));
		}
	}
	
	/**
	 * Initializes the gate.
	 * <p>
	 * Sets the color dependent if its the first gate and a target or not.
	 */
	public void init() {
		if(id == 0) {
			setTarget();
		} else {
			setNoTarget();
		}
	}
	
	/**
	 * Getter for the successor gate list.
	 * 
	 * @return successorGates
	 */
	public ArrayList<Gate> getSuccessors() {
		return successorGates;
	}
	
	// TODO: use batch and environment from constructor
	public void render(ModelBatch batch, PerspectiveCamera camera, Environment env) {
		if(visible && model.isVisible(camera)) {
			model.render(batch, env);
			//goalModel.render(batch, env);
		}
	}
	
	/**
	 * Called if the gate is currently a target for the player.
	 */
	public void setTarget() {
		setColor(Color.RED);
	}
	
	/**
	 * Called if the gate is not or no longer a target for the player.
	 */
	public void setNoTarget() {
		setColor(Color.GRAY);
	}
	
	/**
	 * Sets the diffuse color of the gate model.
	 */
	public void setColor(Color color) {
		model.materials.get(0).set(ColorAttribute.createDiffuse(color));
	}
	
	/**
	 * Sets the visibility of the gate model.
	 */
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && obj instanceof Gate && id == ((Gate) obj).id;
	}
	
	@Override
	public String toString() {
		return "<Gate " + id + ">";
	}

	@Override
	public void dispose() {
		model.dispose();
		goalModel.dispose();
		
		successors.clear();
		successorGates.clear();
		
		collisionObject.dispose();
		collisionGoal.dispose();
	}
}
