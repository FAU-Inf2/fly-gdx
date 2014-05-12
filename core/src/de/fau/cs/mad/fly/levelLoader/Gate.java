package de.fau.cs.mad.fly.levelLoader;

import com.badlogic.gdx.math.Vector3;

public class Gate {

	public static final int NO_GATE = -1;
	
	private static int nextId = 1;

	/**
	 * ID which identifies the object.
	 */
	private int id;

	/**
	 * Position of the gate.
	 */
	private Vector3 position;
	
	public Gate(Vector3 position) {
		setPosition(position);
		this.id = nextId++;
	}

	/**
	 * 
	 * @return {@link #id}
	 */
	public int getId() {
		return id;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Compares the {@link #id} of two gates.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Gate) {
			return this.getId() == ((Gate) o).getId();
		} else {
			return false;
		}
	}
}
