package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

public class TimeController {

	float initTime;
	float currentTime;
	long initTimeStamp;
	List<IntegerTimeListener> integerTimeListeners;
	List<TimeIsUpListener> timeIsUpListeners;

	public TimeController() {
		initTime = 0f;
		integerTimeListeners = new ArrayList<IntegerTimeListener>();
		timeIsUpListeners = new ArrayList<TimeIsUpListener>();
	}
	
	/** Sets the time to a certain second value */
	public void initTimer(float seconds) {
		this.initTime = seconds;
		initTimeStamp = System.currentTimeMillis();
		currentTime = initTime;
	}

	/**
	 * Adds the delta to the current time. <p>
	 * 
	 * If time is up, {@link #timeIsUp()} is called, <p> 
	 * if the integer of the time changes (ceiled) {@link #integerTimeChanged()} is called.
	 */
	public void checkTime() {
		int timeBefore = (int) Math.ceil(currentTime);
		Gdx.app.log("checkTime", String.valueOf(timeBefore));
		currentTime = initTime - (System.currentTimeMillis()-initTimeStamp)/1000;
		Gdx.app.log("checkTime", "current time: " + String.valueOf(currentTime));
		if(currentTime < 1) {
			timeIsUp();
		}
		if(timeBefore != (int) Math.ceil(currentTime)) {
			integerTimeChanged();
		}
	}

	/** Notifies all {@link TimeIsUpListener}s. */
	private void timeIsUp() {
		for(TimeIsUpListener listener : timeIsUpListeners) {
			listener.timeIsUp();
			Gdx.app.log("TimeIsUP", "TimeIsUp");
		}
	}

	/** Notifies all {@link IntegerTimeListener} */
	private void integerTimeChanged() {
		for(IntegerTimeListener listener : integerTimeListeners) {
			listener.integerTimeChanged((int) Math.ceil(currentTime));
			Gdx.app.log("timeChanged", "timeChanged: " + String.valueOf((int) Math.ceil(currentTime)));
		}
	}
	
	/** Register a new {@link IntegerTimeListener} */
	public void registerIntegerTimeListener(IntegerTimeListener newListener) {
		integerTimeListeners.add(newListener);
	}
	
	/** Register a new {@link TimeIsUpListener} */
	public void registerTimeIsUpListener(TimeIsUpListener newListener) {
		timeIsUpListeners.add(newListener);
	}
}
