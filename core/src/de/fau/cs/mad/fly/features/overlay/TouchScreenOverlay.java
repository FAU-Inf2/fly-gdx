package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.CameraController;
import de.fau.cs.mad.fly.game.GameController;

public class TouchScreenOverlay implements IFeatureInit, IFeatureRender, IFeatureDispose {
	
	private CameraController cameraController;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	public TouchScreenOverlay(final CameraController cameraController, final ShapeRenderer shapeRenderer, Stage stage) {
		
		this.cameraController = cameraController;

		this.shapeRenderer = shapeRenderer;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 1.0f);
		shapeRenderer.circle(-Gdx.graphics.getWidth()/3f, -Gdx.graphics.getHeight()/3f, Gdx.graphics.getWidth() * 0.075f, 40);
		shapeRenderer.end();
		
	}

	@Override
	public void init(GameController game) {
		// TODO Auto-generated method stub
		
	}

}
