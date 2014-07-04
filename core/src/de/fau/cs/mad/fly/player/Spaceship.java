package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.settings.SettingManager;

public class Spaceship implements IPlane, IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	private GameObject instance;
	private Model model;
	
	private boolean firstPerson;

	private String modelRef;
	private float scaleFactor = 0.003f;
	
	private Vector3 movingDir = new Vector3(0,0,1);
	private Vector3 up =  new Vector3(0,1,0);
	
	private float lastRoll = 0.f;
	private float lastAzimuth = 0.f;
	
	public Spaceship(String modelRef) {
		this.modelRef = modelRef;
	}
	
	@Override
	public void load(final GameController game) {
		this.gameController = game;
		this.batch = gameController.getBatch();
		this.environment = gameController.getLevel().getEnvironment();
		this.camera = gameController.getCamera();
		
		Assets.loadSpaceship();
		
		GameModel model = Assets.manager.get(modelRef, GameModel.class);
		
		instance = new GameObject(model);

		instance.transform.setToTranslation(game.getLevel().start.position);
		instance.transform.scl(scaleFactor);
		
		btCollisionShape shape = gameController.getCollisionDetector().getShapeManager().createConvexShape(modelRef, instance);
		btRigidBodyConstructionInfo info = gameController.getCollisionDetector().getRigidBodyInfoManager().createRigidBodyInfo(modelRef, shape, 0.1f);
		instance.filterGroup = CollisionDetector.PLAYER_FLAG;
		instance.filterMask = CollisionDetector.ALL_FLAG;
		instance.id = "spaceship";

		instance.scaleBoundingBox();
		instance.setRigidBody(shape, info);

		instance.setMovement(new Vector3(0.0f, 0.0f, 1.0f));
		instance.userData = this;
		gameController.getCollisionDetector().addRigidBody(instance);
	}
	
	@Override
	public void update(float delta) {
		instance.transform.translate(movingDir.cpy().scl(2.f / scaleFactor * delta));
		instance.getRigidBody().setWorldTransform(instance.transform);
		
		//instance.updateRigidBody();

		//Vector3 trans = new Vector3();
		//instance.transform.getTranslation(trans);
		//System.out.println(instance.transform);
	}

	@Override
	public void render(float delta) {
		float rollDir = lastRoll * 10.f;
		float azimuthDir = lastAzimuth * 50f;
		
		instance.transform.rotate(movingDir.cpy().crs(up), rollDir);
		instance.transform.rotate(movingDir, -azimuthDir);
		
		//instance.transform.scl(scaleFactor);

		instance.render(batch, environment, camera);
		
		instance.transform.rotate(movingDir, azimuthDir);
		instance.transform.rotate(movingDir.cpy().crs(up), -rollDir);
	}

	@Override
	public GameObject getInstance() {
		return instance;
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public float getSpeed() {
		return 2;
	}

	@Override
	public float getAzimuthSpeed() {
		return 9.0f;
	}

	@Override
	public float getRollingSpeed() {
		return 9.0f;
	}

	@Override
	public void dispose() {
		instance.dispose();
	}

	@Override
	public void init(GameController game) {
		camera = gameController.getCamera();
		
		instance.transform.rotate(new Vector3(0,0,1), camera.direction);
	}

	@Override
	public void rotate(float rollDir, float azimuthDir) {
		instance.transform.rotate(movingDir.cpy().crs(up), rollDir);
		instance.transform.rotate(up, azimuthDir);
		
		//instance.getRigidBody().set

		lastRoll = rollDir;
		lastAzimuth = azimuthDir;
	}

	@Override
	public Vector3 getPosition() {
		return instance.getPosition();
	}
}
