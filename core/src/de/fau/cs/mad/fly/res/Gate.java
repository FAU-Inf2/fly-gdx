package de.fau.cs.mad.fly.res;

import java.util.ArrayList;

public class Gate {

	public static final int NO_GATE = -1;

	public int id;

	public String model;

	public float[] transformMatrix;
	
	public ArrayList<Integer> successors = new ArrayList<Integer>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && obj instanceof Gate && id == ((Gate) obj).id;
	}


}
