package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display the steering.
 * 
 * @author Tobias Zangl
 */
public class SteeringOverlay implements IFeatureInit, IFeatureDraw, IFeatureDispose {

	private final FlightController flightController;

	private final OrthographicCamera camera;
	private final ShapeRenderer shapeRenderer;
	private final Color COLOR_OF_STEERING_CIRCLE;

	private float steeringX, steeringY;

	public SteeringOverlay(final FlightController cameraController, final Skin skin) {
		this.flightController = cameraController;
		this.shapeRenderer = new ShapeRenderer();
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		COLOR_OF_STEERING_CIRCLE = skin.getColor("lightGrey");
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
	public void draw(float delta) {
		steeringX = -20 * flightController.getAzimuthFactor();
		steeringY = 20 * flightController.getRollFactor();

		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(COLOR_OF_STEERING_CIRCLE);
		shapeRenderer.circle(Gdx.graphics.getWidth() *0.01f * steeringX, Gdx.graphics.getHeight() * 0.01f * steeringY, Gdx.graphics.getWidth() * 0.01f, 20);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(COLOR_OF_STEERING_CIRCLE);
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
		shapeRenderer.dispose();
	}
}