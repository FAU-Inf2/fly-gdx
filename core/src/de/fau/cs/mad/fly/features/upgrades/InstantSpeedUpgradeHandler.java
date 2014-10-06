package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.InstantSpeedUpgrade;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.sound.Playback;

/**
 * Used to display and handle instant speed upgrades.
 * <p>
 * Do not mix instant speed upgrades with a linear speed upgrade.
 * 
 * @author Tobi
 * 
 */
public class InstantSpeedUpgradeHandler extends CollectibleObjects implements IFeatureInit, IFeatureUpdate {
    
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
     * Determines if the speed upgrade change is forever.
     */
    private boolean isInfinite = false;
    
    /**
     * A backup of the old speed.
     */
    private float oldSpeed;
    
    /**
     * The speed factor of the speed upgrade.
     */
    private float upgradeSpeedFactor;
    
    /**
     * The duration of the speed upgrade.
     */
    private float upgradeDuration;
    
    /**
     * The duration the speed upgrade was already used.
     */
    private float duration;

    private Playback sound;

    /**
     * Creates a new instant speed upgrade handler.
     */
    public InstantSpeedUpgradeHandler() {
        super("InstantSpeedUpgrade");
    }
    
    @Override
    public void init(GameController game) {
        plane = game.getPlayer().getPlane();
    }
    
    @Override
    protected void handleCollecting(Collectible c) {
        sound = GameController.getInstance().getAudioManager().play(AudioManager.Sounds.SONIC);
        sound.setVolume(0.3f);
        InstantSpeedUpgrade upgrade = (InstantSpeedUpgrade) c;
        
        isCollected = true;
        
        this.upgradeSpeedFactor = upgrade.getSpeedFactor();
        this.upgradeDuration = upgrade.getDuration();
        if (upgradeDuration <= 0.0f) {
            isInfinite = true;
        }
        
        oldSpeed = plane.getPlaneSpeed();
        plane.setCurrentSpeed(oldSpeed * upgradeSpeedFactor);
        duration = 0.0f;
        
        InfoOverlay.getInstance().setOverlay(I18n.t("speedUpgradeCollected") + "\n" + I18n.t("bonus") + " " + ((int) (upgradeSpeedFactor * 100.0f) + "% " + I18n.t("for") + " " + (int) upgradeDuration) + "s.", 3);
    }
    
    @Override
    public void update(float delta) {
        if (!isCollected) {
            return;
        }
        
        duration += delta;

        
        if (!isInfinite && duration >= upgradeDuration) {
            plane.setCurrentSpeed(oldSpeed);
            sound.stop();
            isCollected = false;
        }
    }
}