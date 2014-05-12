package de.fau.cs.mad.fly.levelLoader;

public class Gate {
	
	/**
	 * ID which identifies the object.
	 */
	private int id;
	
	/**
	 * 
	 * @return {@link #id}
	 */
	public int getId() {
		return id;
	}
	
	
	/**
	 * Compares the {@link #id} of two gates.
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Gate) {
			return this.getId() == ((Gate)o).getId(); 
		}
		else {
			return false;
		}
	}
}
