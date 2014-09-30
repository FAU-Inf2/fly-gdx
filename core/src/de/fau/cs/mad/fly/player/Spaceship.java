package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.Gdx;
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
import de.fau.cs.mad.fly.player.gravity.IGravity;
import de.fau.cs.mad.fly.player.particle.EmptyParticle;
import de.fau.cs.mad.fly.player.particle.IParticle;
import de.fau.cs.mad.fly.player.particle.ShuttleParticle;
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
    
    private Vector3 particleOffset = null;
    private Matrix4 particleTransform;
    
    private IParticle particle;
    
    private float i = 0.0f;
    private float rotationSpeed = 0.0f;
    private Vector3 rotation = null;
    
    private IGravity gravity;
    
    private boolean useRolling;
    
    private String modelRef;
    
    private Vector3 movingDir = new Vector3(0, 0, 1);
    private final Vector3 up = new Vector3(0, 1, 0);
    Vector3 linearMovement;
    
    private float lastRoll = 0.f;
    private float lastAzimuth = 0.f;
    
    private Matrix4 storedTransform;
    private Matrix4 displayTransform = new Matrix4();
    
    private float speed;
    private float azimuthSpeed;
    private float rollingSpeed;
    private IPlane.Head head;
    
    public Spaceship(GameModel model, IPlane.Head head) {
        super(model, "Spaceship");
        this.head = head;
        
        // TODO: adjust the speed, currently just divided by 5 because it was
        // too fast
        this.speed = head.speed / 5;
        this.azimuthSpeed = head.azimuthSpeed;
        this.rollingSpeed = head.rollingSpeed;
        
        this.modelRef = head.modelRef;
        
        if (head.rotation != null) {
            rotationSpeed = head.rotationSpeed;
            rotation = head.rotation;
        }
        
        if (head.particleOffset != null) {
            particleOffset = head.particleOffset;
            particle = new ShuttleParticle();
        } else {
            particleOffset = new Vector3();
            particle = new EmptyParticle();
        }
    }
    
    @Override
    public void load(final GameController game) {
        this.gameController = game;
        this.batch = gameController.getBatch();
        this.environment = gameController.getLevel().getEnvironment();
        this.camera = gameController.getCamera();
        linearMovement = new Vector3();
        
        gravity = game.getLevel().getGravity();
        
        particle.load(modelRef);
        Gdx.app.log("Spaceship.load", "Initializing particle...");
        particle.init();
        Gdx.app.log("Spaceship.load", "Initializing particle: Done!");
        // resetSpeed();
        
        transform.setToTranslation(game.getLevel().start.position);
        
        Gdx.app.log("Spaceship.load", "Creating collision shape...");
        btCollisionShape shape = CollisionDetector.getInstance().getShapeManager().createConvexShape(modelRef, this);
        
        Gdx.app.log("Spaceship.load", "Scaling bounding box...");
        scaleFrustumBoundingBox();
        createRigidBody(modelRef, shape, 1.0f, CollisionDetector.PLAYER_FLAG, CollisionDetector.ALL_FLAG);
        Gdx.app.log("Spaceship.load", "Adding motion state...");
        addMotionState();
        getRigidBody().setDamping(0.0f, 0.5f);
        Gdx.app.log("Spaceship.load", "Adjusting scaling...");
        getRigidBody().getCollisionShape().setLocalScaling(new Vector3(0.7f, 0.7f, 0.7f));
        Gdx.app.log("Spaceship.load", "Adding rigid body to collision detector...");
        
        CollisionDetector.getInstance().addRigidBody(this);
        Gdx.app.log("Spaceship.load", "EXIT");
    }
    
    @Override
    public void render(float delta) {
        float rollDir = lastRoll * 10.f;// / delta / 60.f;
        float azimuthDir = lastAzimuth * 50f;// / delta / 60.f;
        
        storedTransform = transform;
        displayTransform.set(transform);
        transform = displayTransform;
        
        transform.rotate(movingDir.cpy().crs(up), rollDir);
        transform.rotate(movingDir, -azimuthDir);
        
        if (rotation != null) {
            transform.rotate(rotation, i * rotationSpeed);
        }
        
        render(batch, environment, camera);
        
        particleTransform = transform.cpy();
        particleTransform.translate(particleOffset);
        particle.render(particleTransform);
        
        transform = storedTransform;
        
        i += delta;
    }
    
    /**
     * Setter if rolling should be used.
     * 
     * @param rolling
     *            True if rolling should be used, false otherwise.
     */
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
        if (!useRolling) {
            rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(up, azimuthDir * deltaFactor);
        } else {
            rotationTransform.rotate(movingDir.cpy().crs(up), rollDir * deltaFactor).rotate(movingDir, -azimuthDir * deltaFactor);
        }
        getRigidBody().setCenterOfMassTransform(rotationTransform);
        
        float[] transformValues = rotationTransform.getValues();
        linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(getSpeed());
        
        gravity.applyGravity(transform, linearMovement);
        
        setMovement(linearMovement);
        
        lastRoll = rollDir;
        lastAzimuth = azimuthDir;
    }
    
    public void shift(Vector3 vector) {
    	rotationTransform = getRigidBody().getCenterOfMassTransform();
    	
    	rotationTransform.trn(vector);
    	
    	getRigidBody().setCenterOfMassTransform(rotationTransform);
    	
    	float[] transformValues = rotationTransform.getValues();
        linearMovement.set(transformValues[8], transformValues[9], transformValues[10]).scl(getSpeed());
        setMovement(linearMovement);
    	
    	lastRoll = Math.signum(vector.z);
    	lastAzimuth = -Math.signum(vector.x);
    }
    
    /**
     * Resets the Spaceship on the specified rail
     */
    public void resetOnRail(float railX, float railY, float railPos) {
    	//Vector3 newPosition = new Vector3(railY, railPos, railX);
    	Vector3 newPosition = new Vector3(-railY, railX, railPos);
    	Gdx.app.log("reset", ""+newPosition);
    	
    	Vector3 yPos = new Vector3(0,railPos,0);

        Perspective start = gameController.getLevel().start;
    	
    	rotationTransform.setToLookAt(start.viewDirection.cpy().add(yPos), start.upDirection);
    	rotationTransform.rotate(start.upDirection, 180.0f);
    	rotationTransform.translate(newPosition);
        
    	getRigidBody().setCenterOfMassTransform(rotationTransform);
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
    
    public void dispose() {
        particle.stop();
        particle.dispose();
        super.dispose();
    }
    
    @Override
    public void setGravity(IGravity gravity) {
        this.gravity = gravity;
    }
    
    @Override
    public IGravity getGravity() {
        return gravity;
    }
}