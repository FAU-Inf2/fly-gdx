package de.fau.cs.mad.fly.levelLoader;

import java.util.ArrayList;

/**
 * Vector that defines a connection between gates. All Sections result in the
 * path which creates the level.
 * 
 * @author Lukas Hahmann
 * 
 */
public class Section {

	/**
	 * Each section should not have a horizontal angle that is greater than this
	 * value.
	 */
	public final double MAX_ABS_HORIZONTAL_ANGLE = 90.0;
	/**
	 * Each section should not have a vertical angle that is greater than this
	 * value.
	 */
	public final double MAX_ABS_VERTICAL_ANGLE = 90.0;
	/**
	 * ID which has to be greater than 0. This id has to be successor of any
	 * other section in the level, otherwise it is not used.
	 */
	public int id = -1;

	/**
	 * Defines the minimum horizontal angle this section differs horizontally
	 * from its predecessor
	 */
	public double minHorizontalAngle = 0.0;
	/**
	 * Defines the maximum horizontal angle this section differs from its
	 * predecessor
	 */
	public double maxHorizontalAngle = 0.0;
	/**
	 * Defines the minimum vertical angle this section differs horizontally from
	 * its predecessor
	 */
	public double minVerticalAngle = 0.0;
	/**
	 * Defines the maximum vertical angle this section differs horizontally from
	 * its predecessor
	 */
	public double maxVerticalAngle = 0.0;
	/**
	 * Length of the line between the starting and the endpoint of this section.
	 */
	public float length = 0.0f;

	/**
	 * List of succesor section. If it contains more than one section, the path
	 * forks here and the user has to decide which path to follow. If there are
	 * no successors, a {@link #gateID} has to be defined, which is then one
	 * possible goal of the level.
	 */
	public ArrayList<Integer> successorSections;

	/**
	 * If a gate has to be created at the end of this edge, this id has to be
	 * greater than 0. Otherwise no gate is created.
	 */
	public int gateID;

	/**
	 * Checks if the information of this section is complete to use it.
	 * 
	 * @see #MAX_HORIZONTAL_ANGLE, @see {@link #MAX_ABS_VERTICAL_ANGLE}
	 * @return true information is complete
	 * @return false some information is missing
	 */
	public boolean isComplete() {
		if (id > 0 && length > 0.0 && minVerticalAngle <= maxVerticalAngle
				&& minHorizontalAngle <= maxHorizontalAngle
				&& minVerticalAngle >= -MAX_ABS_VERTICAL_ANGLE
				&& maxVerticalAngle <= MAX_ABS_VERTICAL_ANGLE
				&& minHorizontalAngle >= MAX_ABS_HORIZONTAL_ANGLE
				&& maxHorizontalAngle <= MAX_ABS_HORIZONTAL_ANGLE) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Section:\n");
		stringBuilder.append("id: " + id + ",\n");
		stringBuilder.append("horizontal angle from " + minHorizontalAngle
				+ "° to " + maxHorizontalAngle + "°\n");
		stringBuilder.append("vertical angle from " + minVerticalAngle
				+ "° to " + maxVerticalAngle + "°\n");
		stringBuilder.append("lenth: " + length + ",\n");
		stringBuilder.append("successorSections: " + successorSections + ",\n");
		stringBuilder.append("gateID: " + gateID);
		return stringBuilder.toString();
	}
}
