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
	 * Name of the object which is used as level border. Default is outer space.
	 */
	public String levelBorder = "spacesphere.obj";

	/**
	 * Radius of the Level which defines the outer boundary which should be
	 * never reached by the user. The default level border defines a sphere with
	 * radius 100.
	 */
	public float radius = 100.0f;

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
}
