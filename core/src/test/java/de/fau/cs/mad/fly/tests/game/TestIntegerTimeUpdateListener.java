package de.fau.cs.mad.fly.tests.game;

import de.fau.cs.mad.fly.game.IntegerTimeListener;

public class TestIntegerTimeUpdateListener implements IntegerTimeListener {

	int time = 0;
	int timeSinceStart = 0;
	
	@Override
	public boolean integerTimeChanged(int newTime, int timeSinceStart) {
		this.time = newTime;
		this.timeSinceStart = timeSinceStart;
		return false;
	}
	
	public int getTime() {
		return time;
	}

}
