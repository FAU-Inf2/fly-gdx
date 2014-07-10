package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.features.ICollectListener;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.features.IFeatureUpdate;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.profile.PlayerManager;

/**
 * Used do display and handle any sort of collectible objects in the game.
 * 
 * @author Tobi
 */
public class CollectibleObjects implements IFeatureLoad, IFeatureInit, IFeatureRender, IFeatureDispose, ICollisionListener<Spaceship, GameObject> {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	
	private String collectibleType;
	private String modelRef;
	private List<GameObject> collectibleObjects;
	
	private List<ICollectListener> collectListeners;
	
	/**
	 * Creates a new collectible objects game feature.
	 * 
	 * @param collectibleType		The type of the collectible object in the level file.
	 * @param modelRef				The model reference in the level file used to display the collectible objects.
	 */
	public CollectibleObjects(String collectibleType, String modelRef) {
		this.collectibleType = collectibleType;
		this.modelRef = modelRef;
	}
	
	/**
	 * Adds a new collect listener.
	 * @param listener				The collect listener.
	 */
	public void addCollectListener(ICollectListener listener) {
		collectListeners.add(listener);
	}
	
	@Override
	public void load(GameController game) {
		this.gameController = game;
		batch = gameController.getBatch();
		environment = gameController.getLevel().getEnvironment();

		collectibleObjects = new ArrayList<GameObject>();
		collectListeners = new ArrayList<ICollectListener>();		
		
		for(GameObject gameObject : gameController.getLevel().components) {
			if(gameObject.id.equals("speedUpgrade")) {
				btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, gameObject);
				gameObject.createRigidBody(modelRef, shape, 10.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
				gameObject.getRigidBody().setCollisionFlags(gameObject.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
				CollisionDetector.getInstance().addRigidBody(gameObject);

				gameObject.addMotionState();
				gameObject.setDummy(true);
				gameObject.getRigidBody().setSleepingThresholds(0.01f, 0.01f);

				collectibleObjects.add(gameObject);
			}
		}

		Gdx.app.log("CollectibleObjects.load", "Collectible objects created.");
	}
	
	@Override
	public void init(GameController game) {
		camera = gameController.getCamera();
	}
	
	@Override
	public void render(float delta) {
		/*batch.begin(camera);
		for(GameObject gameObject : collectibleObjects) {
			gameObject.updateRigidBody();			
			gameObject.render(batch, environment, camera);
		}
		batch.end();*/
	}

	@Override
	public void dispose() {
		for(GameObject object : collectibleObjects) {
			//object.dispose();
		}
	}

	@Override
	public void onCollision(Spaceship ship, GameObject gameObject) {		
		if(!collectibleObjects.contains(gameObject)) {
			return;
		}
		
		gameObject.hide();
		collectibleObjects.remove(gameObject);
		gameObject.removeRigidBody();
		
		//PlayerManager.getInstance().getCurrentPlayer().getPlane().setSpeed(5.0f);
		
		for(ICollectListener listener : collectListeners) {
			listener.onCollect(collectibleType);
		}
	}
}