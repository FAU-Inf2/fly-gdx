package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.player.Spaceship;

/**
 * Manages the logic of the gates like storing the gate list and handling the gate passing.
 * 
 * @author Tobi
 *
 */
public class GateCircuit implements IFeatureLoad, ICollisionListener<Spaceship, Gate> {

    /**
     * The gate that has been passed recently, at the beginning the dummy gate.
     */
    private Gate virtualGate;
    
    /**
     * The starting gate of the gate circuit.
     */
    private Gate startingGate;
    
    /**
     * Maps the id to the corresponding gate.
     */
    private Map<Integer, Gate> gates = new HashMap<Integer, Gate>();
    
    /**
     * The list of all the gates of the gate circuit.
     */
    private List<Gate> allGates = new ArrayList<Gate>();
    
    /**
     * The list of the event listeners that want to be notified if a gate is passed or the gate circuit is finished.
     */
    private List<EventListener> eventListeners = new ArrayList<EventListener>();
    
    /**
     * True if the last gate was reached, false otherwise.
     */
    private boolean reachedLastGate = false;
    
    /**
     * The level where the gate circuit is located.
     */
    protected Level level = null;
 
    /**
     * Creates a new gate circuit.
     * @param startingGate		The starting gate of the gate circuit.
     */
    public GateCircuit(Gate startingGate) {
        this.virtualGate = startingGate;
        this.startingGate = startingGate;
    }
  
    /**
     * Adds an event listener to the gate circuit.
     * @param listener
     */
    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }
    
    /**
     * Returns the number of gates in the gate circuit.
     * @return size of the gate circuit.
     */
    public int getGatesNumber() {
        return allGates().size();
    }
    
    /**
     * Returns a gate by a given id.
     * @param id		The id of the gate.
     * @return the gate with the given id.
     */
    public Gate getGateById(int id) {
        return gates.get(id);
    }
    
    /**
     * Adds a gate to the gate circuit.
     * @param gate		The gate to add.
     */
    public void addGate(Gate gate) {
        gates.put(gate.id, gate);
        allGates.add(gate);
    }
    
    /**
     * Fills the Map that maps id to the corresponding gate and the list of all
     * gates.
     * @param gates		The map with all the gates.
     */
    public void setGates(Map<Integer, Gate> gates) {
        this.gates = gates;
        this.allGates.addAll(gates.values());
    }
    
    /**
     * Returns if the last gate is already reached.
     * @return true if the last gate is reached, false otherwise.
     */
    public boolean isReachedLastGate() {
        return reachedLastGate;
    }
    
    /**
     * Setter to tell if the last gate was reached.
     * @param reachedLastGate
     */
    protected void setReachedLastGate(boolean reachedLastGate) {
        this.reachedLastGate = reachedLastGate;
    }
    
    /**
     * Checks if the currently passed gate is one of the current active gates.
     * @param gate		The gate to check.
     */
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
    
    /**
     * Calls the event listeners for a passed gate and finishes the circuit if it was the last gate and it has no successors.
     * @param gate		The gate that was passed.
     */
    public void activeGatePassed(Gate gate) {
        for (EventListener s : eventListeners)
            s.onGatePassed(gate);
        virtualGate = gate;
        if (gate.successors.length == 0) {
            reachedLastGate = true;
            circuitFinished();
        }
    }
    
    /**
     * Calls the event listeners for the finished gate circuit.
     */
    protected void circuitFinished() {
    	level.finishLevel();
        for (EventListener s : eventListeners) {
            s.onFinished();
        }
    }
    
    /**
     * Setter for the starting gate.
     * @param startingGate		The starting gate.
     */
    public void setStartGate(Gate startingGate) {
        this.startingGate = startingGate;
    }
    
    /**
     * Getter for the currently active gates.
     * @return currently active gates.
     */
    public int[] currentGates() {
        return virtualGate.successors;
    }
    
    /**
     * Getter for all the gates of the gate circuit.
     * @return list of all the gates.
     */
    public List<Gate> allGates() {
        return allGates;
    }
    
    /**
     * Creates the rigid bodies with convenient parameters for the gate models
     * and the gate goals in the level.
     */
    public void createGateRigidBodies() {
        CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
        Gdx.app.log("GateCircuit.createGateRigidBodies", "Setting up collision for level gates.");
        
        for (Gate g : allGates()) {
            if (g.display.getRigidBody() == null) {
				btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(g.display.modelId, g.display);
				g.display.createRigidBody(g.display.modelId, displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
				
				// different scaling for the gates is buggy
				/*g.display.transform.scl(g.display.scaling);
				g.display.getRigidBody().getCollisionShape().setLocalScaling(g.display.scaling);*/
            }
            collisionDetector.addRigidBody(g.display);
            
            if (g.goal.getRigidBody() == null) {
				btCollisionShape goalShape = collisionDetector.getShapeManager().createBoxShape(g.goal.modelId + ".goal", new Vector3(0.8f, 0.1f, 0.8f));
				g.goal.userData = g;
				g.goal.createRigidBody(g.goal.modelId + ".goal", goalShape, 0.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
				g.goal.getRigidBody().setCollisionFlags(g.goal.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
				
				// different scaling for the gates is buggy
				/*g.goal.transform.scl(g.display.scaling);
				g.goal.getRigidBody().getCollisionShape().setLocalScaling(g.display.scaling);*/
            }
            collisionDetector.addRigidBody(g.goal);
        }
    }
    
    /**
     * Resets the gate circuit.
     * <p>
     * Clears the event listeners and sets the virtual gate to the starting gate.
     */
    public void reset() {
        eventListeners.clear();
        virtualGate = startingGate;
    }
	
	@Override
	public void load(GameController game) {
		activeGatePassed(virtualGate);
	}
	
	@Override
	public void onCollision(Spaceship spaceship, Gate gate) {
		gatePassed(gate);
	}
}
