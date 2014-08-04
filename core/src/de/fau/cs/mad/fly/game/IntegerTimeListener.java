package de.fau.cs.mad.fly.game;

/** Interface for all features that listen to changes of the integer time. */
public interface IntegerTimeListener {
	/**
	 * This method is called, if the integer time has changed.
	 * 
	 * @param timeLeft				The time in seconds that is left for the player.
	 * @param timeSinceStart		The time in seconds since the start.
	 * 
	 * @return true, if the listener wants to be removed from the list after the call, false otherwise.
	 */
	public boolean integerTimeChanged(int timeLeft, int timeSinceStart);
}