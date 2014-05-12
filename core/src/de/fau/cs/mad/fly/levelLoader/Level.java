package de.fau.cs.mad.fly.levelLoader;

import java.text.ParseException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Child class of {@link RawLevel} that contains all functions and containers to
 * extract the information out of its parent to load level in the 3D world.
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level extends RawLevel {
	/**
	 * Radius of the Level which defines the outer boundary which should be
	 * never reached by the user.
	 */
	private float radius = 0.0f;

	private HashMap<Integer, Gate> gates = new HashMap<Integer, Gate>();

	public HashMap<Integer, Gate> getGates() {
		return gates;
	}

	/**
	 * Converts the {@link RawLevel} to a {@link Level} where all information is
	 * generated to create the 3D world.
	 * 
	 * @see #isComplete() for level completion check
	 * @throws ParseException
	 *             when level is not complete
	 */
	public void refactor() throws ParseException {
		if (isComplete()) {
			calculateLevelRadius();
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
	public boolean isComplete() {
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

		Vector3 verticalTurningAxis = new Vector3(0, 1, 0);
		Vector3 horizontalTurningAxis = new Vector3(firstSection.directionX,
				firstSection.directionY, firstSection.directionZ);
		horizontalTurningAxis = horizontalTurningAxis.crs(verticalTurningAxis)
				.nor();
		float horizontalAngle = 0.0f;
		float verticalAngle = 0.0f;

		Vector3 currentVector = new Vector3(firstSection.directionX,
				firstSection.directionY, firstSection.directionZ);

		if (firstSection.gateID != Gate.NO_GATE) {
			Gate newGate = new Gate(currentPosition.cpy());
			gates.put(newGate.getId(), newGate);
		}
		for (Section s : sections) {
			horizontalAngle = calculateHorizontalAngle(s);
			if (horizontalAngle != 0) {
				rotationMatrix = new Matrix4().setToRotation(
						horizontalTurningAxis, horizontalAngle);
				currentVector = currentVector.rot(rotationMatrix);
				verticalTurningAxis = verticalTurningAxis.rot(rotationMatrix);
			}
			verticalAngle = calculateVerticalAngle(s);
			if (verticalAngle != 0) {
				rotationMatrix = new Matrix4().setToRotation(
						verticalTurningAxis, verticalAngle);
				currentVector = currentVector.rot(rotationMatrix);
				horizontalTurningAxis = horizontalTurningAxis
						.rot(rotationMatrix);
			}
			currentVector = currentVector.nor();
			currentPosition.mulAdd(currentVector, s.length);
			if (s.gateID != Gate.NO_GATE) {
				Vector3 position = new Vector3(currentPosition.x,
						currentPosition.y, currentPosition.z);
				Gate newGate = new Gate(position);
				gates.put(newGate.getId(), newGate);
			}
		}

	}

	/**
	 * If {@link Section#minHorizontalAngle} ==
	 * {@link Section#maxHorizontalAngle} this angle is return. Otherwise a
	 * random number between the two values is generate.
	 * 
	 * @param section
	 * @return horizontalAngle
	 */
	private float calculateHorizontalAngle(Section section) {
		if (section.minHorizontalAngle == section.maxHorizontalAngle) {
			return section.minHorizontalAngle;
		} else {
			return (float) (section.minHorizontalAngle + (Math.random() * (section.maxHorizontalAngle - section.minHorizontalAngle)));
		}
	}

	/**
	 * If {@link Section#minVerticalAngle} == {@link Section#maxVerticalAngle}
	 * this angle is return. Otherwise a random number between the two values is
	 * generate.
	 * 
	 * @param section
	 * @return verticalAngle
	 */
	private float calculateVerticalAngle(Section section) {
		if (section.minVerticalAngle == section.maxVerticalAngle) {
			return section.minVerticalAngle;
		} else {
			return (float) (section.minVerticalAngle + (Math.random() * (section.maxVerticalAngle - section.minVerticalAngle)));
		}
	}

	/**
	 * Radius of the level.
	 * 
	 * @see #calculateLevelRadius()
	 * @return {@link #radius}
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Calculates the {@link #radius} of the Level as a sum of the length of all
	 * sections. To make sure the radius can never be reached, the final result
	 * is increased by 10 %.
	 */
	private void calculateLevelRadius() {
		radius = super.firstSection.length;
		for (Section section : super.sections) {
			radius += section.length;
		}
		radius *= 1.1;
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

	@Override
	public String toString() {
		String string = new String("RawLevel:\n");
		string += ("id: " + id + ",\n");
		string += ("name: " + name + ",\n");
		string += ("starting point: (" + startingPointX + ", " + startingPointY
				+ ", " + startingPointZ + "),\n");
		string += ("first section: " + firstSection + ",\n");
		string += ("sections: " + sections + "\n");
		return string.toString();
	}
}
