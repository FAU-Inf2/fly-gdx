package de.fau.cs.mad.fly.levelLoader;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Gate {

	public static final int NO_GATE = -1;
	
	private static int nextId = 1;

	/**
	 * ID which identifies the object.
	 */
	private int id;

	public ModelInstance modelInstance;

	
	public Gate(){
		this.id = nextId++;
	}
	
	
	/**
	 * 
	 * @return {@link #id}
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Gate)) {
			return false;
		}
		Gate other = (Gate) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}


}
