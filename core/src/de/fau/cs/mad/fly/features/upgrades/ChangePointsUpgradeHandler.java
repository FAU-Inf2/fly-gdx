package de.fau.cs.mad.fly.features.upgrades;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.ChangePointsUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Used to display and handle change points upgrades.
 * 
 * @author Tobi
 * 
 */
public class ChangePointsUpgradeHandler extends CollectibleObjects {
    /**
     * Creates a new change points upgrade handler.
     */
    public ChangePointsUpgradeHandler() {
        super("ChangePointsUpgrade");
    }
    
    @Override
    protected void handleCollecting(Collectible c) {
        GameController.getInstance().getAudioManager().play(AudioManager.Sounds.DAMN_SON);
        ChangePointsUpgrade upgrade = (ChangePointsUpgrade) c;
        GameController.getInstance().getScoreController().addBonusPoints(upgrade.getPointsChange());
        InfoOverlay.getInstance().setOverlay(I18n.t("pointUpgradeCollected") + "\n" + I18n.t("bonus") + " " + upgrade.getPointsChange() + "", 3);
    }
}