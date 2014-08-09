package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;

import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.player.Spaceship;

/**
 * Manages the logic of the gates like storing the gate list and handling the gate passing.
 * 
 * @author Tobi
 *
 */
public class GateCircuit implements IFeatureLoad, ICollisionListener {

    /**
     * The gate that has been passed recently, at the beginning the dummy gate.
     */
    private GateGoal virtualGate;
    
    /**
     * The starting gate of the gate circuit.
     */
    private GateGoal startingGate;
    
    /**
     * Maps the id to the corresponding gate.
     */
    private Map<Integer, GateGoal> gates = new HashMap<Integer, GateGoal>();
    
    /**
     * The list of all the gates of the gate circuit.
     */
    private List<GateGoal> allGateGoals = new ArrayList<GateGoal>();
    
    /**
     * The list of all the gates of the gate circuit.
     */
    private List<GateDisplay> allGateDisplays = new ArrayList<GateDisplay>();
    
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
    public GateCircuit(GateGoal startingGate) {
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
        return allGateGoals().size();
    }
    
    /**
     * Returns a gate goal by a given id.
     * @param id		The id of the gate.
     * @return the gate with the given id.
     */
    public GateGoal getGateGoalById(int id) {
        return gates.get(id);
    }
    
    /**
     * Adds a gate to the gate circuit.
     * @param gate		The gate goal to add.
     */
    public void addGate(GateGoal gate) {
        gates.put(gate.getId(), gate);
        allGateGoals.add(gate);
        allGateDisplays.add(gate.getDisplay());
    }
    
    /**
     * Removes a gate from the gate circuit.
     * @param gate		The gate goal to remove.
     */
    public void removeGate(GateGoal gate) {
        gates.remove(gate);
        allGateGoals.remove(gate);
        allGateDisplays.remove(gate.getDisplay());
    }
    
    /**
     * Fills the Map that maps id to the corresponding gate and the list of all
     * gates.
     * @param gates		The map with all the gates.
     */
    public void setGates(Map<Integer, GateGoal> gates) {
        this.gates = gates;
        allGateGoals.addAll(gates.values());
        for(GateGoal g : allGateGoals) {
        	if(g.getDisplay() != null) {
        		allGateDisplays.add(g.getDisplay());
        	}
        }
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
    public void gatePassed(GateGoal gate) {
        int numberOfSuccessorGates = virtualGate.successors.length;
        for (int i = 0; i < numberOfSuccessorGates; i++) {
            if (gate.getId() == virtualGate.successors[i]) {
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
    public void activeGatePassed(GateGoal gate) {
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
    public void setStartGate(GateGoal startingGate) {
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
     * Getter for all the gate goals of the gate circuit.
     * @return list of all the gates goals.
     */
    public List<GateGoal> allGateGoals() {
        return allGateGoals;
    }
    
    /**
     * Getter for all the gate displays of the gate circuit.
     * @return list of all the gates displays.
     */
    public List<GateDisplay> allGateDisplays() {
        return allGateDisplays;
    }
    
    /**
     * Creates the rigid bodies with convenient parameters for the gate models
     * and the gate goals in the level.
     */
    public void createGateRigidBodies() {
        CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
        Gdx.app.log("GateCircuit.createGateRigidBodies", "Setting up collision for level gates.");
        
        for (GateGoal g : allGateGoals()) {
        	g.createRigidBody(collisionDetector);
        }
        
        for (GateDisplay d : allGateDisplays()) {
        	d.createRigidBody(collisionDetector);
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
	public void onCollision(GameObject g1, GameObject g2) {
    	if(!(g2 instanceof GateGoal)) {
    		return;
    	}
		
    	GateGoal gate = (GateGoal) g2;
		
		gatePassed(gate);
	}
}
