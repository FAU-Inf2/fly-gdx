package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.res.Level.Gate;
/**
 * generates an endless random level
 * @author Sebastian
 *
 */
public class EndlessLevelGenerator {
	
	private Level level;
	private Vector3 lastDirection;
	private int currGate = 4;
	
	private List<Gate> gates;
	private List<Gate> predecessors;
	
	private Gate lastGate;
	private Gate lastGatePassed;
	
	private float maxAngle = 45;
	private int lastGateId = -1;
	
	/**
	 * 
	 * @param level - the level that was initially loaded and is now expanded on the fly
	 */
	public EndlessLevelGenerator(Level level) {
		this.level = level;
		
		this.gates = new ArrayList<Gate>();
		this.predecessors = new ArrayList<Gate>();
		List<GameObject> coll =  new ArrayList<GameObject>();
		coll.addAll(level.components);
		
		lastGatePassed = new Gate(-2);
		//TODO: lastGatePassed.successors = new ArrayList<Gate>();
		
		level.components = coll;
		
		for(Gate g : level.allGates()) {
			this.gates.add(g);
			
			if(lastGate != null) {
				lastDirection = g.display.getPosition().cpy().sub(lastGate.display.getPosition().cpy());
				lastDirection.nor();
			}
			lastGate = g;
		}
		predecessors.add(lastGate);
	}
	
	public List<Gate> getGates() {
		return gates;
	}
	
	/**
	 * adds a randomly generated gate to the level
	 * 
	 * @param passed - the gate that was passed and now should be removed from the level
	 */
	public void addRandomGate(Gate passed) {
		Gdx.app.log("myApp", "addRandomGate");
		if(passed.id != lastGateId) {
			//Gate newGate = generateRandomGate(gates.get(gates.size()-1));
			List<Gate> newGates = generateRandomGates(predecessors);
			
			predecessors.clear();
			for(Gate newGate : newGates) {
				Gdx.app.log("myApp", "newGate");
				level.components.add(newGate.display);
				//level.components.add(newGate.goal);
				
				gates.add(newGate);
				predecessors.add(newGate);
			}
			
			
			// for restart (not needed now)
			/*for(Gate g : passed.successors) {
				level.setStartGate(g);
				level.start.viewDirection = g.display.getPosition().cpy();
				break;
			}
			level.start.position = passed.display.getPosition().cpy().sub(lastDirection.cpy().scl(5));*/
			
			lastGateId = passed.id;
			
			
			// removing passed gate and all possible parallel gates
//			TODO: for(Gate successor : lastGatePassed.successors) {
//				level.components.remove(successor.display);
//				level.components.remove(successor.goal);
//				
//				gates.remove(successor);
//				successor.dispose();
//			}
			
			lastGatePassed = passed;
			
		}
	}
	
	/**
	 * generates a random number of Gates depending on the positions and directions of the previous gates
	 * 
	 * @param predecessor - the previous Gate
	 * @return - the randomly generated Gate
	 */
	private List<Gate> generateRandomGates(List<Gate> predecessors) {

		List<Gate> newGates = new ArrayList<Gate>();
		float rand = 1.f;
		float distance = MathUtils.random(5) + 5.f;
		Vector3 newLastDirection = new Vector3(0,0,0);
		
		while(rand > 0.8f) {
			//Gdx.app.log("myApp", "generateRandomGate");
			Gate newGate = new Gate(currGate);

			// Creating display and goal for the new Gate
			GameObject display = new GameObject(level.getDependency("torus"));
			display.modelId = "torus";
			display.id = "gate" + Integer.toString(currGate); 
			
			GameObject goal = new GameObject(level.getDependency("hole"));
			goal.modelId = "hole";
			goal.id = "gate" + Integer.toString(currGate) + "hole"; 

			Vector3 newDirection;
			
			do {
				newDirection = lastDirection.cpy();
				newDirection.rotate(new Vector3(0,0,1), MathUtils.random(maxAngle * 2) - maxAngle);
				newDirection.rotate(new Vector3(0,1,0), MathUtils.random(maxAngle * 2) - maxAngle);
				newDirection.rotate(new Vector3(1,0,0), MathUtils.random(maxAngle * 2) - maxAngle);
				newDirection.nor();
				
				
				Matrix4[] t = new Matrix4[predecessors.size()];
				for(int i = 0; i < predecessors.size(); i++) {
					t[i] = predecessors.get(i).display.transform.cpy();
				}
				
				//display.transform = predecessors.get(0).display.transform.cpy();
				display.transform.avg(t);
				
				display.transform.rotate(lastDirection, newDirection);
				display.transform.translate(new Vector3(0,1,0).scl(distance));
				
			} while(checkForSpawnCollision(display.getPosition(), newGates, 5.0f));

			goal.transform = display.transform.cpy();
			
			// add display and goal to the new Gate
			newGate.display = display;
			newGate.goal = goal;
			// TODO: newGate.successors = new ArrayList<Level.Gate>();
			
			addRigidBody(newGate);
			
			//lastDirection = newDirection.cpy();
			newLastDirection.add(newDirection.cpy());
			currGate++;
			
			newGates.add(newGate);
			rand = MathUtils.random();
		}
		
		newLastDirection.scl(1.f / newGates.size());
		lastDirection = newLastDirection.cpy();
		
		for(Gate predecessor : predecessors) {
			//TODO: predecessor.successors = newGates;
		}
		
		return newGates;
	}
	
	private void addRigidBody(Gate newGate) {
		// add display and goal to the collisionDetector
		CollisionDetector collisionDetector = CollisionDetector.getInstance();
		/////////////////////////////////////////////////////////////////////////////////////
		if (newGate.display.getRigidBody() == null) {
			//Gdx.app.log("Builder.init", "Display RigidBody == null");
			btCollisionShape displayShape = collisionDetector.getShapeManager().createStaticMeshShape(newGate.display.modelId, newGate.display);
			newGate.display.createRigidBody(newGate.display.modelId, displayShape, 0.0f, CollisionDetector.OBJECT_FLAG, CollisionDetector.ALL_FLAG);
		}
		
		collisionDetector.addRigidBody(newGate.display);
		/////////////////////////////////////////////////////////////////////////////////////////////
		if (newGate.goal.getRigidBody() == null) {
			//Gdx.app.log("Builder.init", "Goal RigidBody == null");
			btCollisionShape goalShape = collisionDetector.getShapeManager().createBoxShape(newGate.goal.modelId + ".goal", new Vector3(1.0f, 0.05f, 1.0f));
			newGate.goal.hide();
			newGate.goal.userData = newGate;
			newGate.goal.createRigidBody(newGate.goal.modelId + ".goal", goalShape, 0.0f, CollisionDetector.DUMMY_FLAG, CollisionDetector.PLAYER_FLAG);
			newGate.goal.getRigidBody().setCollisionFlags(newGate.goal.getRigidBody().getCollisionFlags() | btRigidBody.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		}
		collisionDetector.addRigidBody(newGate.goal);
		
		//////////////////////////////////////////////////////////
	}
	
	private boolean checkForSpawnCollision(Vector3 position, List<Gate> gates, float distance) {
		for(Gate gate : gates) {
			if(position.dst(gate.display.getPosition()) < distance) {
				return true;
			}
		}
		return false;
	}

}