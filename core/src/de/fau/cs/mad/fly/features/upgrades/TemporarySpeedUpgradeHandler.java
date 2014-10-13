package de.fau.cs.mad.fly.features.upgrades;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.SpeedUpgradeEffect;
import de.fau.cs.mad.fly.features.upgrades.types.TemporarySpeedUpgrade;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.IPlane;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.sound.Playback;

/**
 * Used to display and handle instant speed upgrades.
 * <p>
 * Do not mix instant speed upgrades with a linear speed upgrade.
 * 
 * @author Lukas Hahmann <lukas.hahmann@gmail.com>
 * 
 */
public class TemporarySpeedUpgradeHandler extends CollectibleObjects implements IFeatureInit, IFeatureUpdate, IFeatureFinish {
    
    /**
     * The plane which speed should be changed after a speed upgrade was
     * collected.
     */
    private IPlane plane;
    
    private boolean upgradesActive;
    
    private List<SpeedUpgradeEffect> upgrades;
    
    private float speedUpFactor;
    
    /**
     * Creates a new {@link TemporarySpeedUpgradeHandler}.
     */
    public TemporarySpeedUpgradeHandler() {
        super(TemporarySpeedUpgrade.TYPE);
        upgradesActive = false;
        upgrades = new ArrayList<SpeedUpgradeEffect>();
        speedUpFactor = SpeedUpgradeEffect.NO_SPEEDUP;
    }
    
    @Override
    public void init(GameController game) {
        plane = game.getPlayer().getPlane();
    }
    
    @Override
    protected void handleCollecting(Collectible c) {
        if (c instanceof TemporarySpeedUpgrade) {
            TemporarySpeedUpgrade upgrade = (TemporarySpeedUpgrade) c;
            upgrades.add(upgrade.getEffect());
            c.dispose();
            
            // show info message
            StringBuilder builder = new StringBuilder();
            builder.append(I18n.t("speedUpgradeCollected"));
            builder.append("\n");
            builder.append(I18n.t("bonus"));
            builder.append(" ");
            DecimalFormat df = new DecimalFormat("0");
            String factorRounded = df.format(upgrade.getEffect().getMaxSpeedupFactor() * 100f);
            builder.append(factorRounded);
            builder.append(" %");
            InfoOverlay.getInstance().setOverlay(builder.toString(), 3);
            
            // start playing sound if not yet playing
			GameController.getInstance().getAudioManager().play(AudioManager.Sounds.PICKUP);
			upgradesActive = true;
        }
        
    }
    
    @Override
    public void update(float delta) {
        int size = upgrades.size();
        if (size > 0) {
            speedUpFactor = SpeedUpgradeEffect.NO_SPEEDUP;
            SpeedUpgradeEffect upgrade;
            for (int i = size - 1; i >= 0; i--) {
                upgrade = upgrades.get(i);
                upgrade.update(delta * 1000f);
                if (upgrade.isActive()) {
                    speedUpFactor *= upgrade.getCurrentSpeedupFactor();
                } else {
                    upgrades.remove(i);
                }
            }
            plane.setCurrentSpeed(plane.getBaseSpeed() * speedUpFactor);
        } else if (upgradesActive) {
            upgradesActive = false;
            speedUpFactor = SpeedUpgradeEffect.NO_SPEEDUP;
            plane.setCurrentSpeed(plane.getBaseSpeed() * speedUpFactor);
        }
    }

    @Override
    public void finish() {
    }
}