package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

public class TimeController {

	private float initTimeInSeconds;
	private float currentTimeInSeconds;
	private long initTimeStampInMilliSeconds;

	private boolean paused;
	private float pauseTimeInSeconds;
	private long pauseTimeStampInMilliSeconds;

	private List<IntegerTimeListener> integerTimeListeners;
	private List<TimeIsUpListener> timeIsUpListeners;

	public TimeController() {
		initTimeInSeconds = 0f;
		integerTimeListeners = new ArrayList<IntegerTimeListener>();
		timeIsUpListeners = new ArrayList<TimeIsUpListener>();
	}

	/**
	 * Sets the time to a certain second value.
	 * 
	 * @throws IllegalArgumentException
	 *             for negative parameter.
	 */
	public void initAndStartTimer(float seconds) {
		if (seconds < 0) {
			throw new IllegalArgumentException("TimeController.initTimer(" + String.valueOf(seconds) + ") got a negative parameter.");
		}
		initTimeInSeconds = seconds;
		initTimeStampInMilliSeconds = System.currentTimeMillis();
		currentTimeInSeconds = initTimeInSeconds;
		pauseTimeStampInMilliSeconds = 0;
		pauseTimeInSeconds = 0;
		paused = false;
		integerTimeChanged();
	}

	/**
	 * Adds the delta to the current time.
	 * <p>
	 * 
	 * If time is up, {@link #timeIsUp()} is called,
	 * <p>
	 * if the integer of the time changes (ceiled) {@link #integerTimeChanged()}
	 * is called. Time will never be below 0.
	 */
	public void checkTime() {
		if (!paused) {
			int timeBefore = (int) Math.ceil(currentTimeInSeconds);
			currentTimeInSeconds = initTimeInSeconds - (System.currentTimeMillis() - initTimeStampInMilliSeconds) / 1000 + pauseTimeInSeconds;
			if (currentTimeInSeconds < 1) {
				currentTimeInSeconds = 0;
				timeIsUp();
			}
			if (timeBefore != (int) Math.ceil(currentTimeInSeconds)) {
				integerTimeChanged();
			}
		}
	}

	/**
	 * Pauses the {@link TimeController} until {@link #resume()} is called.
	 * <p>
	 * If the {@link TimeController} is already paused, nothing happens.
	 */
	public void pause() {
		if (!paused) {
			paused = true;
			pauseTimeStampInMilliSeconds = System.currentTimeMillis();
		}
	}

	/**
	 * Restarts the {@link TimeController} after {@link #pause()} was called.
	 * <p>
	 * If {@link #resume()} is called, without the {@link TimeController} being
	 * paused, nothing happens.
	 */
	public void resume() {
		if (paused) {
			paused = false;
			pauseTimeInSeconds += (System.currentTimeMillis() - pauseTimeStampInMilliSeconds) / 1000;
			pauseTimeStampInMilliSeconds = 0;
		}
	}

	/** Notifies all {@link TimeIsUpListener}s. */
	private void timeIsUp() {
		for (TimeIsUpListener listener : timeIsUpListeners) {
			listener.timeIsUp();
		}
	}

	/** Notifies all {@link IntegerTimeListener} */
	private void integerTimeChanged() {
		for (IntegerTimeListener listener : integerTimeListeners) {
			listener.integerTimeChanged((int) Math.ceil(currentTimeInSeconds));
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
