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
import de.fau.cs.mad.fly.game.GameController;
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

	private Environment environment;

	private ModelBatch batch;

	private ModelInstance levelBorderModel;
	
	public List<ModelInstance> gateModels;
	
	public void initLevel(GameController gameController) {
		setUpEnvironment();

		batch = gameController.batch;
		

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
	public void render(PerspectiveCamera camera) {
		// rendering outer space
		if (levelBorderModel != null) {
			batch.render(levelBorderModel);
		}
		// render gates
		for (ModelInstance mi : gateModels) {
			batch.render(mi, environment);
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
		return "#<Level:" + id + " name=" + name + " startingPoint=" + start
				+ " gates=" + gates;
	}
}

/**
 * Converts the {@link RawLevel} to a {@link Level} where all information is
 * generated to create the 3D world.
 * 
 * @see #check() for level completion check
 * @throws ParseException
 *             when level is not complete
 */
/*
 * public void refactor() throws ParseException { if (check()) {
 * calculateGatePositions(); } else { throw new ParseException("Level " +
 * this.name + " is not complete:" + this.toString(), 0); } }
 */

/**
 * Decides if all necessary information is loaded to create a level out of it.
 * 
 * @return true level is completely loaded
 * @return false some information is missing
 */
/*
 * public boolean check() { boolean complete = true; if (id != null &&
 * !name.isEmpty() && firstSection != null && sections != null &&
 * firstSection.isComplete()) { int i = 0; while (i < sections.size() &&
 * complete) { complete = sections.get(i).isComplete(); i++; } } else {
 * Gdx.app.log("Level.isComplete()", "first comparison wrong"); complete =
 * false; } return complete; }
 */

/**
 * Converts the relative positions defined as sections to absolute positions.
 * These positions are saved in the
 */
/*
 * private void calculateGatePositions() { // get the end of first section as
 * first possible position for a gate Vector3 currentPosition =
 * getCameraLookAt(); Matrix4 rotationMatrix; Matrix4 translationMatrix = new
 * Matrix4(); ModelInstance lastGateInstance = null; float horizontalAngle;
 * float verticalAngle; Vector3 currentVector = new
 * Vector3(firstSection.directionX, firstSection.directionY,
 * firstSection.directionZ); Vector3 verticalTurningAxis = new Vector3(0, 1, 0);
 * Vector3 horizontalTurningAxis = new Vector3(firstSection.directionX,
 * firstSection.directionY, firstSection.directionZ); horizontalTurningAxis =
 * horizontalTurningAxis.crs(verticalTurningAxis) .nor();
 * 
 * if (firstSection.gateID != Gate.NO_GATE) { Gate newGate = new
 * Gate(firstSection.gateID); lastGateInstance = new ModelInstance(
 * Assets.manager.get(Assets.torus)); lastGateInstance.transform =
 * translationMatrix .translate(getCameraLookAt()) .rotate(verticalTurningAxis,
 * getCameraLookAt()).cpy(); newGate.modelInstance = lastGateInstance;
 * gates.put(newGate.getId(), newGate); } for (Section s : sections) {
 * rotationMatrix = new Matrix4(); horizontalAngle =
 * calculateAngle(s.minHorizontalAngle, s.maxHorizontalAngle); if
 * (horizontalAngle != 0) { rotationMatrix = rotationMatrix.setToRotation(
 * horizontalTurningAxis, horizontalAngle); } verticalAngle =
 * calculateAngle(s.minVerticalAngle, s.maxVerticalAngle); if (verticalAngle !=
 * 0) { rotationMatrix = rotationMatrix.setToRotation( verticalTurningAxis,
 * verticalAngle); } currentVector = currentVector.rot(rotationMatrix);
 * verticalTurningAxis = verticalTurningAxis.rot(rotationMatrix);
 * 
 * currentVector = currentVector.nor(); currentPosition.mulAdd(currentVector,
 * s.length); if (s.gateID != Gate.NO_GATE) { Gate newGate = new Gate(s.gateID);
 * newGate.modelInstance = lastGateInstance.copy();
 * newGate.modelInstance.transform = translationMatrix .trn(new
 * Vector3().mulAdd(currentVector, s.length)) .rotate(verticalTurningAxis,
 * verticalAngle) .rotate(horizontalTurningAxis, horizontalAngle).cpy();
 * gates.put(newGate.getId(), newGate); lastGateInstance =
 * newGate.modelInstance; } } }
 */
/**
 * Calculates a random number between @param min and @param max.
 */
/*
 * private float calculateAngle(float min, float max) { return (float) (min +
 * (Math.random() * (max - min))); }
 */