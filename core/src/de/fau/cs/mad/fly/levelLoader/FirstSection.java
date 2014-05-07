package de.fau.cs.mad.fly.levelLoader;

/**
 * Child of {@link Section} wich is the only section that stores an absolute
 * direction as a 3d vector.
 * It indicates the direction the player faces when starting a level.
 * 
 * @author Lukas Hahmann
 * 
 */
public class FirstSection extends Section {
	/**
	 * x-coordinate of 3d direction vector 
	 */
	public double directionX = 0.0;
	/**
	 * y-coordinate of 3d direction vector 
	 */
	public double directionY = 0.0;
	/**
	 * y-coordinate of 3d direction vector 
	 */
	public double directionZ = 0.0;

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("FirstSection:\n");
		stringBuilder.append("direction: (" + directionX + ", " + directionY
				+ ", " + directionZ + "),\n");
		stringBuilder.append("extends: " + super.toString());
		return stringBuilder.toString();
	}
}
