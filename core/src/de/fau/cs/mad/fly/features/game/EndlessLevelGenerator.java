package de.fau.cs.mad.fly.features.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.res.Level;
import de.fau.cs.mad.fly.res.Level.Gate;
import de.fau.cs.mad.fly.res.Perspective;
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
	
	private Gate lastGate;
	
	private float maxAngle = 90;
	private int lastGateId = -1;
	
	/**
	 * 
	 * @param level - the level that was initially loaded and is now expanded on the fly
	 */
	public EndlessLevelGenerator(Level level) {
		this.level = level;
		
		this.gates = new ArrayList<Gate>();
		Collection<GameObject> coll =  new ArrayList<GameObject>();
		coll.addAll(level.components);
		
		level.components = coll;
		
		for(Gate g : level.allGates()) {
			this.gates.add(g);
			
			if(lastGate != null) {
				Gdx.app.log("myApp", "lastGate:  x: " + lastGate.display.getPosition().x + "| y: " + lastGate.display.getPosition().y + "| z: " + lastGate.display.getPosition().z);
				Gdx.app.log("myApp", "g:  x: " + g.display.getPosition().x + "| y: " + g.display.getPosition().y + "| z: " + g.display.getPosition().z);
				lastDirection = g.display.getPosition().cpy().sub(lastGate.display.getPosition().cpy());
				lastDirection.nor();
			}
			lastGate = g;
		}
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
		
		if(passed.id != lastGateId) {
			Gate newGate = generateRandomGate(gates.get(gates.size()-1));
			
			
			
			level.components.add(newGate.display);
			//level.components.add(newGate.goal);
			
			gates.add(newGate);
			
			for(Gate g : passed.successors) {
				level.setStartGate(g);
				break;
			}
			level.start.position = passed.display.getPosition().cpy().sub(lastDirection.cpy().scl(5));
			//level.start.viewDirection = lastDirection.cpy().scl(-1);
			level.start.viewDirection = gates.get(1).display.getPosition().cpy();
			lastGateId = passed.id;
			
			// removing passed gate
			level.components.remove(passed.display);
			level.components.remove(passed.goal);
			
			
			passed.dispose();
			/*CollisionDetector collisionDetector = ((Fly) Gdx.app.getApplicationListener()).getGameController().getCollisionDetector();
			collisionDetector.removeRigidBody(passed.display);
			collisionDetector.removeRigidBody(passed.goal);
			
			if(passed.display != null) {
				passed.display.dispose();
				passed.goal.dispose();
			}*/
			
			gates.remove(passed);
			
		}
	}
	
	/**
	 * generates a random Gate depending on the position and direction of the previous gate
	 * 
	 * @param predecessor - the previous Gate
	 * @return - the randomly generated Gate
	 */
	private Gate generateRandomGate(Gate predecessor) {
		
		float distance = MathUtils.random(5) + 5.f;
		
		Vector3 newDirection = lastDirection.cpy();
		newDirection.rotate(new Vector3(0,0,1), MathUtils.random(maxAngle) - maxAngle/2);
		newDirection.rotate(new Vector3(0,1,0), MathUtils.random(maxAngle) - maxAngle/2);
		newDirection.rotate(new Vector3(1,0,0), MathUtils.random(maxAngle) - maxAngle/2);
		newDirection.nor();
		
		Gate newGate = new Gate(currGate);
		
		// Creating display and goal for the new Gate
		GameObject display = new GameObject(level.getDependency("torus"));
		display.modelId = "torus";
		display.id = "gate" + Integer.toString(currGate); 
		
		GameObject goal = new GameObject(level.getDependency("hole"));
		goal.modelId = "hole";
		goal.id = "gate" + Integer.toString(currGate) + "hole"; 
		
		display.transform = predecessor.display.transform.cpy();
		
		display.transform.rotate(lastDirection, newDirection);
		display.transform.translate(new Vector3(0,1,0).scl(distance));
		
		goal.transform = display.transform.cpy();
		
		
		// add display and goal to the new Gate
		newGate.display = display;
		newGate.goal = goal;
		newGate.successors = new ArrayList<Level.Gate>();
		
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
		
		lastDirection = newDirection.cpy();
		currGate++;
		
		Collection<Level.Gate> successors = new ArrayList<Level.Gate>();
		successors.add(newGate);
		predecessor.successors = successors;
		
		return newGate;
	}

}