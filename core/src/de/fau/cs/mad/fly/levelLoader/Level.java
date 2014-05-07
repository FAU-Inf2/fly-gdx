package de.fau.cs.mad.fly.levelLoader;

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
	public double radius = 0.0;

	/**
	 * Calculates the {@link #radius} of the Level as a sum of the length of all
	 * sections. To make sure the radius can never be reached, the final result
	 * is increased by 10 %.
	 */
	public void calculateRadius() {
		radius = super.firstSection.length;
		for (Section section : super.sections) {
			radius += section.length;
		}
		radius *= 1.1;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Level: ");
		stringBuilder.append("radius: " + radius + ",\n");
		stringBuilder.append("extends RawLevel: " + super.toString());
		return stringBuilder.toString();
	}
}
