package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.ICollisionListener;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The manager for the whole collision detection and handling stuff.
 * 
 * @author Tobi
 */
public class CollisionDetector implements Disposable {
	
	/**
	 * The collision shape manager used to store the shape for the objects.
	 */
	CollisionShapeManager shapeManager;
	
	/**
	 * The rigid body info manager used to store the rigid body construction info for the objects.
	 */
	RigidBodyInfoManager rigidBodyInfoManager;
	
	/**
	 * Singleton collision detector instance.
	 */
	private static CollisionDetector instance;
	
	/**
	 * Creates the collision detector if there is not already an instance created.
	 */
	public static void createCollisionDetector() {
		if(instance == null) {
			instance = new CollisionDetector();
		}
	}

	/**
	 * Getter for the collision detector singleton.
	 * @return instance
	 */
	public static CollisionDetector getInstance() {
		return instance;
	}

	/**
	 * The main listener for the collision detection.
	 * <p>
	 * It transmits the collision events to the added collision listeners.
	 * 
	 * @author Tobi
	 */
	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;

		public CollisionContactListener() {
			listeners = new ArrayList<ICollisionListener>();
		}

		/**
		 * Adds a new collision listener.
		 * 
		 * @param listener		Collision listener which has to implement the ICollisionListener interface.
		 */
		public void addListener(ICollisionListener listener) {
			listeners.add(listener);
		}
		
		/**
		 * Removes a listener from the list. Can be used to save performance if the interface does not need to get informed for collisions anymore.
		 * 
		 * @param listener		Collision listener which has to implement the ICollisionListener interface.
		 */
		public void removeListener(ICollisionListener listener) {
			listeners.remove(listener);
		}

		@Override @SuppressWarnings("unchecked")
		public void onContactStarted(btCollisionObject o1, btCollisionObject o2) {
			GameObject g1 = (GameObject) o1.userData;
			GameObject g2 = (GameObject) o2.userData;
			Gdx.app.log("CollisionDetector.onContactStarted", "g1 = " + g1.id + " (userData = " + g1.userData.getClass() + "), g2 = " + g2.id + " (userData = " + g2.userData.getClass() + ")" );
			outer: for( ICollisionListener listener : listeners ) {
				for ( Method m : listener.getClass().getMethods() ) {
					if ( !m.getName().equals("onCollision") )
						continue;
					
					//System.out.println(m.getParameterTypes()[0] + " - " + m.getParameterTypes()[1]);
					Class<?> c0 = m.getParameterTypes()[0];
					Class<?> c1 = m.getParameterTypes()[1];
					
					if(c0.isAssignableFrom(g1.userData.getClass()) && c1.isAssignableFrom(g2.userData.getClass())) {
						listener.onCollision(g1.userData, g2.userData);
					} else if(c1.isAssignableFrom(g1.userData.getClass()) && c0.isAssignableFrom(g2.userData.getClass())) {
						listener.onCollision(g2.userData, g1.userData);
					}
					continue outer; // go to the next listener.
				}
				listener.onCollision(g1, g2);
			}
		}
	}
	
	/**
	 * The flags to filter collisions.
	 */
	public final static short DUMMY_FLAG = 1 << 7;
	public final static short OBJECT_FLAG = 1 << 8;
	public final static short PLAYER_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	CollisionContactListener contactListener;
	btBroadphaseInterface broadphase;
	
	btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;
	
	DebugDrawer debugDrawer;

	protected CollisionDetector() {
		shapeManager = new CollisionShapeManager();
		rigidBodyInfoManager = new RigidBodyInfoManager();

		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0.0f, 0.0f, 0.0f));
		
		contactListener = new CollisionContactListener();
	}

	/**
	 * Getter for the collision shape manager.
	 * 
	 * @return CollisionShapeManager
	 */
	public CollisionShapeManager getShapeManager() {
		return shapeManager;
	}
	
	/**
	 * Getter for the rigid body info manager.
	 * 
	 * @return RigidBodyInfoManager
	 */
	public RigidBodyInfoManager getRigidBodyInfoManager() {
		return rigidBodyInfoManager;
	}
	
	/**
	 * Creates a new rigid body.
	 * 
	 * @param instance			The game object for which the rigid body has to be created.
	 * @param shape				The collision shape. Should be created with the help of the collision shape manager.
	 * @param userData			The user data to store in the rigid body.
	 * @param rigidBodyInfo		The rigid body construction info. Should be created with the help of the rigid body info manager.
	 * @return					btRigidBody
	 */
	public static btRigidBody createRigidBody(final GameObject instance, final btCollisionShape shape, final GameObject userData, btRigidBody.btRigidBodyConstructionInfo rigidBodyInfo) {
		btRigidBody rigidBody = new btRigidBody(rigidBodyInfo);
		rigidBody.setCollisionShape(shape);
		rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		
		rigidBody.setWorldTransform(instance.transform);
		rigidBody.userData = userData;
		
		// TODO: use contact callback filtering
		//rigidBody.setContactCallbackFlag(GROUND_FLAG);
		//rigidBody.setContactCallbackFilter(0);
		
		return rigidBody;
	}
	
	/**
	 * Adds a rigid body to the dynamics world from a given game object.
	 * <p>
	 * It also sets the filter group and mask.
	 * 
	 * @param gameObject		The game object with the rigid body to add.
	 */
	public void addRigidBody(final GameObject gameObject) {
		dynamicsWorld.addRigidBody(gameObject.getRigidBody(), gameObject.filterGroup, gameObject.filterMask);
	}
	
	/**
	 * Removes a rigid body from the dynamics world from a given game object.
	 * 
	 * @param gameObject		The game object with the rigid body to remove.
	 */
	public void removeRigidBody(final GameObject gameObject) {
		dynamicsWorld.removeRigidBody(gameObject.getRigidBody());
	}

	/**
	 * Getter for the collision contact listener
	 * 
	 * @return CollisionContactListener
	 */
	public CollisionContactListener getCollisionContactListener() {
		return contactListener;
	}

	/**
	 * Performs the collision detection and handling.
	 * 
	 * @param delta			Time after the last call.
	 */
	public void perform(float delta) {		
		// TODO: check if the values are okay
		dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
	}

	@Override
	public void dispose() {
		dynamicsWorld.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();
		contactListener.dispose();
		constraintSolver.dispose();

		rigidBodyInfoManager.dispose();
		shapeManager.dispose();
		Gdx.app.log("CollisionDetector", "Collision disposed.");
		
		instance = null;
	}
}
