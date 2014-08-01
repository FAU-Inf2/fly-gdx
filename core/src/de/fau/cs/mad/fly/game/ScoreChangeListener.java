package de.fau.cs.mad.fly.game;

public interface ScoreChangeListener {
	
	/**
	 * Is called if the score has changed.
	 */
	public void scoreChanged(int newScore);
}