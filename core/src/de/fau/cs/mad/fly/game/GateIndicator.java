package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.fau.cs.mad.fly.Assets;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureFinishLevel,
		IRenderableFeature, IFeatureGatePassed {

	private ModelInstance arrowModel;

	@Override
	public void finish(GameController game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GameController game) {
		Assets.loadArrow();
		arrowModel = new ModelInstance(Assets.manager.get(Assets.arrow));
	}

	@Override
	public void gatePassed(GameController gameController) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Environment environment, PerspectiveCamera camera,
			ModelBatch batch, float delta) {
		batch.begin(camera);
		if (arrowModel != null) {
			batch.render(arrowModel);
		}
		batch.end();

	}

}
