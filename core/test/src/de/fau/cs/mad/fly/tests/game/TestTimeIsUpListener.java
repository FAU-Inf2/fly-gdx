package de.fau.cs.mad.fly.tests.game;

import de.fau.cs.mad.fly.game.TimeIsUpListener;

public class TestTimeIsUpListener implements TimeIsUpListener {

	boolean timeIsUp = false;
	
	@Override
	public void timeIsUp() {
		timeIsUp = true;
	}
	
	public boolean isTimeUp() {
		return timeIsUp;
	}
	
	public void resetTimeIsUp() {
		timeIsUp = false;
	}

}
