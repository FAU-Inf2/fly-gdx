package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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

/**
 * Creates an asteroid belt inside a given level size with a given asteroid model and a given count of asteroids.
 * 
 * @author Tobias Zangl
 */
public class AsteroidBelt implements IFeatureLoad, IFeatureInit, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	
	private Vector3 levelSize;
	
	private int asteroidCount = 0;
	private String modelRef;
	private List<GameObject> asteroids;

	public AsteroidBelt(int asteroidCount, String modelRef, Vector3 levelSize) {
		this.asteroidCount = asteroidCount;
		this.modelRef = modelRef;
		this.levelSize = levelSize;
	}
	
	@Override
	public void load(GameController game) {
		this.gameController = game;
		batch = gameController.getBatch();
		environment = gameController.getLevel().getEnvironment();
		
		asteroids = new ArrayList<GameObject>(asteroidCount);

		GameModel model = gameController.getLevel().getDependency(modelRef);
		if(model == null) {
			Gdx.app.log("AsteroidBelt.load", "Asteroid model " + modelRef + " not found in the level!");
			return;
		}
		
		Matrix4 identityMatrix = new Matrix4();
		
		for(int i = 0; i < asteroidCount; i++) {
			GameObject asteroid = new GameObject(model);
			
			asteroid.transform.setToTranslation(getRandomVector(levelSize));
			//asteroid.movingDir = getRandomVector(new Vector3(2.0f, 2.0f, 2.0f)).nor();
			//asteroid.movingSpeed = 1.0f;

			btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(modelRef, asteroid);
			asteroid.filterGroup = CollisionDetector.OBJECT_FLAG;
			asteroid.filterMask = CollisionDetector.ALL_FLAG;
			asteroid.setCollisionObject(shape);
			gameController.getCollisionDetector().addCollisionObject(asteroid);

			asteroids.add(asteroid);
		}

		Gdx.app.log("AsteroidBelt.load", "Asteroid belt created.");
	}
	
	private Vector3 getRandomVector(Vector3 size) {
		Vector3 v = new Vector3();
		v.x = (float) (Math.random() * size.x) - size.x / 2.0f;
		v.y = (float) (Math.random() * size.y) - size.y / 2.0f;
		v.z = (float) (Math.random() * size.z) - size.z / 2.0f;
		System.out.println(v.x + " " + v.y + " " + v.z);
		return v;
	}
	
	@Override
	public void init(GameController game) {
		camera = gameController.getCamera();
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