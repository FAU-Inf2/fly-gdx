package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Assets;

public class AsteroidBelt implements IFeatureLoad, IFeatureInit, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	
	private int asteroidCount = 0;
	private String modelRef;
	private List<GameObject> asteroids;

	public AsteroidBelt(int asteroidCount, String modelRef) {
		this.asteroidCount = asteroidCount;
		this.modelRef = modelRef;
	}
	
	@Override
	public void load(GameController game) {
		this.gameController = game;
		batch = gameController.getBatch();
		environment = gameController.getLevel().getEnvironment();
		camera = gameController.getCamera();
		
		asteroids = new ArrayList<GameObject>(asteroidCount);
		
		Assets.loadAsteroid();
		
		GameModel model = Assets.manager.get(modelRef, GameModel.class);
		
		for(GameObject asteroid : asteroids) {
			asteroid = new GameObject(model);
			
			if (asteroid.getCollisionObject() == null) {
				btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(modelRef, asteroid);
				asteroid.filterGroup = CollisionDetector.OBJECT_FLAG;
				asteroid.filterMask = CollisionDetector.ALL_FLAG;
				asteroid.setCollisionObject(shape);
			}
			gameController.getCollisionDetector().addCollisionObject(asteroid);
			
			//asteroid.transform.
		}

		Gdx.app.log("AsteroidBelt.load", "Asteroid belt created: " + modelRef);
	}
	
	@Override
	public void init(GameController game) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(float delta) {
		for(GameObject asteroid : asteroids) {
			//asteroid.transform.
			
			asteroid.render(batch, environment, camera);
		}
	}

	@Override
	public void dispose() {
		for(GameObject asteroid : asteroids) {
			asteroid.dispose();
		}
	}
}