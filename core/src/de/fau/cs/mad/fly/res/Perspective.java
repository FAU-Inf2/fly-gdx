package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by danyel on 15/05/14.
 */
public class Perspective {
	public Vector3 position;
	public Vector3 viewDirection;
	public Vector3 upDirection;

	public Perspective(Vector3 position, Vector3 viewDirection, Vector3 upDirection) {
		this.position = position;
		this.viewDirection = viewDirection;
		this.upDirection = upDirection;
	}

	public Perspective() {
		this(new Vector3(), new Vector3(0, 1, 0), new Vector3(0, 0, 1));
	}

	public String toString() {
		return "#<Perspective position=" + position + " viewDirection=" + viewDirection + " upDirection=" + upDirection + ">";
	}
}
