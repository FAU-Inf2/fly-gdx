package de.fau.cs.mad.fly.features.upgrades;

import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.game.CollectibleObjects;
import de.fau.cs.mad.fly.features.overlay.InfoOverlay;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.ResizeGatesUpgrade;
import de.fau.cs.mad.fly.sound.AudioManager;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.res.GateCircuit;
import de.fau.cs.mad.fly.res.GateGoal;

/**
 * Used to display and handle gate resize upgrades.
 * 
 * @author Tobi
 * 
 */
public class ResizeGatesUpgradeHandler extends CollectibleObjects implements IFeatureInit {
    private GateCircuit gateCircuit;
    
    /**
     * Creates an resize gates upgrade handler.
     */
    public ResizeGatesUpgradeHandler() {
        super("ResizeGatesUpgrade");
    }
    
    @Override
    public void init(GameController game) {
        gateCircuit = game.getLevel().getGateCircuit();
    }
    
    @Override
    protected void handleCollecting(Collectible c) {
        GameController.getInstance().getAudioManager().play(AudioManager.Sounds.DAMN_SON);
        ResizeGatesUpgrade upgrade = (ResizeGatesUpgrade) c;
        resizeGates(upgrade.getScale());
        InfoOverlay.getInstance().setOverlay(I18n.t("resizeGatesUpgradeCollected"), 3);
    }
    
    /**
     * Resizes the gates and the gate holes with the scaling vector.
     * 
     * @param scale
     *            The scaling vector of the resizing of the gates.
     */
    private void resizeGates(Vector3 scale) {
        Vector3 scaling = new Vector3();
        for (GateGoal g : gateCircuit.allGateGoals()) {
            if (g.getDisplay() != null) {
                g.getDisplay().transform.scl(scale);
                g.getDisplay().transform.getScale(scaling);
                g.getDisplay().getRigidBody().getCollisionShape().setLocalScaling(scaling);
            }
            g.transform.scl(scale);
            g.transform.getScale(scaling);
            g.getRigidBody().getCollisionShape().setLocalScaling(scaling);
        }
    }
}