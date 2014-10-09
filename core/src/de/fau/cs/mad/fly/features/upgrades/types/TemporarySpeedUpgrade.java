package de.fau.cs.mad.fly.features.upgrades.types;

import de.fau.cs.mad.fly.game.GameModel;

/**
 * An upgrade to change the current speed of the plane.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class TemporarySpeedUpgrade extends Collectible {
    
    /** Type of this upgrade */
    public static final String TYPE = "TemporarySpeedUpgrade";
    
    /** Effect of this {@link TemporarySpeedUpgrade}. */
    private SpeedUpgradeEffect effect;
    
    /**
     * Creates a new {@link TemporarySpeedUpgrade}.
     * 
     * @param model
     *            The model of the upgrade.
     * @param effect
     *            of this upgrade
     */
    public TemporarySpeedUpgrade(GameModel model, SpeedUpgradeEffect effect) {
        super(model);
        this.effect = effect;
    }
    
    /**
     * Getter for the {@link #effect} of this upgrade.
     */
    public SpeedUpgradeEffect getEffect() {
        return effect;
    }
    
    @Override
    public String getType() {
        return TYPE;
    }
}