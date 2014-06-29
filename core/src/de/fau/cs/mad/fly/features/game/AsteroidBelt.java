package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

/**
 * Creates an asteroid belt inside a given level size with a given asteroid model and a given count of asteroids.
 * 
 * @author Tobias Zangl
 */
public class AsteroidBelt implements IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	
	private Vector3 spawnSize;
	
	private int asteroidCount = 0;
	private String modelRef;
	private List<GameObject> asteroids;

	/**
	 * Constructor for the AstroidBelt
	 * @param asteroidCount		The count of asteroids to display and update.
	 * @param modelRef			The model used for the asteroids.
	 * @param spawnSize			The size of the box in positive and negative direction where the asteroids have to be created inside.
	 */
	public AsteroidBelt(int asteroidCount, String modelRef, Vector3 spawnSize) {
		this.asteroidCount = asteroidCount;
		this.modelRef = modelRef;
		this.spawnSize = spawnSize;
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
		
		for(int i = 0; i < asteroidCount; i++) {
			asteroids.add(createAsteroid(model, i));
		}

		Gdx.app.log("AsteroidBelt.load", "Asteroid belt created.");
	}
	
	/**
	 * Creates an asteroid with the given model.
	 * <p>
	 * Calculates moving and rotation randomly between specific values after cheking if the position is not colling with anything else.
	 * Creates the collision object of the asteroid.
	 * @param model			The model used for the asteroid.
	 * @return GameObject
	 */
	private GameObject createAsteroid(GameModel model, int id) {
		GameObject asteroid = new GameObject(model);
		
		Vector3 position;
		do {
			position = getRandomVectorInSize(spawnSize);
		} while(checkForSpawnCollision(position, asteroids, 20.0f));
		
		asteroid.transform.setToTranslation(position);

		btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(modelRef, asteroid);
		btRigidBodyConstructionInfo info = gameController.getCollisionDetector().getRigidBodyInfoManager().createRigidBodyInfo(modelRef, shape, 1.0f);
		asteroid.filterGroup = CollisionDetector.OBJECT_FLAG;
		asteroid.filterMask = CollisionDetector.ALL_FLAG;
		asteroid.setRigidBody(shape, info);
		asteroid.setRestitution(1.0f);
		asteroid.id = "Asteroid_" + id;
		gameController.getCollisionDetector().addRigidBody(asteroid);
		
		asteroid.setMovement(getRandomVector(-2.0f, 2.0f));
		asteroid.setRotation(getRandomVector(-0.5f, 0.5f));

		return asteroid;
	}
	
	/**
	 * Checks if the calculated position is colliding with an already created asteroid or any other component of the level.
	 * @param position		The currently calculated position.
	 * @param asteroids		The list of the already created asteroids.
	 * @param distance2		The minimum distance between a created asteroid and the other objects.
	 * @return true if the position is too close to anything else, false otherwise.
	 */
	private boolean checkForSpawnCollision(Vector3 position, List<GameObject> asteroids, float distance2) {
		for(GameObject asteroid : asteroids) {
			if(position.dst2(asteroid.getPosition()) < distance2) {
				Gdx.app.log("AsteroidBelt.checkForSpawnCollision", "SpawnCollision with other asteroid");
				return true;
			}
		}
		
		for(GameObject component : gameController.getLevel().components) {
			if(position.dst2(component.getPosition()) < distance2) {
				Gdx.app.log("AsteroidBelt.checkForSpawnCollision", "SpawnCollision with level component");
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates a random vector with x, y and z value between 0.0f and max value.
	 * @param max		The maximum size of the random vector.
	 * @return random vector.
	 */
	private Vector3 getRandomVector(float max) {
		return getRandomVector(new Vector3(max, max, max));
	}
	
	/**
	 * Calculates a random vector with x, y and z value between min and max value.
	 * @param min		The minimum size of the random vector.
	 * @param max		The maximum size of the random vector.
	 * @return random vector.
	 */
	private Vector3 getRandomVector(float min, float max) {
		return getRandomVector(new Vector3(min, min, min), new Vector3(max, max, max));
	}
	
	/**
	 * Calculates a random vector positive or negative within a given absolute size.
	 * @param size		The maximum absolute size of the random vector.
	 * @return random vector.
	 */
	private Vector3 getRandomVectorInSize(final Vector3 size) {
		Vector3 v = new Vector3();
		v.x = MathUtils.random(- size.x, size.x);
		v.y = MathUtils.random(- size.y, size.y);
		v.z = MathUtils.random(- size.z, size.z);
		return v;
	}
	
	/**
	 * Calculates a random vector between 0.0f and maximum vector.
	 * @param max		The maximum size of the random vector.
	 * @return random vector.
	 */
	private Vector3 getRandomVector(final Vector3 max) {
		Vector3 v = new Vector3();
		v.x = MathUtils.random(0.0f, max.x);
		v.y = MathUtils.random(0.0f, max.y);
		v.z = MathUtils.random(0.0f, max.z);
		return v;
	}

	/**
	 * Calculates a random vector between a min and a max vector.
	 * @param min		The minimum vector size.
	 * @param max		The maximum vector size.
	 * @return random vector.
	 */
	private Vector3 getRandomVector(final Vector3 min, final Vector3 max) {
		Vector3 v = new Vector3();
		v.x = MathUtils.random(min.x, min.x);
		v.y = MathUtils.random(min.y, min.y);
		v.z = MathUtils.random(min.z, min.z);
		return v;
	}
	
	/**
	 * Checks if the asteroid is too far away of a given position.
	 * @param asteroid		The asteroid to check.
	 * @param target		The target position to check.
	 * @param distance2		The minimum distance between the astroid and the target.
	 * @return true, if it is too far away, otherwise false.
	 */
	private boolean checkOutOfBound(GameObject asteroid, Vector3 target, float distance2) {
		if(asteroid.getPosition().dst2(target) > distance2) {
			return true;
		}
		return false;
	}
	
	@Override
	public void init(GameController game) {
		camera = gameController.getCamera();
	}
	
	@Override
	public void update(float delta) {		
		for(GameObject asteroid : asteroids) {
			if(checkOutOfBound(asteroid, gameController.getCamera().position, 2000.0f)) {
				asteroid.flipDirection();
			}
		}
	}
	
	@Override
	public void render(float delta) {		
		batch.begin(camera);
		for(GameObject asteroid : asteroids) {
			asteroid.updateRigidBody();
			asteroid.render(batch, environment, camera);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		for(GameObject asteroid : asteroids) {
			asteroid.dispose();
		}
	}
}