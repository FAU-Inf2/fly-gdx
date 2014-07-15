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
public class Level implements Disposable, IFeatureLoad, ICollisionListener<Spaceship, Level.Gate> {
    public static class Gate implements Iterable<Gate>, Disposable {
        public final int id;
        public int score;
        public GameObject display;
        public GameObject goal;
        public int passedTimes = 0;
        public Collection<Gate> successors;
        
        public Gate(Integer id) {
            this.id = id;
            this.score = 50;
        }
        
        public void mark() {
            if (display != null) {
                display.mark();
            }
        }
        
        public void unmark() {
            if (display != null) {
                display.unmark();
            }
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                return id == ((Gate) o).hashCode();
            }
        }
        
        @Override
        public int hashCode() {
            return id;
        }
        
        @Override
        public String toString() {
            return "#<Gate " + id + ">";
        }
        
        private void buildIterator(Collection<Gate> gs) {
            for (Gate g : successors)
                if (!gs.contains(g)) {
                    gs.add(g);
                    g.buildIterator(gs);
                }
        }
        
        @Override
        public Iterator<Gate> iterator() {
            final Set<Gate> set = new HashSet<Gate>();
            buildIterator(set);
            return set.iterator();
        }
        
        @Override
        public void dispose() {
            CollisionDetector.getInstance().removeRigidBody(display);
            CollisionDetector.getInstance().removeRigidBody(goal);
            
            display.dispose();
            goal.dispose();
        }
    }
    
    public static class Head {
        public String name;
        public int id;
        public FileHandle file;
    }
    
    public static interface EventListener {
        public void onFinished();
        
        public void onGatePassed(Gate gate, Iterable<Gate> current);
        
        public void onUpdate();
        
        public void onRender();
    }
    
    public static class EventAdapter implements EventListener {
        @Override
        public void onFinished() {
        }
        
        @Override
        public void onGatePassed(Gate gate, Iterable<Gate> current) {
        }
        
        @Override
        public void onUpdate() {
        }
        
        @Override
        public void onRender() {
        }
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
    public Collection<GameObject> components;
    public final Perspective start;
    private Gate virtualGate;
    private Gate startingGate;
    private final Environment environment;
    private List<EventListener> eventListeners = new ArrayList<EventListener>();
    private final Map<String, GameModel> dependencies;
    // public final Collection<String> scripts;
    
    private GameObject borderObject = null;
    
    // private float initTime = 0;
    private float leftTime = 0;
    
    public float getLeftTime() {
        return leftTime;
    }
    
    public void setLeftTime(float leftTime) {
        this.leftTime = leftTime;
    }
    
    public int getGatesNumber() {
        int count = 0;
        for (Gate gate : allGates()) {
            count++;
        }
        return count;
    }
    
    private int CollisionTime = 0;
    private int leftCollisionTime = 0;
    
    public int getLeftCollisionTime() {
        return leftCollisionTime;
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
    
    public Level(String name, Perspective start, Collection<GameObject> components, Map<String, GameModel> dependencies, Gate startingGate) {
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
        if (currentGates().contains(gate)) {
            gate.passedTimes++;
            activeGatePassed(gate);
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
            s.onGatePassed(gate, virtualGate.successors);
        virtualGate = gate;
        if (gate.successors.isEmpty()) {
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
    
    public Collection<Gate> currentGates() {
        return Collections.unmodifiableCollection(virtualGate.successors);
    }
    
    public Iterable<Gate> allGates() {
        return startingGate;
    }
    
    public Iterable<Gate> remainingGates() {
        return virtualGate;
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
                g.goal.hide();
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
        final int numberOfEventListeners = eventListeners.size();
        for (int i = 0; i < numberOfEventListeners; i++) {
            eventListeners.get(i).onUpdate();
        }
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
        final int numberOfEventListeners = eventListeners.size();
        for (int i = 0; i < numberOfEventListeners; i++) {
            eventListeners.get(i).onRender();
        }
        // TODO: use some Array like structure for components so that it can be
        // used in a for (i...) loop, due to performance reasons
        for (GameObject c : components) {
            c.render(batch, environment, camera);
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