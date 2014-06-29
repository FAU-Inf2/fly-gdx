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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The manager for the whole collision detection and handling stuff.
 * 
 * @author Tobi
 *
 */
public class CollisionDetector implements Disposable {

	CollisionShapeManager shapeManager;
	RigidBodyInfoManager rigidBodyInfoManager;

	public final static short DUMMY_FLAG = 1 << 7;
	public final static short OBJECT_FLAG = 1 << 8;
	public final static short PLAYER_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;

	/**
	 * The main listener for the collision detection.
	 * <p>
	 * It transmits the collision events to the added collision listeners.
	 * 
	 * @author Tobi
	 *
	 */
	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;
		
		private final Map<Type, Object> m = new HashMap<Type, Object>(4);

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

		@Override @SuppressWarnings("unchecked")
		public void onContactStarted(btCollisionObject o1, btCollisionObject o2) {
			GameObject g1 = (GameObject) o1.userData;
			GameObject g2 = (GameObject) o2.userData;
			Gdx.app.log("CollisionDetector.onContactStarted", "g1 = " + g1.id + " (userData = " + g1.userData.getClass() + "), g2 = " + g2.id + " (userData = " + g2.userData.getClass() + ")" );
			m.clear();
			// Store in hash to pass values in correct order later.
			m.put(g1.userData.getClass(), g1.userData);
			// if same class, g1 will get overwritten, so save a reference
			Object ret = m.put(g2.userData.getClass(), g2.userData);
			outer: for( ICollisionListener listener : listeners ) {
				for ( Type t : listener.getClass().getGenericInterfaces() )
					if ( t instanceof ParameterizedType ) {
						ParameterizedType type = (ParameterizedType) t;
						if ( type.getRawType() != ICollisionListener.class )
							continue;
						// Retrieve from map.
						Object first = m.get(type.getActualTypeArguments()[0]);
						Object second = m.get(type.getActualTypeArguments()[1]);
						// in case if same class, maintain Bullet's order
						if ( first == second )
							first = ret;
						if ( first != null && second != null ) // the listener wants to know about this collision
							listener.onCollision(first, second);
						continue outer; // go to the next listener.
					}
				// Should never come to this
				listener.onCollision(g1, g2);
			}
		}
	}

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	CollisionContactListener contactListener;
	btBroadphaseInterface broadphase;
	
	btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;
	
	DebugDrawer debugDrawer;

	public CollisionDetector() {
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
		// TODO: add parameter to check if the rigid body should not respond to collisions
		//  | btRigidBody..CollisionFlags.CF_NO_CONTACT_RESPONSE
		
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
	 * @param o			The game object with the rigid body to add.
	 */
	public void addRigidBody(final GameObject o) {
		dynamicsWorld.addRigidBody(o.getRigidBody(), o.filterGroup, o.filterMask);
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
	}
}
