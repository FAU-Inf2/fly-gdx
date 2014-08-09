package de.fau.cs.mad.fly.player;


import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Perspective;

public class Spaceship extends GameObject implements IPlane {
	private GameController gameController;
	private ModelBatch batch;
	private Environment environment;
	private PerspectiveCamera camera;
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
	
	private float speed;
	private float azimuthSpeed;
	private float rollingSpeed;
	private IPlane.Head head;
	
	public Spaceship(GameModel model, IPlane.Head head) {
		super(model, "Spaceship");
		this.head = head;
		this.modelRef = head.modelRef;
	}
	
	public Spaceship(GameModel model, String modelRef) {
		super(model, "spaceship");
		this.modelRef = modelRef;
	}
	
	@Override
	public void load(final GameController game) {
		this.gameController = game;
		this.batch = gameController.getBatch();
		this.environment = gameController.getLevel().getEnvironment();
		this.camera = gameController.getCamera();
		linearMovement = new Vector3();
		
		resetSpeed();

		transform.setToTranslation(game.getLevel().start.position);
		
		btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, this);

		scaleBoundingBox();
		createRigidBody(modelRef, shape, 1.0f, CollisionDetector.PLAYER_FLAG, CollisionDetector.ALL_FLAG);
		
		addMotionState();		
		getRigidBody().setDamping(0.0f, 0.5f);
		getRigidBody().getCollisionShape().setLocalScaling(new Vector3(0.7f, 0.7f, 0.7f));

		CollisionDetector.getInstance().addRigidBody(this);
	}

	@Override
	public void render(float delta) {
		float rollDir = lastRoll * 10.f;// / delta / 60.f;
		float azimuthDir = lastAzimuth * 50f;// / delta / 60.f;
		
		transform.rotate(movingDir.cpy().crs(up), rollDir);
		transform.rotate(movingDir, -azimuthDir);

		render(batch, environment, camera);
		
		transform.rotate(movingDir, azimuthDir);
		transform.rotate(movingDir.cpy().crs(up), -rollDir);
	}
	
	public void setRolling(boolean rolling) {
		this.useRolling = rolling;
	}

	@Override
	public Head getHead() {
		return head;
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
	public void init(GameController game) {
		camera = gameController.getCamera();

		Perspective start = game.getLevel().start;
		startTransform = getRigidBody().getCenterOfMassTransform().setToLookAt(start.viewDirection, start.upDirection);
		startTransform.rotate(start.upDirection, 180.0f);
		startTransform.translate(start.position);
		
		getRigidBody().setCenterOfMassTransform(startTransform);
		
		transformValues = startTransform.getValues();
		linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(speed);
		setMovement(linearMovement);
	}

	@Override
	public void rotate(float rollDir, float azimuthDir, float deltaFactor) {		
		rotationTransform = getRigidBody().getCenterOfMassTransform();
		if(!useRolling) {
			rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(up, azimuthDir * deltaFactor);
		} else {
			rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(movingDir, -azimuthDir * deltaFactor);
		}
		getRigidBody().setCenterOfMassTransform(rotationTransform);
		
		float[] transformValues = rotationTransform.getValues();
		linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(getSpeed());
		setMovement(linearMovement);

		lastRoll = rollDir;
		lastAzimuth = azimuthDir;
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}

	@Override
	public void resetSpeed() {
		speed = 2.0f;
		azimuthSpeed = 9.0f;
		rollingSpeed = 9.0f;
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix4 getTransform() {
		return transform;
	}
}