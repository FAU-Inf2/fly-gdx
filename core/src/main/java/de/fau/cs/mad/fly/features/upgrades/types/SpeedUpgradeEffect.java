package de.fau.cs.mad.fly.features.upgrades.types;

/**
 * Used to calculate the speed factor of the spaceship, especially when more
 * than one speed upgrade is collected.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class SpeedUpgradeEffect {
    
    private int speedupTimeInMilliSeconds;
    private int maxSpeedTimeInMilliSeconds;
    private int slowdownTimeInMilliSeconds;
    private int currentTimeInMilliSeconds;
    private int totalTimeInMilliSeconds;
    
    private float maxSpeedupFactor;
    private float currentSpeedFactor;
    public static final float NO_SPEEDUP = 1f;
    
    /**
     * Creates a new {@link SpeedUpgradeEffect} with the specified parameter.
     * <p>
     * 
     * @param maxSpeedupFactor
     * @param speedupTimeInMilliSeconds
     * @param maxSpeedTimeInMilliSeconds
     * @param slowdownTimeInMilliSeconds
     */
    public SpeedUpgradeEffect(float maxSpeedupFactor, int speedupTimeInMilliSeconds, int maxSpeedTimeInMilliSeconds, int slowdownTimeInMilliSeconds) {
        if (maxSpeedupFactor > 0f) {
            this.maxSpeedupFactor = maxSpeedupFactor;
        } else {
            throw new IllegalArgumentException("TemporarySpeedUpgrade.Constructor: maxSpeedupFactor has to be > 0f (is " + maxSpeedupFactor + ")");
        }
        this.currentSpeedFactor = NO_SPEEDUP;
        
        if (speedupTimeInMilliSeconds >= 0) {
            this.speedupTimeInMilliSeconds = speedupTimeInMilliSeconds;
        } else {
            throw new IllegalArgumentException("TemporarySpeedUpgrade.Constructor: speedupTimeInMilliSeconds has to be >= 0 (is " + speedupTimeInMilliSeconds + ")");
        }
        
        if (maxSpeedTimeInMilliSeconds >= 0) {
            this.maxSpeedTimeInMilliSeconds = maxSpeedTimeInMilliSeconds;
        } else {
            throw new IllegalArgumentException("TemporarySpeedUpgrade.Constructor: maxSpeedTimeInMilliSeconds has to be >= 0 (is " + maxSpeedTimeInMilliSeconds + ")");
        }
        
        if (slowdownTimeInMilliSeconds >= 0) {
            this.slowdownTimeInMilliSeconds = slowdownTimeInMilliSeconds;
        } else {
            throw new IllegalArgumentException("TemporarySpeedUpgrade.Constructor: slowdownTimeInMilliSeconds has to be >= 0 (is " + slowdownTimeInMilliSeconds + ")");
        }
        
        this.totalTimeInMilliSeconds = speedupTimeInMilliSeconds + maxSpeedTimeInMilliSeconds + slowdownTimeInMilliSeconds;
        this.currentTimeInMilliSeconds = 0;
    }
    
    /**
     * Adds the delta to the {@link #currentTimeInMilliSeconds}. If it is then
     * still active, the {@link #currentSpeedFactor} is updated.
     * 
     * @param deltaInMilliSeconds
     */
    public void update(float deltaInMilliSeconds) {
        currentTimeInMilliSeconds += deltaInMilliSeconds;
        
        if (isActive()) {
            if (currentTimeInMilliSeconds < speedupTimeInMilliSeconds) {
                // speedup
                currentSpeedFactor = NO_SPEEDUP + (maxSpeedupFactor - 1) * currentTimeInMilliSeconds / speedupTimeInMilliSeconds;
            } else if (currentTimeInMilliSeconds < speedupTimeInMilliSeconds + maxSpeedTimeInMilliSeconds) {
                // max speed
                currentSpeedFactor = maxSpeedupFactor;
            } else {
                // slowdown
                currentSpeedFactor = NO_SPEEDUP + (maxSpeedupFactor - 1) * (totalTimeInMilliSeconds - currentTimeInMilliSeconds) / slowdownTimeInMilliSeconds;
            }
        } else {
            currentSpeedFactor = NO_SPEEDUP;
        }
    }
    
    /**
     * Checks weather this upgrade is active or if its time is up.
     * 
     * @return
     */
    public boolean isActive() {
        return currentTimeInMilliSeconds < totalTimeInMilliSeconds;
    }
    
    /**
     * Getter for the {@link #currentSpeedFactor}, dependent on the passed time
     * since collecting it.
     * 
     * @return {@link #currentSpeedFactor}
     */
    public float getCurrentSpeedupFactor() {
        return currentSpeedFactor;
    }
    
    /**
     * Getter for the {@link #maxSpeedupFactor}, that is independent of the time the effect is active.
     */
    public float getMaxSpeedupFactor() {
        return maxSpeedupFactor;
    }
    
}
