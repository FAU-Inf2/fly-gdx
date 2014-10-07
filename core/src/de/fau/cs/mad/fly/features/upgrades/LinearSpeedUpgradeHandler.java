package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.LinearSpeedUpgrade;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.IPlane;

/**
 * Used to display and handle linear increasing and decreasing speed upgrades.
 * <p>
 * Do not mix linear speed upgrades or a linear speed upgrade with instant speed upgrades.
 * 
 * @author Tobi
 * 
 */
public class LinearSpeedUpgradeHandler extends CollectibleObjects implements IFeatureInit, IFeatureUpdate {
    
    /**
     * The plane which speed should be changed after a speed upgrade was
     * collected.
     */
    private IPlane plane;
    
    /**
     * Determines if the speed upgrade was collected and has to be handled.
     */
    private boolean isCollected = false;
    
    /**
     * A backup of the old speed.
     */
    private float oldSpeed;
    
    /**
     * A backup for the maximum speed.
     */
    private float maxSpeed;
    
    /**
     * The speed factor of the speed upgrade increase.
     */
    private float upgradeIncreaseFactor;
    
    /**
     * The duration of the speed upgrade increase.
     */
    private float upgradeIncreaseDuration;
    
    /**
     * The speed factor of the speed upgrade decrease.
     */
    private float upgradeDecreaseFactor;
    
    /**
     * The duration the speed upgrade was already used.
     */
    private float duration;
    
    /**
     * Creates a new linear speed upgrade handler.
     */
    public LinearSpeedUpgradeHandler() {
        super("LinearSpeedUpgrade");
    }
    
    @Override
    public void init(GameController game) {
        plane = game.getPlayer().getPlane();
    }
    
    @Override
    protected void handleCollecting(Collectible c) {
        GameController.getInstance().getAudioManager().play(AudioManager.Sounds.DAMN_SON);
        LinearSpeedUpgrade upgrade = (LinearSpeedUpgrade) c;
        
        isCollected = true;
        
        oldSpeed = plane.getPlaneSpeed();
        duration = 0.0f;
        
        upgradeIncreaseFactor = upgrade.getIncreaseFactor();
        upgradeIncreaseDuration = upgrade.getIncreaseDuration();
        upgradeDecreaseFactor = upgrade.getDecreaseFactor();
    }
    
    @Override
    public void update(float delta) {
        if (!isCollected) {
            return;
        }
        
        duration += delta;
        
        if (duration <= upgradeIncreaseDuration) {
            maxSpeed = oldSpeed + duration * upgradeIncreaseFactor;
            plane.setCurrentSpeed(maxSpeed);
        } else if (duration > upgradeIncreaseDuration) {
            if (plane.getCurrentSpeed() < oldSpeed) {
                plane.setCurrentSpeed(oldSpeed);
                isCollected = false;
            } else {
                plane.setCurrentSpeed(maxSpeed - (duration - upgradeIncreaseDuration) * upgradeDecreaseFactor);
            }
        }
    }
}