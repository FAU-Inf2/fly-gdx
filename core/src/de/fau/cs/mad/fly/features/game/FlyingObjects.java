package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.helper.RandomHelper;

/**
 * Used to display flying objects inside a given size around the player with a given model and a given count of flying objects.
 * 
 * @author Tobias Zangl
 */
public class FlyingObjects implements IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	
	private Vector3 spawnSize;
	
	private String id;
	private int count = 0;
	private String modelRef;
	private List<GameObject> objects;

	/**
	 * Constructor for the AstroidBelt
	 * @param count				The count of flying objects to display.
	 * @param modelRef			The model used for the flying objects.
	 * @param spawnSize			The size of the box in positive and negative direction where the flying objects have to be created inside.
	 */
	public FlyingObjects(String id, int count, String modelRef, Vector3 spawnSize) {
		this.id = id;
		this.count = count;
		this.modelRef = modelRef;
		this.spawnSize = spawnSize;
	}
	
	@Override
	public void load(GameController game) {
		this.gameController = game;
		batch = gameController.getBatch();
		environment = gameController.getLevel().getEnvironment();
		
		objects = new ArrayList<GameObject>(count);

		GameModel model = gameController.getLevel().getDependency(modelRef);
		if(model == null) {
			Gdx.app.log("FlyingObjects.load", "Flying objects model " + modelRef + " not found in the level!");
			count = 0;
			return;
		}
		
		for(int i = 0; i < count; i++) {
			objects.add(createFlyingObject(model, i));
		}

		Gdx.app.log("FlyingObjectst.load", "Flying objects created.");
	}
	
	/**
	 * Creates a flying object with the given model.
	 * <p>
	 * Calculates moving and rotation randomly between specific values after checking if the position is not colliding with anything else.
	 * Creates the collision object of the flying object.
	 * @param model			The model used for the flying object.
	 * @return GameObject
	 */
	private GameObject createFlyingObject(GameModel model, int id) {
	    long time = System.currentTimeMillis();
		GameObject flyingObject = new GameObject(model, this.id + "_" + id);
		Gdx.app.log("loadGameObject", "createFlyingObject: " + String.valueOf(System.currentTimeMillis() - time));
		
		Vector3 position;
		do {
			position = RandomHelper.getRandomVectorInSize(spawnSize);
		} while(checkForSpawnCollision(position, objects, 20.0f));
		
		flyingObject.transform.setToTranslation(position);

		btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, flyingObject);
		flyingObject.createRigidBody(modelRef, shape, 1.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
		flyingObject.setRestitution(1.0f);
		CollisionDetector.getInstance().addRigidBody(flyingObject);
		
		flyingObject.setMovement(RandomHelper.getRandomVector(-2.0f, 2.0f));
		flyingObject.setRotation(RandomHelper.getRandomVector(-0.5f, 0.5f));

		return flyingObject;
	}
	
	/**
	 * Checks if the calculated position is colliding with an already created flying object or any other component of the level.
	 * @param position		The currently calculated position.
	 * @param objects		The list of the already created flying objects
	 * @param distance2		The minimum distance between a created flying object and the other objects.
	 * @return true if the position is too close to anything else, false otherwise.
	 */
	private boolean checkForSpawnCollision(Vector3 position, List<GameObject> objects, float distance2) {
		int size = objects.size();
		for(int i = 0; i < size; i++) {
			if(position.dst2(objects.get(i).getPosition()) < distance2) {
				//Gdx.app.log("FlyingObjects.checkForSpawnCollision", "SpawnCollision with other flying object.");
				return true;
			}
		}
		
		for(GameObject component : gameController.getLevel().components) {
			if(position.dst2(component.getPosition()) < distance2) {
				//dx.app.log("FlyingObjects.checkForSpawnCollision", "SpawnCollision with level component.");
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if the flying object is too far away of a given position.
	 * @param object		The object to check.
	 * @param target		The target position to check.
	 * @param distance2		The minimum distance between the object and the target.
	 * @return true, if it is too far away, otherwise false.
	 */
	private boolean checkOutOfBound(GameObject object, Vector3 target, float distance2) {
		if(object.getPosition().dst2(target) > distance2) {
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
		for(int i = 0; i < count; i++) {
			if(checkOutOfBound(objects.get(i), gameController.getCamera().position, 2000.0f)) {
				objects.get(i).flipDirection();
			}
		}
	}
	
	@Override
	public void render(float delta) {		
		for(int i = 0; i < count; i++) {
			objects.get(i).updateRigidBody();
			objects.get(i).render(batch, environment, camera);
		}
	}

	@Override
	public void dispose() {
		for(int i = 0; i < count; i++) {
			objects.get(i).dispose();
		}
	}
}