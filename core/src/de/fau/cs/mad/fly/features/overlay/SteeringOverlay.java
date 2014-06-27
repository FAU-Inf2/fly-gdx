package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display the steering.
 * 
 * @author Tobias Zangl
 */
public class SteeringOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {

	private final Stage stage;
	private final FlightController flightController;

	private final OrthographicCamera camera;
	private final ShapeRenderer shapeRenderer;

	private float steeringX, steeringY;

	public SteeringOverlay(final FlightController cameraController, final ShapeRenderer shapeRenderer, final Stage stage) {
		this.stage = stage;
		this.flightController = cameraController;

		this.shapeRenderer = shapeRenderer;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 * Setter for the Steering.
	 * 
	 * @param x
	 *            the value in x direction of the steering, should be between
	 *            -30.0, +30.0
	 * @param y
	 *            the value in direction of the steering, should be between
	 *            -30.0, +30.0
	 */
	public void setSteering(float x, float y) {
		steeringX = x;
		steeringY = y;
	}

	@Override
	public void render(float delta) {
		steeringX = -20 * flightController.getAzimuthDir();
		steeringY = 20 * flightController.getRollDir();

		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(Gdx.graphics.getWidth() *0.01f * steeringX, Gdx.graphics.getHeight() * 0.01f * steeringY, Gdx.graphics.getWidth() * 0.01f, 20);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(0, 0, Gdx.graphics.getWidth() * 0.05f, 40);
		shapeRenderer.end();
	}

	@Override
	public void init(final GameController gameController) {
		steeringX = 0.0f;
		steeringY = 0.0f;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}