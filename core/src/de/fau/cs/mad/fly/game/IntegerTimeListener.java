package de.fau.cs.mad.fly.game;

/** Interface for all features that listen to changes of the integer time. */
public interface IntegerTimeListener {
	/** This method is called, if the integer time has changed */
	public void integerTimeChanged(int newTime);
}
