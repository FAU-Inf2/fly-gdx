package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.CameraController;
import de.fau.cs.mad.fly.game.GameController;

/**
 * Optional Feature to display the steering.
 * 
 * @author Tobias Zangl
 */
public class SteeringOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {
	private final Fly game;

	private Stage stage;
	private CameraController cameraController;

	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	private SpriteBatch batch;
	
	private float steeringX, steeringY;
	
	public SteeringOverlay(final Fly game, Stage stage) {
		this.game = game;
		this.stage = stage;
		this.cameraController = game.getCameraController();
		
		shapeRenderer = game.getShapeRenderer();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	/**
	 * Setter for the Steering.
	 * @param x
	 *            the value in x direction of the steering, should be between -30.0, +30.0
	 * @param y
	 *            the value in  direction of the steering, should be between -30.0, +30.0
	 */
	public void setSteering(float x, float y) {
		steeringX = x;
		steeringY = y;
	}

	@Override
	public void render(float delta) {		
		steeringX = -20 * cameraController.getAzimuthDir();
		steeringY = 20 * cameraController.getRollDir();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(game.getAbsoluteX(0.01f) * steeringX, game.getAbsoluteY(0.01f) * steeringY, game.getAbsoluteX(0.01f), 20);
		shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(0, 0, game.getAbsoluteX(0.05f), 40);
		shapeRenderer.end();
	}

	@Override
	public void init(GameController gameController) {
		steeringX = 0.0f;
		steeringY = 0.0f;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}