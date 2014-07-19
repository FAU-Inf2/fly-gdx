package de.fau.cs.mad.fly.player;


import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
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
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Perspective;

public class Spaceship implements IPlane, IFeatureLoad, IFeatureInit, IFeatureUpdate, IFeatureRender, IFeatureDispose {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
	private GameObject instance;
	private Model model;
	private Matrix4 rotationTransform;
	private float[] transformValues;
	private Matrix4 startTransform;
	
	private boolean useRolling;

	private String modelRef;
	
	private Vector3 movingDir = new Vector3(0,0,1);
	private final Vector3 up =  new Vector3(0,1,0);
	Vector3 linearMovement; 
	
	private float lastRoll = 0.f;
	private float lastAzimuth = 0.f;
	
	private float speed = 2.0f;
	private float azimuthSpeed = 9.0f;
	private float rollingSpeed = 9.0f;
	
	public Spaceship(String modelRef) {
		this.modelRef = modelRef;
	}
	
	@Override
	public void load(final GameController game) {
		this.gameController = game;
		this.batch = gameController.getBatch();
		this.environment = gameController.getLevel().getEnvironment();
		this.camera = gameController.getCamera();
		linearMovement = new Vector3();
		
		Assets.load(Assets.spaceship);
		
		GameModel model = Assets.manager.get(modelRef, GameModel.class);
		
		instance = new GameObject(model, "spaceship");

		instance.transform.setToTranslation(game.getLevel().start.position);
		
		btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, instance);

		instance.scaleBoundingBox();
		instance.createRigidBody(modelRef, shape, 1.0f, CollisionDetector.PLAYER_FLAG, CollisionDetector.ALL_FLAG);
		
		instance.addMotionState();		
		instance.getRigidBody().setDamping(0.0f, 0.5f);

		instance.userData = this;
		CollisionDetector.getInstance().addRigidBody(instance);
	}
	
	@Override
	public void update(float delta) {
		// currently not used, perhaps in the future.
	}

	@Override
	public void render(float delta) {
		float rollDir = lastRoll * 10.f;// / delta / 60.f;
		float azimuthDir = lastAzimuth * 50f;// / delta / 60.f;
		
		instance.transform.rotate(movingDir.cpy().crs(up), rollDir);
		instance.transform.rotate(movingDir, -azimuthDir);

		instance.render(batch, environment, camera);
		
		instance.transform.rotate(movingDir, azimuthDir);
		instance.transform.rotate(movingDir.cpy().crs(up), -rollDir);
	}
	
	public void setRolling(boolean rolling) {
		this.useRolling = rolling;
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
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public float getAzimuthSpeed() {
		return azimuthSpeed;
	}

	@Override
	public float getRollingSpeed() {
		return rollingSpeed;
	}

	@Override
	public void dispose() {
		instance.dispose();
	}

	@Override
	public void init(GameController game) {
		camera = gameController.getCamera();

		Perspective start = game.getLevel().start;
		startTransform = instance.getRigidBody().getCenterOfMassTransform().setToLookAt(start.viewDirection, start.upDirection);
		startTransform.rotate(start.upDirection, 180.0f);
		startTransform.translate(start.position);
		System.out.println(startTransform);
		
		instance.getRigidBody().setCenterOfMassTransform(startTransform);
		
		transformValues = startTransform.getValues();
		linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(speed);
		instance.setMovement(linearMovement);
	}

	@Override
	public void rotate(float rollDir, float azimuthDir, float deltaFactor) {		
		rotationTransform = instance.getRigidBody().getCenterOfMassTransform();
		if(!useRolling) {
			rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(up, azimuthDir * deltaFactor);
		} else {
			rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(movingDir, -azimuthDir * deltaFactor);
		}
		instance.getRigidBody().setCenterOfMassTransform(rotationTransform);
		
		float[] transformValues = rotationTransform.getValues();
		linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(getSpeed());
		instance.setMovement(linearMovement);

		lastRoll = rollDir;
		lastAzimuth = azimuthDir;
	}

	@Override
	public Vector3 getPosition() {
		return instance.getPosition();
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}
}