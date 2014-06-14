package de.fau.cs.mad.fly.features.game;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;

/**
 * This class implements the function to show in the game small arrows that
 * indicate the direction of the next gates.
 * 
 * @author Lukas Hahmann
 * 
 */
public class GateIndicator implements IFeatureInit, IFeatureRender {
	
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;

	private GameObject arrowModel;

	@Override
	public void init(GameController game) {
		this.gameController = game;
		Assets.loadArrow();
		arrowModel = new GameObject(Assets.manager.get(Assets.arrow));
		batch = game.getBatch();
		
		environment = gameController.getLevel().getEnvironment();
	}

	@Override
	public void render(float delta) {
		for (Level.Gate gate : gameController.getLevel().currentGates()) {
			//System.out.println("nextGates" + gameController.getLevelProgress().currentGates().size());
			Vector3 targetPosition = gate.goal.getPosition();
			Vector3 vectorToTarget = new Vector3();
			Vector3 cross = new Vector3();
			Vector3 cameraDirection = gameController.getCamera().direction.cpy();
			Vector3 up = gameController.getCamera().up.cpy();
			Vector3 down = up.cpy().scl(-1);

			// The arrow should be in the middle of the screen, a little before
			// the
			// camera, that it is always visible and below the vertical
			// midpoint.
			Vector3 gatePositionRelativeToCamera = cameraDirection.scl(3)
					.add(gameController.getCamera().position)
					.add(down.scl(1.4f));

			vectorToTarget.set(targetPosition.cpy()
					.sub(gatePositionRelativeToCamera).scl(-1).nor());

			// calculate orthogonal up vector
			up.crs(vectorToTarget).crs(vectorToTarget).nor();

			cross.set(vectorToTarget.cpy().crs(up).nor());

			// create local coordinate system for the arrow. All axes have to be
			// normalized, otherwise, the arrow is scaled.
			float[] values = { up.x, up.y, up.z, 0f, cross.x, cross.y, cross.z, 0f, vectorToTarget.x, vectorToTarget.y, vectorToTarget.z, 0f, 0f, 0f, 0f, 1f };

			arrowModel.transform.set(values).trn(gatePositionRelativeToCamera);
			// batch.begin(gameController.getCamera());
			batch.render(arrowModel, environment);
			// batch.end();
		}
	}
}
