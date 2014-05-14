package de.fau.cs.mad.fly.levelLoader;

import java.text.ParseException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.Assets;

/**
 * Child class of {@link RawLevel} that contains all functions and containers to
 * extract the information out of its parent to load level in the 3D world.
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level extends RawLevel {
	/**
	 * Map of all Gate-objects with absolute position in 3D space.
	 */
	private HashMap<Integer, Gate> gates = new HashMap<Integer, Gate>();
	private Environment environment;

	/**
	 * Getter for {@link #gates}
	 * 
	 * @return {@link #gates}
	 */
	public HashMap<Integer, Gate> getGates() {
		return gates;
	}

	/**
	 * Converts the {@link RawLevel} to a {@link Level} where all information is
	 * generated to create the 3D world.
	 * 
	 * @see #check() for level completion check
	 * @throws ParseException
	 *             when level is not complete
	 */
	public void refactor() throws ParseException {
		if (check()) {
			calculateGatePositions();
		} else {
			throw new ParseException("Level " + this.name + " is not complete:"
					+ this.toString(), 0);
		}
	}

	/**
	 * Decides if all necessary information is loaded to create a level out of
	 * it.
	 * 
	 * @return true level is completely loaded
	 * @return false some information is missing
	 */
	public boolean check() {
		boolean complete = true;
		if (id >= 0 && name != "" && firstSection != null && sections != null
				&& firstSection.isComplete()) {
			int i = 0;
			while (i < sections.size() && complete) {
				complete = sections.get(i).isComplete();
				i++;
			}
		} else {
			Gdx.app.log("Level.isComplete()", "first comparison wrong");
			complete = false;
		}
		return complete;
	}

	/**
	 * Converts the relative positions defined as sections to absolute
	 * positions. These positions are saved in the
	 */
	private void calculateGatePositions() {
		// get the end of first section as first possible position for a gate
		Vector3 currentPosition = getCameraLookAt();
		Matrix4 rotationMatrix = null;
		Matrix4 translationMatrix = new Matrix4();
		ModelInstance lastGateInstance = null;
		float horizontalAngle = 0.0f;
		float verticalAngle = 0.0f;
		Vector3 currentVector = new Vector3(firstSection.directionX,
				firstSection.directionY, firstSection.directionZ);
		Vector3 verticalTurningAxis = new Vector3(0, 1, 0);
		Vector3 horizontalTurningAxis = new Vector3(firstSection.directionX,
				firstSection.directionY, firstSection.directionZ);
		horizontalTurningAxis = horizontalTurningAxis.crs(verticalTurningAxis)
				.nor();

		if (firstSection.gateID != Gate.NO_GATE) {
			Gate newGate = new Gate(firstSection.gateID);
			lastGateInstance = new ModelInstance(
					Assets.manager.get(Assets.torus));
			lastGateInstance.transform = translationMatrix
					.translate(getCameraLookAt())
					.rotate(verticalTurningAxis, getCameraLookAt()).cpy();
			newGate.modelInstance = lastGateInstance;
			gates.put(newGate.getId(), newGate);
		}
		for (Section s : sections) {
			rotationMatrix = new Matrix4();
			horizontalAngle = calculateAngle(s.minHorizontalAngle,
					s.maxHorizontalAngle);
			if (horizontalAngle != 0) {
				rotationMatrix = rotationMatrix.setToRotation(
						horizontalTurningAxis, horizontalAngle);
			}
			verticalAngle = calculateAngle(s.minVerticalAngle,
					s.maxVerticalAngle);
			if (verticalAngle != 0) {
				rotationMatrix = rotationMatrix.setToRotation(
						verticalTurningAxis, verticalAngle);
			}
			currentVector = currentVector.rot(rotationMatrix);
			verticalTurningAxis = verticalTurningAxis.rot(rotationMatrix);

			currentVector = currentVector.nor();
			currentPosition.mulAdd(currentVector, s.length);
			if (s.gateID != Gate.NO_GATE) {
				Gate newGate = new Gate(s.gateID);
				newGate.modelInstance = lastGateInstance.copy();
				newGate.modelInstance.transform = translationMatrix
						.trn(new Vector3().mulAdd(currentVector, s.length))
						.rotate(verticalTurningAxis, verticalAngle)
						.rotate(horizontalTurningAxis, horizontalAngle).cpy();
				gates.put(newGate.getId(), newGate);
				lastGateInstance = newGate.modelInstance;
			}
		}
	}

	/**
	 * Calculates a random number between @param min and @param max.
	 */
	private float calculateAngle(float min, float max) {
		return (float) (min + (Math.random() * (max - min)));
	}

	/**
	 * Getter for the starting position of the level
	 * 
	 * @return Vector3 with the starting position of the level
	 */
	public Vector3 getCameraStartPosition() {
		return new Vector3(startingPointX, startingPointY, startingPointZ);
	}

	/**
	 * Returns the point, the camera is looking at in the beginning. This point
	 * is generated by adding the starting position and the direction vector of
	 * the first section.
	 */
	public Vector3 getCameraLookAt() {
		Vector3 lookAt = new Vector3(startingPointX, startingPointY,
				startingPointZ);
		if (firstSection != null) {
			lookAt.add(firstSection.directionX, firstSection.directionY,
					firstSection.directionZ);
		}
		return lookAt;
	}

	/**
	 * Object that is used as level border
	 * 
	 * @return level border
	 */
	public ModelInstance getLevelBorder() {
		return new ModelInstance(Assets.manager.get(new AssetDescriptor<Model>(
				levelBorder, Model.class)));
	}

	/**
	 * Render the level
	 * 
	 * @param camera
	 *            that displays the level
	 */
	public void render(PerspectiveCamera camera) {
		setUpEnvironment();
		ModelBatch batch = new ModelBatch();
		batch.begin(camera);
		// rendering outer space
		if (levelBorder != null) {
			batch.render(getLevelBorder());
		}
		// render gates
		for (int i = 0; i < getGates().size(); i++) {
			batch.render(getGates().get(i).modelInstance, environment);
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

	@Override
	public String toString() {
		return "RawLevel:\nid: " + id + ",\nname: " + name
				+ ",\nstarting point: (" + startingPointX + ", "
				+ startingPointY + ", " + startingPointZ
				+ "),\nfirst section: " + firstSection + ",\nsections: "
				+ sections + "\n";
	}
}
