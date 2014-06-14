package de.fau.cs.mad.fly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import de.fau.cs.mad.fly.features.ICollisionListener;

import java.util.ArrayList;

public class CollisionDetector implements Disposable {

	CollisionShapeManager shapeManager;

	public final static short DUMMY_FLAG = 1 << 7;
	public final static short OBJECT_FLAG = 1 << 8;
	public final static short PLAYER_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;

	public static enum Types {
		Player, Gate, Goal, Other;

		public static Types get(int ord) { return values()[ord]; }
	}

	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;

		public CollisionContactListener() {
			listeners = new ArrayList<ICollisionListener>();
		}

		public void addListener(ICollisionListener listener) {
			listeners.add(listener);
		}

		@Override
		public void onContactStarted(btCollisionObject o0, btCollisionObject o1) {
			// Types t1 = Types.get(o0.getUserValue());
			// Types t2 = Types.get(o0.getUserValue());
			Gdx.app.log("CollisionDetector.onContactStarted", "o0.id = " + ((GameObject) o0.userData).id + ", o1.id = " + ((GameObject) o1.userData).id );
			for(ICollisionListener listener : listeners)
				listener.onCollision((GameObject) o0.userData, (GameObject) o1.userData);
		}

	}

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	CollisionContactListener contactListener;
	btBroadphaseInterface broadphase;
	btCollisionWorld collisionWorld;
	DebugDrawer debugDrawer;

	public CollisionDetector() {
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

	public CollisionShapeManager getShapeManager() {
		return shapeManager;
	}

	public static btCollisionObject createObject(GameObject instance, btCollisionShape shape, Types t, GameObject userData) {
		btCollisionObject collisionObject = new btCollisionObject();
		collisionObject.setCollisionShape(shape);
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		collisionObject.setWorldTransform(instance.transform);
		collisionObject.setUserValue(t.ordinal());
		collisionObject.userData = userData;

		return collisionObject;
	}

	public void addCollisionObject(GameObject o) {
		collisionWorld.addCollisionObject(o.getCollisionObject(), o.filterGroup, o.filterMask);
	}

	public CollisionContactListener getCollisionContactListener() {
		return contactListener;
	}

	public void perform() {
		/*debugDrawer.begin(gameController.getCamera());
-		collisionWorld.debugDrawWorld();
-		debugDrawer.end();*/
		
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
