package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.Assets;
import de.fau.cs.mad.fly.features.IFeatureFinishLevel;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureFinish;
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

	private ModelInstance arrowModel;
	/** visualize current target point */
	private ModelInstance cube;
	private GameController gameController;
	private ModelBuilder modelBuilder;
	private ModelBatch batch;

	private ModelInstance debugModel;

	@Override
	public void init(GameController game) {
		this.gameController = game;
		Assets.loadArrow();
		arrowModel = new ModelInstance(Assets.manager.get(Assets.arrow));
		batch = game.batch;
		
		//TODO: remove this if arrow is working
		this.modelBuilder = new ModelBuilder();
		cube = new ModelInstance(modelBuilder.createBox(0.1f, 0.1f, 0.1f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal));
		cube.transform.setToTranslation(1, 1, 10);
	}


	@Override
	public void render(float delta) {
		Vector3 targetPosition = new Vector3(1, 1, 10);
		Vector3 cameraPosition = gameController.getCamera().position.cpy();
		Vector3 cameraDirection = gameController.getCamera().direction.cpy();
		Vector3 up = gameController.getCamera().up.cpy();
		Vector3 down = up.cpy().scl(-1);

		// The arrow should be in the middle of the screen, a little before the
		// camera, that it is always visible and below the vertical midpoint.
		Vector3 gatePositionRelativeToCamera = cameraDirection.scl(3)
				.add(gameController.getCamera().position).add(down);

		Vector3 vectorToTarget = targetPosition.cpy();

		vectorToTarget = vectorToTarget.sub(gatePositionRelativeToCamera).nor();

		// calculate orthogonal up vector
		up.crs(vectorToTarget).crs(vectorToTarget).nor();

		Vector3 cross = vectorToTarget.scl(-1).cpy().crs(up).nor();
		vectorToTarget.nor();

		// create local coordinate system for the arrow. All axes have to be
		// normalized, otherwise, the arrow is scaled.
		float[] values = { up.x, up.y, up.z, 0f, cross.x, cross.y, cross.z, 0f,
				vectorToTarget.x, vectorToTarget.y, vectorToTarget.z, 0f, 0f,
				0f, 0f, 1f };

		Matrix4 transformationMatrix = new Matrix4(values);

		arrowModel.transform = transformationMatrix
				.trn(gatePositionRelativeToCamera);

		batch.render(arrowModel);
		batch.render(cube);
		//batch.render(debugModel);
	}

	@Override
	public void gatePassed(Gate passedGate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
