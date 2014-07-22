package de.fau.cs.mad.fly.game;

/** Interface for all features that listen to changes of the integer time. */
public interface IntegerTimeListener {
	/**
	 * This method is called, if the integer time has changed.
	 * 
	 * @return true, if the listener wants to be removed from the list after the call, false otherwise.
	 */
	public boolean integerTimeChanged(int newTime);
}