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
import de.fau.cs.mad.fly.game.MovingGameObject;
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
	private List<MovingGameObject> asteroids;

	/**
	 * Constructor for the AstroidBelt
	 * @param asteroidCount		The count of asteroids to display and update.
	 * @param modelRef			The model used for the asteroids.
	 * @param levelSize			The size of the box where the asteroids have to be created inside.
	 */
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
		
		asteroids = new ArrayList<MovingGameObject>(asteroidCount);

		GameModel model = gameController.getLevel().getDependency(modelRef);
		if(model == null) {
			Gdx.app.log("AsteroidBelt.load", "Asteroid model " + modelRef + " not found in the level!");
			return;
		}
		
		Matrix4 identityMatrix = new Matrix4();
		
		for(int i = 0; i < asteroidCount; i++) {

			asteroids.add(createAsteroid(model));
		}

		Gdx.app.log("AsteroidBelt.load", "Asteroid belt created.");
	}
	
	/**
	 * Creates an asteroid with the given model.
	 * <p>
	 * Calculates moving and rotation randomly between specific values after cheking if the position is not colling with anything else.
	 * Creates the collision object of the asteroid.
	 * @param model			The model used for the asteroid.
	 * @return
	 */
	private MovingGameObject createAsteroid(GameModel model) {
		MovingGameObject asteroid = new MovingGameObject(model);
		
		Vector3 position;
		do {
			position = getRandomVector(levelSize);
		} while(checkForSpawnCollision(position, asteroids, 40.0f));
		
		asteroid.transform.setToTranslation(position);
		asteroid.setRotationVector(getRandomVector(new Vector3(2.0f, 2.0f, 2.0f)).nor());
		asteroid.setRotationSpeed(getRandomFloat(2.0f, 10.0f));
		asteroid.setMovingVector(getRandomVector(new Vector3(2.0f, 2.0f, 2.0f)).nor());
		asteroid.setMovingSpeed(getRandomFloat(2.0f, 10.0f));

		btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(modelRef, asteroid);
		asteroid.filterGroup = CollisionDetector.OBJECT_FLAG;
		asteroid.filterMask = CollisionDetector.ALL_FLAG;
		asteroid.setCollisionObject(shape);
		gameController.getCollisionDetector().addCollisionObject(asteroid);

		return asteroid;
	}
	
	/**
	 * Checks if the calculated position is colliding with an already created asteroid or any other component of the level.
	 * @param position		The currently calculated position.
	 * @param asteroids		The list of the already created asteroids.
	 * @param distance2		The minimum distance between a created asteroid and the other objects.
	 * @return
	 */
	private boolean checkForSpawnCollision(Vector3 position, List<MovingGameObject> asteroids, float distance2) {
		for(MovingGameObject asteroid : asteroids) {
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
	 * Calculates a random vector within a given size.
	 * @param size		The maximum size of the random vector.
	 * @return random vector.
	 */
	private Vector3 getRandomVector(Vector3 size) {
		Vector3 v = new Vector3();
		v.x = (float) (Math.random() * size.x) - size.x / 2.0f;
		v.y = (float) (Math.random() * size.y) - size.y / 2.0f;
		v.z = (float) (Math.random() * size.z) - size.z / 2.0f;
		//System.out.println(v.x + " " + v.y + " " + v.z);
		return v;
	}
	
	/**
	 * Calculates a random floating point number between min and max values.
	 * @param min		The minimum value of the floating point number.
	 * @param max		The maximum value of the floating point number.
	 * @return random floating point number.
	 */
	private float getRandomFloat(float min, float max) {
		float speed = (float) (Math.random() * (max-min)) + min;
		return speed;
	}
	
	/**
	 * Checks if the asteroid is too far away of a given position.
	 * @param asteroid		The asteroid to check.
	 * @param target		The target position to check.
	 * @param distance2		The minimum distance between the astroid and the target.
	 * @return true, if it is too far away, otherwise false.
	 */
	private boolean checkOutOfBound(MovingGameObject asteroid, Vector3 target, float distance2) {
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
	public void render(float delta) {
		batch.begin(camera);
		for(MovingGameObject asteroid : asteroids) {
			asteroid.rotate(delta);
			
			if(checkOutOfBound(asteroid, gameController.getCamera().position, 2000.0f)) {
				asteroid.flipDirection();
			}
			
			asteroid.move(delta);
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