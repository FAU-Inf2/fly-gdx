package de.fau.cs.mad.fly.game;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.PHY_ScalarType;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.ICollisionListener;

public class CollisionDetector implements Disposable {
	
	CollisionShapeManager shapeManager;

	public final static short DUMMY_FLAG = 1 << 7;
	public final static short OBJECT_FLAG = 1 << 8;
	public final static short PLAYER_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;
	//public final static short ALL_WITHOUT_DUMMY_FLAG = -1;
	boolean collisionFlag = false;

	private final Fly game;

	// constants to represent the userValues for specific types of collision objects
	public final static int USERVALUE_PLAYER = 0;
	public final static int USERVALUE_GATES = 100;
	public final static int USERVALUE_GATE_GOALS = 200;
	public final static int USERVALUE_MISC = 1000;

	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;
		
		public CollisionContactListener() {
			listeners = new ArrayList<ICollisionListener>();
		}
		
		public void addListener(ICollisionListener listener) {
			listeners.add(listener);
		}
		
		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			collisionFlag = true;
							
			/*Gdx.app.log("CollisionDetector.onContactAdded", "userValue0: "
					+ userValue0 + "; userValue1: " + userValue1
					+ "; partId0: " + partId0 + "; partId1: " + partId1
					+ "; index0:" + index0 + "; index1: " + index1);*/

				
			for(ICollisionListener listener : listeners) {
				listener.listen(0, colObj0, colObj1);
			}
		}
	}

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	CollisionContactListener contactListener;
	btBroadphaseInterface broadphase;
	btCollisionWorld collisionWorld;
	DebugDrawer debugDrawer;

	private GameController gameController;

	public CollisionDetector(final Fly game) {
		this.game = game;
		shapeManager = new CollisionShapeManager();
		
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
		contactListener = new CollisionContactListener();
		
		debugDrawer = new DebugDrawer();
		collisionWorld.setDebugDrawer(debugDrawer);
		debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
	}

	public void load(GameController gameCon) {
		gameController = gameCon;
		Gdx.app.log("CollisionDetector", "Collision initialized");
	}
	
	public CollisionShapeManager getShapeManager() {
		return shapeManager;
	}

	public btCollisionObject createObject(GameObject instance, btCollisionShape shape, int userValue, Object userData) {
		return createObject(instance, shape, userValue, userData, OBJECT_FLAG, ALL_FLAG);
	}
	
	public btCollisionObject createObject(GameObject instance, btCollisionShape shape, int userValue, Object userData, short filterGroup, short filterMask) {
		btCollisionObject collisionObject = new btCollisionObject();
		
		collisionObject.setCollisionShape(shape);
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		collisionObject.setWorldTransform(instance.transform);
		collisionObject.setUserValue(userValue);
		collisionObject.userData = userData;

		collisionWorld.addCollisionObject(collisionObject, filterGroup, filterMask);

		return collisionObject;
	}
	
	public CollisionContactListener getCollisionContactListener() {
		return contactListener;
	}

	public void perform() {
		/*debugDrawer.begin(gameController.getCamera());
		collisionWorld.debugDrawWorld();
		debugDrawer.end();*/

		collisionWorld.performDiscreteCollisionDetection();
	}

	@Override
	public void dispose() {
		collisionWorld.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();
		contactListener.dispose();
		Gdx.app.log("CollisionDetector", "Collision disposed");
	}
}
