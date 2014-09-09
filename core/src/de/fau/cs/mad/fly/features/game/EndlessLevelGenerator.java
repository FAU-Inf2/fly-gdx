package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.fau.cs.mad.fly.features.upgrades.ChangeTimeUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.InstantSpeedUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.LinearSpeedUpgradeHandler;
import de.fau.cs.mad.fly.features.upgrades.types.ChangeTimeUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.Collectible;
import de.fau.cs.mad.fly.features.upgrades.types.InstantSpeedUpgrade;
import de.fau.cs.mad.fly.features.upgrades.types.LinearSpeedUpgrade;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameControllerBuilder;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.GateDisplay;
import de.fau.cs.mad.fly.res.GateGoal;
import de.fau.cs.mad.fly.res.Level;

/**
 * generates an endless random level
 * 
 * @author Sebastian
 * 
 */
public class EndlessLevelGenerator {
	
	private AssetManager manager;
	
	private LinearSpeedUpgradeHandler linearSpeedHandler;
	private InstantSpeedUpgradeHandler instantSpeedHandler;
	private ChangeTimeUpgradeHandler changeTimeHandler;
	
	private Level level;
	private Vector3 lastDirection;
	private int currGate = 4;
	
	private List<GateGoal> gateGoals;
	private List<GateGoal> predecessors;
	
	private GateGoal lastGate;
	private GateGoal lastGatePassed;
	private GateGoal lastRemoved;
	
	private float maxAngle = 45;
	private float minAngle = 0.1f;
	private int lastGateId = -1;

	private float lastDistance = 0.f;
	private float difficulty;
	private boolean increasingDifficulty;
	
	private int defaultGateScore = 50;
	
	/**
	 * 
     * @param level
     *            - the level that was initially loaded and is now expanded on
     *            the fly
	 */
	public EndlessLevelGenerator(Level level, GameControllerBuilder builder) {
		this.manager = Assets.manager;
    	
		manager.load(new AssetDescriptor<GameModel>("models/timeUpgrade/timeUpgrade", GameModel.class));
		manager.load(new AssetDescriptor<GameModel>("models/speedUpgrade/speedUpgrade", GameModel.class));
		
		this.linearSpeedHandler = new LinearSpeedUpgradeHandler();
		this.instantSpeedHandler = new InstantSpeedUpgradeHandler();
		this.changeTimeHandler = new ChangeTimeUpgradeHandler();
		
		builder.addFeatureToLists(linearSpeedHandler);
		builder.addFeatureToLists(instantSpeedHandler);
		builder.addFeatureToLists(changeTimeHandler);
		
		this.level = level;
		
		this.difficulty = 0.f;
		this.maxAngle = 45.f + 4.5f * difficulty;
		this.increasingDifficulty = true;
		
		this.gateGoals = new ArrayList<GateGoal>();
		this.predecessors = new ArrayList<GateGoal>();
		
		lastGatePassed = new GateGoal(-2, level.getDependency("hole"), defaultGateScore, new GateDisplay(level.getDependency("torus")));
		lastGatePassed.successors = new int[0];
		lastRemoved = new GateGoal(-3, level.getDependency("hole"), defaultGateScore, new GateDisplay(level.getDependency("torus")));
		lastRemoved.successors = new int[0];
		
		this.gateGoals = level.getGateCircuit().allGateGoals();
		int size = gateGoals.size();
		
		lastGate = gateGoals.get(size - 1);
        if (size > 1) {
			lastDirection = lastGate.getPosition().cpy().sub(gateGoals.get(size - 2).getPosition());
		} else {
			lastDirection = level.start.viewDirection;
		}
		predecessors.add(lastGate);
	}
	
	public List<GateGoal> getGates() {
		return gateGoals;
	}
	
	public int getExtraTime() {
		return (int) (lastDistance / difficulty) + 2;
	}
	
	/**
	 * adds a randomly generated gate to the level
	 * 
     * @param passed
     *            - the gate that was passed and now should be removed from the
     *            level
	 */
	public void addRandomGate(GateGoal passed) {
		Gdx.app.log("myApp", "addRandomGate");
        if (passed.getGateId() != lastGateId) {
        	lastDistance = lastGatePassed.getPosition().cpy().dst(passed.getPosition());
        	
			List<GateGoal> newGates = generateRandomGates(predecessors);
			
			predecessors.clear();
			int size = newGates.size();
            for (int i = 0; i < size; i++) {
                GateGoal newGate = newGates.get(i);
				level.getGateCircuit().addGate(newGate);
				gateGoals.add(newGate);
				predecessors.add(newGate);
			}
			
			// for restart (not needed now)
            /*
             * for(Gate g : passed.successors) { level.setStartGate(g);
             * level.start.viewDirection = g.display.getPosition().cpy(); break;
             * } level.start.position =
             * passed.display.getPosition().cpy().sub(lastDirection
             * .cpy().scl(5));
             */
			
            lastGateId = passed.getGateId();
			
			// removing passed gate and all possible parallel gates
			size = lastRemoved.successors.length;
            for (int i = 0; i < size; i++) {
				int id = lastRemoved.successors[i];
				GateGoal successor = level.getGateCircuit().getGateGoalById(id);
				
				level.getGateCircuit().removeGate(successor);
				
				gateGoals.remove(successor);
				successor.dispose();
			}
			lastRemoved = lastGatePassed;
			lastGatePassed = passed;
			
		}
	}
	
	/**
     * generates a random number of Gates depending on the positions and
     * directions of the previous gates
	 * 
     * @param predecessor
     *            - the previous Gate
	 * @return - the randomly generated Gate
	 */
	private List<GateGoal> generateRandomGates(List<GateGoal> predecessors) {
		CollisionDetector collisionDetector = CollisionDetector.getInstance();
        
		List<GateGoal> newGates = new ArrayList<GateGoal>();
		float rand = 1.f;
		float min = 0.8f;
        Vector3 newLastDirection = new Vector3(0, 0, 0);
		
        float shortestDistance = 1000.f;
        
        Matrix4[] t = new Matrix4[predecessors.size()];
        for (int i = 0; i < predecessors.size(); i++) {
			t[i] = predecessors.get(i).transform.cpy();
		}
        
        while (rand > min) {
			float distance = (MathUtils.random(5) + 6.f);
			
			int random = MathUtils.random(-1, 1);
			
			distance -= difficulty * random / 2.f;
			
			if(distance < shortestDistance) {
				shortestDistance = distance;
			}
			
            // Gdx.app.log("myApp", "generateRandomGate");
			GateDisplay newDisplay = new GateDisplay(level.getDependency("torus"));
			GateGoal newGoal = new GateGoal(currGate, level.getDependency("hole"), defaultGateScore, newDisplay);
			newDisplay.setGoal(newGoal);

			Vector3 newDirection;
			
			boolean use = true;
			int count = 50;
            // places the gate at a random position until it is far enough to
            // any other gate
			do {
				newDirection = lastDirection.cpy();
                newDirection.rotate(new Vector3(0, 0, 1), randomAngle());
                newDirection.rotate(new Vector3(0, 1, 0), randomAngle());
                newDirection.rotate(new Vector3(1, 0, 0), randomAngle());
				newDirection.nor();
				
                // display.transform =
                // predecessors.get(0).display.transform.cpy();
				newDisplay.transform.avg(t);
				
				newDisplay.transform.rotate(lastDirection, newDirection);
                newDisplay.transform.translate(new Vector3(0, 1, 0).scl(distance));
				
                if (count-- <= 0) {
					use = false;
					break;
				}
				
            } while (checkForSpawnCollision(newDisplay.getPosition(), newGates, 5.0f));
			
            if (!use) {
				rand = MathUtils.random();
				continue;
			}
            
			newGoal.transform = newDisplay.transform.cpy();
			newGoal.successors = new int[0];
			
			newGoal.createShapeAndRigidBody(collisionDetector);
			newDisplay.createShapeAndRigidBody(collisionDetector);
			
            // lastDirection = newDirection.cpy();
			newLastDirection.add(newDirection.cpy());
			
			newGates.add(newGoal);
			rand = MathUtils.random();
			currGate++;
			min += 0.05f;
		}
		
		newLastDirection.scl(1.f / newGates.size());
		lastDirection = newLastDirection.cpy();
        
        //TODO: find nice value
        if(shortestDistance > 10.f) {
        	Gdx.app.log("addUpgrade", "adding");
        	addRandomUpgrade(t, shortestDistance);
        	Gdx.app.log("addUpgrade", "after adding");
        }
		
		int size = newGates.size();
		int[] newGateIds = new int[size];
        for (int i = 0; i < size; i++) {
            newGateIds[i] = newGates.get(i).getGateId();
		}
		
		size = predecessors.size();
        for (int i = 0; i < size; i++) {
			GateGoal predecessor = predecessors.get(i);
			predecessor.successors = newGateIds;
		}
		
        if (increasingDifficulty && (difficulty < 10.f)) {
            // difficulty = 10.f + (float) currGate;
			difficulty = (float) MathUtils.log(5, currGate);
			maxAngle = 45.f + 4.5f * difficulty;
			minAngle = 0.1f + difficulty;
		}
		
		return newGates;
	}
	
	private void addRandomUpgrade(Matrix4[] matrices, float distance) {
		int random = MathUtils.random(2);
		
		Collectible c = null;
		
		switch(random){
		case 0:
			c = new ChangeTimeUpgrade(manager.get("models/timeUpgrade/timeUpgrade", GameModel.class), 10);
			changeTimeHandler.addObject(c);
			break;
		case 1:
			c = new InstantSpeedUpgrade(manager.get("models/speedUpgrade/speedUpgrade", GameModel.class), 2.f, 5.f);
			instantSpeedHandler.addObject(c);
			break;
		case 2:
            c = new LinearSpeedUpgrade(manager.get("models/speedUpgrade/speedUpgrade", GameModel.class), 1.f, 10.f, 1.f);
            linearSpeedHandler.addObject(c);
			break;
		default:
			break;
		}
		
		if(c != null) {
			c.transform.avg(matrices).translate(lastDirection.cpy().scl(distance / 2.f));
			
			CollisionDetector collisionDetector = CollisionDetector.getInstance();
			c.createShapeAndRigidBody(collisionDetector, c.getType());
			
			level.getCollectibleManager().addCollectible(c);
		}
	}
	
	/**
	 * Calculates a random angle.
     * 
	 * @return angle
	 */
	private float randomAngle() {
		float angle = MathUtils.random(maxAngle * 2) - maxAngle;
		
        if (Math.abs(angle) < minAngle) {
			angle += Math.signum(angle) * minAngle;
		}
		
		return angle;
	}
	
	/**
	 * Checks for spawn collisions.
     * 
     * @param position
     *            The position of the new gate goals.
     * @param gates
     *            List of all the currently existing gates goals.
     * @param distance
     *            The minimum distance between the new goal and the existing
     *            goals.
	 * @return true if too close, false otherwise.
	 */
	private boolean checkForSpawnCollision(Vector3 position, List<GateGoal> gates, float distance) {
		int size = gates.size();
        for (int i = 0; i < size; i++) {
			GateGoal gate = gates.get(i);
            if (position.dst(gate.getPosition()) < distance) {
				return true;
			}
		}
		return false;
	}
    
}