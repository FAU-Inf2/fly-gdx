package de.fau.cs.mad.fly.geo;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by danyel on 15/05/14.
 */
public class Perspective {
	public Vector3 position;
	public Vector3 viewDirection;
	public float angle;

	public Perspective(Vector3 position, Vector3 viewDirection, float angle) {
		this.position = position;
		this.viewDirection = viewDirection;
		this.angle = angle;
	}

	public Perspective() {
		this(new Vector3(), new Vector3(0, 0, 1), 0);
	}
}
