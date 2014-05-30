package de.fau.cs.mad.fly.game;

import de.fau.cs.mad.fly.features.IFeatureFinishLevel;
import de.fau.cs.mad.fly.features.IFeatureGatePassed;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.res.Gate;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureFinishLevel,
		IFeatureRender, IFeatureGatePassed {

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GameController gameController) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gatePassed(Gate passedGate) {
		// TODO Auto-generated method stub

	}

}
