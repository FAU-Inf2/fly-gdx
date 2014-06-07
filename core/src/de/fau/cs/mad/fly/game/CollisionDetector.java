package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.ICollisionListener;

public class CollisionDetector implements Disposable {

	final static short GROUND_FLAG = 1 << 8;
	final static short OBJECT_FLAG = 1 << 9;
	final static short ALL_FLAG = -1;
	boolean collisionFlag = false;

	private final Fly game;
	private int collisionCounter = 0;

	// constants to represent the begin of the userValues for specific types of collision objects
	public final static int USERVALUE_PLAYER = 0;
	public final static int USERVALUE_GATES = 100;
	public final static int USERVALUE_GATE_GOALS = 200;
	public final static int USERVALUE_MISC = 1000;
	
	// TODO: finish main listener who passes the events through the feature listeners

	class CollisionContactListener extends ContactListener {
		private ArrayList<ICollisionListener> listeners;
		
		private int collisionObj0 = -1;
		private int collisionObj1 = -1;
		
		public CollisionContactListener() {
			listeners = new ArrayList<ICollisionListener>();
		}
		
		public void addListener(ICollisionListener listener) {
			listeners.add(listener);
		}
		
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
							collisionFlag = true;
							
			/*Gdx.app.log("CollisionDetector.onContactAdded", "userValue0: "
					+ userValue0 + "; userValue1: " + userValue1
					+ "; partId0: " + partId0 + "; partId1: " + partId1
					+ "; index0:" + index0 + "; index1: " + index1);*/

			if (!(checkObj(userValue0) && checkObj(userValue1))) {
				collisionObj0 = userValue0;
				collisionObj1 = userValue1;
				collisionCounter++;
				
				for(ICollisionListener listener : listeners) {
					listener.listen(0, userValue0, userValue1);
				}
			}
			return true;
		}

		private boolean checkObj(int obj) {
			if (obj == collisionObj0 || obj == collisionObj1)
				return true;
			return false;
		}
	}
	

	/*class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
			collisionFlag = true;
			Gdx.app.log("CollisionDetector.onContactAdded",
					"collision happened!");
			Gdx.app.log("CollisionDetector.onContactAdded", "userValue0: "
					+ userValue0 + "; userValue1: " + userValue1
					+ "; partId0: " + partId0 + "; partId1: " + partId1
					+ "; index0:" + index0 + "; index1: " + index1);

			if (!(checkObj(userValue0) && checkObj(userValue1))) {
				collisionObj0 = userValue0;
				collisionObj1 = userValue1;
				collisionCounter++;
				collisionCounterLabel.setText("" + collisionCounter);
			}
			return true;
		}

		private boolean checkObj(int obj) {
			if (obj == collisionObj0 || obj == collisionObj1)
				return true;
			else
				return false;
		}
	}*/

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	CollisionContactListener contactListener;
	btBroadphaseInterface broadphase;
	btCollisionWorld collisionWorld;

	private GameController gameController;

	public CollisionDetector(final Fly game) {
		this.game = game;
		
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new CollisionContactListener();
	}

	public void init(GameController gameCon) {
		gameController = gameCon;
		collisionCounter = 0;
		Gdx.app.log("CollisionDetector", "Collision initialized");
	}

	public btCollisionObject createShape(int userValue,
			btCollisionShape shape, GameObject instance) {
		btCollisionObject collisionObject = instance.GetCollisionObject();// new
																			// btCollisionObject();
		collisionObject.setCollisionShape(shape);
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		collisionObject.setWorldTransform(instance.transform);
		collisionObject.setUserValue(userValue);

		collisionWorld.addCollisionObject(collisionObject, GROUND_FLAG,
				ALL_FLAG);

		Gdx.app.log("CollisionDetector", "Added shape: " + userValue);
		
		return collisionObject;
	}

	public btCollisionObject createConvexHull(int userValue, GameObject instance) {
		final Mesh mesh = instance.model.meshes.get(0);
		final btConvexHullShape hullShape = new btConvexHullShape(
				mesh.getVerticesBuffer(), mesh.getNumVertices(),
				mesh.getVertexSize());

		// now optimize the shape
		final btShapeHull hull = new btShapeHull(hullShape);
		hull.buildHull(hullShape.getMargin());
		final btConvexHullShape hullShapeFinal = new btConvexHullShape(hull);

		btCollisionObject collisionObject = instance.GetCollisionObject();
		collisionObject.setCollisionShape(hullShapeFinal);
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

		collisionObject.setWorldTransform(instance.transform);
		collisionObject.setUserValue(userValue);

		collisionWorld.addCollisionObject(collisionObject,
				OBJECT_FLAG, GROUND_FLAG);
		
		// delete the temporary shape
		hullShape.dispose();
		hull.dispose();
		
		Gdx.app.log("CollisionDetector", "Added convex hull: " + userValue);

		return collisionObject;
	}
	
	public CollisionContactListener getCollisionContactListener() {
		return contactListener;
	}

	public void perform() {
		// Gdx.app.log("collision.render", "camera:"
		// + gameController.getCamera().position.toString());
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
