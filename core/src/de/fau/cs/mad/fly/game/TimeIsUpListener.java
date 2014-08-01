package de.fau.cs.mad.fly.game;

public interface TimeIsUpListener {
	
	/**
	 * Is called if the time of the level is over.
	 * 
	 * @return true, if the listener wants to be removed from the list after the call, false otherwise.
	 */
	public boolean timeIsUp();
}
