package de.fau.cs.mad.fly.game;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.features.IFeatureDispose;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.features.IFeatureRender;

public class CollisionDetector implements IFeatureInit, IFeatureRender,
		IFeatureDispose {

	final static short GROUND_FLAG = 1 << 8;
	final static short OBJECT_FLAG = 1 << 9;
	final static short ALL_FLAG = -1;
	boolean collisionFlag = false;

	private final Fly game;
	private Stage gameStage;
	private Skin skin;
	private Label collisionCounterLabel;
	private int collisionCounter = 0;
	private int collisionObj0 = -1;
	private int collisionObj1 = -1;

	class MyContactListener extends ContactListener {
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
			
			if (!(checkObj(userValue0) && checkObj(userValue1)))
			{
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
	}

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	MyContactListener contactListener;
	btBroadphaseInterface broadphase;
	btCollisionWorld collisionWorld;

	private GameController gameController;

	public CollisionDetector(final Fly game, Stage stage) {
		this.game = game;
		gameStage = stage;
		skin = game.getSkin();
	}

	@Override
	public void init(GameController gameCon) {
		gameController = gameCon;
		InitCollision();

		collisionCounter = 0;

		//init collision stage
		LabelStyle labelStyle = new LabelStyle(skin.getFont("default-font"),
				Color.RED);
		Label label = new Label("Collision:", labelStyle);
		label.setPosition(game.getAbsoluteX(0.35f), game.getAbsoluteY(0.0f));
		gameStage.addActor(label);

		label = new Label(collisionCounter + "", labelStyle);
		label.setPosition(game.getAbsoluteX(0.55f), game.getAbsoluteY(0.0f));
		gameStage.addActor(label);
		collisionCounterLabel = label;

	}

	Array<btCollisionShape> collisionShapes = new Array<btCollisionShape>();
	Array<btCollisionObject> collisionObjects = new Array<btCollisionObject>();

	btCollisionObject spaceshipCollisionObject;
	ModelInstance spaceshipInstance;
	Model spaceshipModel;

	private void InitCollision() {

		Gdx.app.log("InitCollision", "collision init begin");
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase,
				collisionConfig);
		contactListener = new MyContactListener();

		// build the spaceship objects
		ModelBuilder modelBuilder = new ModelBuilder();
		spaceshipModel = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 2, 2,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		spaceshipInstance = new ModelInstance(spaceshipModel);
		spaceshipInstance.transform
				.setToTranslation(gameController.getCamera().position);

		// init and add the spaceship collision object to collision world
		btCollisionShape spaceshipShape;
		spaceshipShape = new btSphereShape(0.1f);
		spaceshipCollisionObject = new btCollisionObject();
		spaceshipCollisionObject.setCollisionShape(spaceshipShape);
		spaceshipCollisionObject.setCollisionFlags(spaceshipCollisionObject
				.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		spaceshipCollisionObject.setWorldTransform(spaceshipInstance.transform);
		Gdx.app.log("collision", "camera init:"
				+ gameController.getCamera().position.toString());
		spaceshipCollisionObject.setUserValue(0);

		collisionShapes.add(spaceshipShape);
		collisionObjects.add(spaceshipCollisionObject);

		collisionWorld.addCollisionObject(spaceshipCollisionObject,
				GROUND_FLAG, ALL_FLAG);

		// init and add each gates to the collision world
		List<ModelInstance> instances = gameController.getLevel().gateModels;
		for (int n = 0; n < instances.size(); n++) {

			final ModelInstance instance = instances.get(n);
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
			// Gdx.app.log("collision", "Gate" + n + ":" + instance.transform);
			hullObject.setUserValue(100 + n);

			collisionWorld.addCollisionObject(hullObject, OBJECT_FLAG,
					GROUND_FLAG);
			collisionShapes.add(hullShapeFinal);
			collisionObjects.add(hullObject);

			// delete the temporary shape
			hullShape.dispose();
			hull.dispose();
		}
		Gdx.app.log("InitCollision", "collision init end");
	}

	@Override
	public void render(float delta) {
		spaceshipInstance.transform
				.setToTranslation(gameController.getCamera().position);
		spaceshipCollisionObject.setWorldTransform(spaceshipInstance.transform);
		// Gdx.app.log("collision.render", "camera:"
		// + gameController.getCamera().position.toString());
		collisionWorld.performDiscreteCollisionDetection();
	}

	@Override
	public void dispose() {
		Gdx.app.log("Collision.dispose", "collision dispose");
		spaceshipModel.dispose();

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
