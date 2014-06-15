package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.game.GameController;

public class TouchScreenOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {
	
	private final FlightController flightController;
	
	private final OrthographicCamera camera;
	private final ShapeRenderer shapeRenderer;
	
	public TouchScreenOverlay(final FlightController cameraController, final ShapeRenderer shapeRenderer, final Stage stage) {
		
		this.flightController = cameraController;

		this.shapeRenderer = shapeRenderer;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(-Gdx.graphics.getWidth()/3f, -Gdx.graphics.getHeight()/3f, Gdx.graphics.getWidth() * 0.075f, 40);
		shapeRenderer.end();
	}

	@Override
	public void init(final GameController game) {
		// TODO Auto-generated method stub
		
	}
}
