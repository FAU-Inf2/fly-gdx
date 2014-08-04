package de.fau.cs.mad.fly.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the time in the game and calls listener if time in seconds has changed or the game time is over.
 * 
 * @author Lukas Hahmann 
 *
 */
public class TimeController {
    
    private float initTimeInSeconds;
    private float currentTimeInSeconds;
    private float timeSinceStartInSeconds;
    private long initTimeStampInMilliSeconds;
    
    private boolean paused;
    private float pauseTimeInSeconds;
    private long pauseTimeStampInMilliSeconds;
    
    private float bonusTime;
    
    private List<IntegerTimeListener> integerTimeListeners;
    private List<TimeIsUpListener> timeIsUpListeners;
    
    private int size;
    
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
            throw new IllegalArgumentException("TimeController.initTimer(" + seconds + ") got a negative parameter.");
        }
        initTimeInSeconds = seconds;
        initTimeStampInMilliSeconds = System.currentTimeMillis();
        currentTimeInSeconds = initTimeInSeconds;
        timeSinceStartInSeconds = 0f;
        pauseTimeStampInMilliSeconds = 0;
        pauseTimeInSeconds = 0;
        bonusTime = 0;
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
            timeSinceStartInSeconds = (System.currentTimeMillis() - initTimeStampInMilliSeconds) / 1000 - pauseTimeInSeconds;
            currentTimeInSeconds = initTimeInSeconds - (System.currentTimeMillis() - initTimeStampInMilliSeconds) / 1000 + pauseTimeInSeconds + bonusTime;
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
     * Adds bonus time in seconds to the bonus time.
     * @param bonusTime		The bonus time in seconds to add.
     */
    public void addBonusTime(float bonusTime) {
    	this.bonusTime += bonusTime;
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
    
    /**
     * Getter for the current integer time in seconds.
     * @return Current integer time in seconds.
     */
    public int getIntegerTime() {
    	return (int) Math.ceil(currentTimeInSeconds);
    }
    
    /**
     * Getter for the integer time since the start in seconds.
     * @return Integer time since the start in seconds.
     */
    public int getIntegerTimeSinceStart() {
    	return (int) Math.ceil(timeSinceStartInSeconds);
    }
    
    /** Notifies all {@link TimeIsUpListener}s. */
    private void timeIsUp() {
    	size = timeIsUpListeners.size();
        for (int i = 0; i < size; i++) {
            if(timeIsUpListeners.get(i).timeIsUp()) {
            	removeTimeIsUpListener(timeIsUpListeners.get(i));
            	size--;
            	i--;
            }
        }
    }
    
    /** Notifies all {@link IntegerTimeListener} */
    private void integerTimeChanged() {
    	size = integerTimeListeners.size();
    	int integerTime = getIntegerTime();
    	int integerTimeSinceStart = getIntegerTimeSinceStart();
    	
        for (int i = 0; i < size; i++) {
            if(integerTimeListeners.get(i).integerTimeChanged(integerTime, integerTimeSinceStart)) {
            	removeIntegerTimeListener(integerTimeListeners.get(i));
            	size--;
            	i--;
            }
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
    
    /** Removes the registered {@link IntegerTimeListener} */
    public void removeIntegerTimeListener(IntegerTimeListener newListener) {
        integerTimeListeners.remove(newListener);
    }
    
    /** Removes the registered {@link TimeIsUpListener} */
    public void removeTimeIsUpListener(TimeIsUpListener newListener) {
        timeIsUpListeners.remove(newListener);
    }
}
