package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.Spaceship;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;

import java.util.*;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level implements Disposable, IFeatureLoad, ICollisionListener<Spaceship, Gate> {
    
    public static class Head {
        public String name;
        public int id;
        public FileHandle file;
    }
    
    @Override
    public void load(GameController game) {
        activeGatePassed(virtualGate);
    }
    
    /**
     * Radius of the Level which defines the outer boundary which should be
     * never reached by the user. The default level border defines a sphere with
     * radius 100.
     */
    public final float radius = 100.0f;
    public final Head head;
    
    public String levelClass = "DefaultLevel";
    
    public List<GameObject> components;
    public final Perspective start;
    /** gate that has been passed recently, at the beginning the dummy gate */
    private Gate virtualGate;
    private Gate startingGate;
    private final Environment environment;
    private List<EventListener> eventListeners = new ArrayList<EventListener>();
    private final Map<String, GameModel> dependencies;
    
    /** Maps the id to the corresponding gate */
    private Map<Integer, Gate> gates = new HashMap<Integer, Gate>();
    private List<Gate> allGates = new ArrayList<Gate>();
    // public final Collection<String> scripts;
    
    private GameObject borderObject = null;
    private int CollisionTime = 0;
    private int leftCollisionTime = 0;
    
    // private float initTime = 0;
    private float leftTime = 0;
    
    public float getLeftTime() {
        return leftTime;
    }
    
    public void setLeftTime(float leftTime) {
        this.leftTime = leftTime;
    }
    
    public int getGatesNumber() {
        return allGates().size();
    }
    
    public int getLeftCollisionTime() {
        return leftCollisionTime;
    }
    
    public Gate getGateById(int id) {
        return gates.get(id);
    }
    
    public void addGate(Gate gate) {
        gates.put(gate.id, gate);
        allGates.add(gate);
    }
    
    /**
     * Fills the Map that maps id to the corresponding gate and the list of all
     * gates.
     * 
     * @param gates
     */
    public void setGates(Map<Integer, Gate> gates) {
        this.gates = gates;
        this.allGates.addAll(gates.values());
    }
    
    public void setLeftCollisionTime(int leftCollisionTime) {
        this.leftCollisionTime = leftCollisionTime;
    }
    
    protected void InitCollisionTime() {
        CollisionTime = 3;
        leftCollisionTime = CollisionTime;
    }
    
    private boolean reachedLastGate = false;
    
    public boolean isReachedLastGate() {
        return reachedLastGate;
    }
    
    protected void setReachedLastGate(boolean reachedLastGate) {
        this.reachedLastGate = reachedLastGate;
    }
    
    private boolean gameOver = false;
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public Score getScore() {
        if (gameOver) {
            Score newScore = new Score();
            newScore.setReachedDate(new Date());
            
            int score = 0;
            int totalScore = 0;
            for (Gate gate : allGates()) {
                totalScore += gate.score * gate.passedTimes;
            }
            score = totalScore;
            newScore.getScoreDetails().add(new ScoreDetail(("gates"), score + ""));
            
            totalScore += ((int) leftTime) * 20;
            
            newScore.getScoreDetails().add(new ScoreDetail(("leftTime"), (totalScore - score) + ""));
            score = totalScore;
            
            totalScore += leftCollisionTime * 30;
            newScore.setTotalScore(totalScore);
            newScore.getScoreDetails().add(new ScoreDetail(("leftCollisionTime"), (totalScore - score) + ""));
            return newScore;
        } else {
            return new Score();// todo
        }
    }
    
    public Level(String name, Perspective start, List<GameObject> components, Map<String, GameModel> dependencies, Gate startingGate) {
        this.head = new Head();
        this.head.name = name;
        this.virtualGate = startingGate;
        this.startingGate = startingGate;
        this.components = components;
        this.start = start;
        this.environment = new Environment();
        this.dependencies = Collections.unmodifiableMap(dependencies);
        // this.scripts = Collections.unmodifiableCollection(scripts);
        setUpEnvironment();
        
        InitCollisionTime();
        gameOver = false;
        
        for (GameObject c : components) {
            if (c.id.equals("space")) {
                borderObject = c;
            }
        }
        
        if (borderObject == null) {
            Gdx.app.log("Level.Level", "No border specified.");
        }
    }
    
    public void gatePassed(Gate gate) {
        int numberOfSuccessorGates = virtualGate.successors.length;
        for (int i = 0; i < numberOfSuccessorGates; i++) {
            if (gate.id == virtualGate.successors[i]) {
                gate.passedTimes++;
                activeGatePassed(gate);
                i = numberOfSuccessorGates;
            }
        }
    }
    
    public GameModel getDependency(String id) {
        return dependencies.get(id);
    }
    
    public Map<String, GameModel> getDependencies() {
        return dependencies;
    }
    
    public void activeGatePassed(Gate gate) {
        for (EventListener s : eventListeners)
            s.onGatePassed(gate);
        virtualGate = gate;
        if (gate.successors.length == 0) {
            reachedLastGate = true;
            levelFinished();
        }
    }
    
    private void levelFinished() {
        gameOver = true;
        for (EventListener s : eventListeners) {
            s.onFinished();
        }
    }
    
    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }
    
    public void setStartGate(Gate startingGate) {
        this.startingGate = startingGate;
    }
    
    public int[] currentGates() {
        return virtualGate.successors;
    }
    
    public List<Gate> allGates() {
        return allGates;
    }
    
    /**
     * Creates the rigid bodies with convenient parameters for the gate models
     * and the gate goals in the level.
     */
    public void createGateRigidBodies() {
        CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
        Gdx.app.log("Level.createGateRigidBodies", "Setting up collision for level gates.");
        
        for (Gate g : allGates()) {
            if (g.display.getRigidBody() == null) {
                btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(g.display.modelId, g.display);
                g.display.createRigidBody(g.display.modelId, displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
            }
            collisionDetector.addRigidBody(g.display);
            
            if (g.goal.getRigidBody() == null) {
                btCollisionShape goalShape = collisionDetector.getShapeManager().createBoxShape(g.goal.modelId + ".goal", new Vector3(1.0f, 0.05f, 1.0f));
                // g.goal.hide();
                g.goal.userData = g;
                g.goal.createRigidBody(g.goal.modelId + ".goal", goalShape, 0.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
                g.goal.getRigidBody().setCollisionFlags(g.goal.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
            }
            collisionDetector.addRigidBody(g.goal);
        }
    }
    
    /**
     * Environment in the level.
     * <p>
     * Includes ambient and directional lights.
     * 
     * @return environment
     */
    public Environment getEnvironment() {
        return environment;
    }
    
    /**
     * Update the level. Checks whether the level is finished or not.
     * 
     * @param delta
     *            time after the last call.
     * @param camera
     *            that displays the level.
     */
    public void update(float delta, PerspectiveCamera camera) {
        borderObject.transform.setToTranslation(camera.position);
        if (gameOver == false && ((int) leftTime <= 0 || leftCollisionTime <= 0)) {
            levelFinished();
        }
    }
    
    /**
     * Render the level.
     * 
     * @param delta
     *            time after the last call.
     * @param batch
     *            the batch to render the level.
     * @param camera
     *            that displays the level.
     */
    public void render(float delta, ModelBatch batch, PerspectiveCamera camera) {
        int i;
        final int numberOfComponents = components.size();
        // Gdx.app.log("components:", String.valueOf(numberOfComponents));
        for (i = 0; i < numberOfComponents; i++) {
            components.get(i).render(batch, environment, camera);
        }
    }
    
    /**
     * Sets up the environment for the level with its light.
     */
    private void setUpEnvironment() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }
    
    public void reset() {
        eventListeners.clear();
        virtualGate = startingGate;
    }
    
    @Override
    public String toString() {
        return "#<Level name=" + head.name + " virtualGate=" + virtualGate + ">";
    }
    
    @Override
    public void dispose() {
        Gdx.app.log("Level.dispose", "Disposing...");
        for (GameObject o : components)
            o.dispose();
        // for ( GameModel m : dependencies )
        // m.dispose();
    }
    
    @Override
    public void onCollision(Spaceship spaceship, Gate gate) {
        gatePassed(gate);
    }
    
}