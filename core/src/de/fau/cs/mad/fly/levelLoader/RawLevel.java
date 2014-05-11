package de.fau.cs.mad.fly.levelLoader;

import java.util.ArrayList;

/**
 * {@link RawLevel} is a container with all information, that is stored in GSON
 * level files.
 * 
 * @author Lukas Hahmann
 * 
 */
public class RawLevel {
	/**
	 * Id of the level which has to be contained in the GSON file
	 */
	public int id = -1;

	/**
	 * Name of the level which is displayed to the user when choosing levels
	 */
	public String name = "";

	/**
	 * x-coordinate of the starting point of this level
	 */
	public float startingPointX = 0.0f;
	/**
	 * y-coordinate of the starting point of this level
	 */
	public float startingPointY = 0.0f;

	/**
	 * z-coordinate of the starting point of this level
	 */
	public float startingPointZ = 0.0f;

	/**
	 * section that connects the starting point with the following sections. It
	 * defines the direction the user looks at, when starting a level.
	 */
	public FirstSection firstSection = null;

	/**
	 * List of all following sections. The level end is defined by at least one
	 * section with no successor.
	 */
	public ArrayList<Section> sections = null;

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
			complete = false;
		}
		return complete;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("RawLevel:\n");
		stringBuilder.append("id: " + id + ",\n");
		stringBuilder.append("name: " + name + ",\n");
		stringBuilder.append("starting point: (" + startingPointX + ", "
				+ startingPointY + ", " + startingPointZ + "),\n");
		stringBuilder.append("first section: " + firstSection + ",\n");
		stringBuilder.append("sections: " + sections + "\n");
		return stringBuilder.toString();
	}
}
