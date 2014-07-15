package de.fau.cs.mad.fly.features.game;

import java.util.Collection;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.res.Level.Gate;

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
	private Vector3 targetPosition;
	private Vector3 vectorToTarget;
	private Vector3 cross;
	private Vector3 cameraDirection;
	private Vector3 up;
	private Vector3 down;
	private Vector3 gatePositionRelativeToCamera;

	private GameObject arrowModel;

	@Override
	public void init(final GameController game) {
		this.gameController = game;
		Assets.load(Assets.arrow);
		arrowModel = new GameObject(Assets.manager.get(Assets.arrow));
		batch = game.getBatch();
		environment = gameController.getLevel().getEnvironment();
		vectorToTarget = new Vector3();
		cross = new Vector3();
	}

	@Override
	public void render(float delta) {
	    final Camera camera = gameController.getCamera();
	    final Collection<Gate> gates = gameController.getLevel().currentGates();
		for (Level.Gate gate : gameController.getLevel().currentGates()) {
			targetPosition = gate.goal.getPosition();
			cameraDirection = camera.direction.cpy();
			up = camera.up.cpy();
			down = up.cpy().scl(-1.4f);

			// The arrow should be in the middle of the screen, a little before
			// the camera, that it is always visible and below the vertical
			// midpoint.
			gatePositionRelativeToCamera = cameraDirection.scl(3).add(camera.position).add(down);

			vectorToTarget.set(targetPosition.cpy().sub(camera.position).scl(-1).nor());

			// calculate orthogonal up vector
			up.crs(vectorToTarget).crs(vectorToTarget).nor();

			cross.set(vectorToTarget.cpy().crs(up).nor());

			// create local coordinate system for the arrow. All axes have to be
			// normalized, otherwise, the arrow is scaled.
			float[] values = { up.x, up.y, up.z, 0f, cross.x, cross.y, cross.z, 0f, vectorToTarget.x, vectorToTarget.y, vectorToTarget.z, 0f, 0f, 0f, 0f, 1f };

			arrowModel.transform.set(values).trn(gatePositionRelativeToCamera);
			batch.render(arrowModel, environment);
		}
	}
}
