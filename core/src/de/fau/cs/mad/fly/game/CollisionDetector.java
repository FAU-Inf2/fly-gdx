package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.ICollisionListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollisionDetector implements Disposable {

	CollisionShapeManager shapeManager;
	RigidBodyInfoManager rigidBodyInfoManager;

	public final static short DUMMY_FLAG = 1 << 7;
	public final static short OBJECT_FLAG = 1 << 8;
	public final static short PLAYER_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;

	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;

		public CollisionContactListener() {
			listeners = new ArrayList<ICollisionListener>();
		}

		public void addListener(ICollisionListener listener) {
			listeners.add(listener);
		}


		private final Map<Type, Object> m = new HashMap<Type, Object>(4);
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

		/*debugDrawer = new DebugDrawer();
		dynamicsWorld.setDebugDrawer(debugDrawer);
		debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);*/
	}

	public CollisionShapeManager getShapeManager() {
		return shapeManager;
	}
	
	public RigidBodyInfoManager getRigidBodyInfoManager() {
		return rigidBodyInfoManager;
	}

	/*public static btCollisionObject createObject(final GameObject instance, final btCollisionShape shape, final GameObject userData) {
		btCollisionObject collisionObject = new btCollisionObject();
		collisionObject.setCollisionShape(shape);
		//  | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		collisionObject.setWorldTransform(instance.transform);
		collisionObject.userData = userData;

		return collisionObject;
	}*/
	
	public static btRigidBody createRigidBody(final GameObject instance, final btCollisionShape shape, final GameObject userData, btRigidBody.btRigidBodyConstructionInfo rigidBodyInfo) {
		btRigidBody rigidBody = new btRigidBody(rigidBodyInfo);
		rigidBody.setCollisionShape(shape);
		//  | btRigidBody..CollisionFlags.CF_NO_CONTACT_RESPONSE
		rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		
		rigidBody.setWorldTransform(instance.transform);
		rigidBody.userData = userData;
		
		//rigidBody.setContactCallbackFlag(GROUND_FLAG);
		//rigidBody.setContactCallbackFilter(0);
		
		
		return rigidBody;
	}
	
	public void addRigidBody(final GameObject o) {
		dynamicsWorld.addRigidBody(o.getRigidBody(), o.filterGroup, o.filterMask);
	}

	public CollisionContactListener getCollisionContactListener() {
		return contactListener;
	}

	public void perform(float delta) {
		/*debugDrawer.begin(camera);
		dynamicsWorld.debugDrawWorld();
		debugDrawer.end();*/
		
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
