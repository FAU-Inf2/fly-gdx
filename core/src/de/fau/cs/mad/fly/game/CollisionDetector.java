package de.fau.cs.mad.fly.game;

import java.util.List;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;

public class CollisionDetector implements IFeatureInit, IFeatureRender,
		IFeatureFinish {

	final static short GROUND_FLAG = 1 << 8;
	final static short OBJECT_FLAG = 1 << 9;
	final static short ALL_FLAG = -1;
	boolean collisionFlag = false;

	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0,
				int userValue1, int partId1, int index1) {
			collisionFlag = true;
			System.out.print("collision happened!");
			return true;
		}
	}

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	MyContactListener contactListener;
	btBroadphaseInterface broadphase;
	btCollisionWorld collisionWorld;

	private GameController gameController;

	public CollisionDetector() {

	}

	@Override
	public void init(GameController game) {
		gameController = game;
		InitCollision();
	}

	Array<btCollisionShape> collisionShapes = new Array<btCollisionShape>();
	Array<btCollisionObject> collisionObjects = new Array<btCollisionObject>();
	btCollisionObject playerCollisionObject;

	private void InitCollision() {

		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();

		btCollisionShape playerShape;
		playerShape = new btSphereShape(0.5f);
		playerCollisionObject = new btCollisionObject();
		playerCollisionObject.setCollisionShape(playerShape);
		playerCollisionObject.setCollisionFlags(playerCollisionObject
				.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		playerCollisionObject
				.setWorldTransform(gameController.getCamera().combined);
		collisionShapes.add(playerShape);
		collisionObjects.add(playerCollisionObject);

		collisionWorld.addCollisionObject(playerCollisionObject, GROUND_FLAG,
				ALL_FLAG);

		List<ModelInstance> instances = gameController.getLevel().gateModels;
		for (final ModelInstance instance : instances) {

			btCollisionObject hullObject;

			final Mesh mesh = instance.model.meshes.get(0);
			final btConvexHullShape hullShape = new btConvexHullShape(
					mesh.getVerticesBuffer(), mesh.getNumVertices(),
					mesh.getVertexSize());

			// now optimize the shape
			final btShapeHull hull = new btShapeHull(hullShape);
			hull.buildHull(hullShape.getMargin());
			final btConvexHullShape hullShapeFinal = new btConvexHullShape(hull);

			hullObject = new btCollisionObject();
			hullObject.setCollisionShape(hullShapeFinal);
			hullObject
					.setCollisionFlags(hullObject.getCollisionFlags()
							| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			hullObject.setWorldTransform(instance.transform);

			collisionWorld.addCollisionObject(playerCollisionObject,
					OBJECT_FLAG, GROUND_FLAG);
			collisionShapes.add(hullShapeFinal);
			collisionObjects.add(playerCollisionObject);

			// delete the temporary shape
			hullShape.dispose();
			hull.dispose();
		}
	}

	@Override
	public void render(float delta) {
		playerCollisionObject
				.setWorldTransform(gameController.getCamera().combined);
		collisionWorld.performDiscreteCollisionDetection();
	}

	@Override
	public void finish() {
		for (btCollisionShape obj : collisionShapes)
			obj.dispose();
		collisionShapes.clear();

		for (btCollisionObject obj : collisionObjects)
			obj.dispose();
		collisionObjects.clear();

		collisionWorld.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();
		contactListener.dispose();
	}
}
